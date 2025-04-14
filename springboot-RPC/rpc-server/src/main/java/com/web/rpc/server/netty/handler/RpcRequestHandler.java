package com.web.rpc.server.netty.handler;

import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.constants.RpcConstants;
import com.web.rpc.core.protocol.MessageType;
import com.web.rpc.core.protocol.RpcMessage;
import com.web.rpc.core.protocol.RpcStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * RPC请求处理器
 * 处理RPC请求和心跳消息
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);
    private final Map<String, Object> serviceMap;
    private final ExecutorService executorService;

    public RpcRequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
        // 创建线程池，线程数为CPU核心数
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage message) {
        try {
            // 处理心跳请求
            if (message.getMessageType() == MessageType.HEARTBEAT_REQUEST) {
                handleHeartbeat(ctx, message);
                return;
            }

            // 处理RPC请求
            if (message.getMessageType() == MessageType.REQUEST) {
                handleRpcRequest(ctx, message);
            }
        } catch (Exception e) {
            logger.error("Error processing message: {}", message, e);
            // 发送错误响应
            RpcResponse errorResponse = new RpcResponse();
            errorResponse.setStatus(RpcStatus.INTERNAL_ERROR);
            errorResponse.setErrorMessage("Server error: " + e.getMessage());

            RpcMessage responseMessage = RpcMessage.createResponse(message.getRequestId(), errorResponse);
            responseMessage.setCompressionType(message.getCompressionType());
            responseMessage.setSerializationType(message.getSerializationType());

            ctx.writeAndFlush(responseMessage);
        }
    }

    /**
     * 处理心跳请求
     */
    private void handleHeartbeat(ChannelHandlerContext ctx, RpcMessage message) {
        logger.debug("Received heartbeat request from {}", ctx.channel().remoteAddress());
        RpcMessage heartbeatResponse = RpcMessage.createHeartbeat(false);
        heartbeatResponse.setRequestId(message.getRequestId());
        heartbeatResponse.setCompressionType(RpcConstants.CompressType.GZIP);
        heartbeatResponse.setSerializationType(message.getSerializationType());
        ctx.writeAndFlush(heartbeatResponse);
    }

    /**
     * 处理RPC请求
     */
    private void handleRpcRequest(ChannelHandlerContext ctx, RpcMessage message) {
        RpcRequest request = (RpcRequest) message.getData();

        // 如果设置了超时，使用带超时的CompletableFuture
        CompletableFuture<RpcResponse> future = CompletableFuture.supplyAsync(
                () -> handleRequest(request),
                executorService
        );

        // 超时处理
        if (request.getTimeout() > 0) {
            // Java 9及以上版本才支持completeOnTimeout，这里使用自定义实现
            final CompletableFuture<RpcResponse> timeoutFuture = future;
            java.util.Timer timer = new java.util.Timer(true);
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (!timeoutFuture.isDone()) {
                        timeoutFuture.complete(createTimeoutResponse(request));
                    }
                }
            }, request.getTimeUnit() != null ?
                    request.getTimeUnit().toMillis(request.getTimeout()) :
                    TimeUnit.MILLISECONDS.toMillis(request.getTimeout()));
        }

        future.thenAccept(response -> {
            // 构造响应消息
            RpcMessage responseMessage = RpcMessage.createResponse(message.getRequestId(), response);
            responseMessage.setCompressionType(message.getCompressionType());
            responseMessage.setSerializationType(message.getSerializationType());

            ctx.writeAndFlush(responseMessage);
        });
    }

    /**
     * 创建超时响应
     */
    private RpcResponse createTimeoutResponse(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        response.setStatus(RpcStatus.TIMEOUT);
        response.setErrorMessage("Request timed out after " + request.getTimeout() + " " + request.getTimeUnit());
        return response;
    }

    /**
     * 处理RPC请求并返回响应
     */
    private RpcResponse handleRequest(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        response.setTimestamp(System.currentTimeMillis());

        try {
            // 构建服务名称，格式为：接口名#版本号
            String serviceName = request.getServiceName() + "#" + request.getVersion();
            logger.info("Handling request: {} for service: {}", request.getRequestId(), serviceName);

            // 获取服务实例
            Object service = serviceMap.get(serviceName);
            if (service == null) {
                String error = String.format("Service not found: %s (version: %s)",
                        request.getServiceName(), request.getVersion());
                logger.error(error);
                logger.error("Available services: {}", serviceMap.keySet());
                response.setStatus(RpcStatus.NOT_FOUND);
                response.setErrorMessage(error);
                return response;
            }

            // 获取并调用方法
            Method method = service.getClass().getMethod(
                    request.getMethodName(),
                    request.getParameterTypes()
            );
            logger.debug("Invoking method: {} on service: {}", method.getName(), serviceName);

            // 调用服务方法
            Object result = method.invoke(service, request.getParameters());
            response.setStatus(RpcStatus.SUCCESS);
            response.setResult(result);
            logger.info("Successfully handled request: {}", request.getRequestId());

        } catch (NoSuchMethodException e) {
            logger.error("Method not found: {}", request.getMethodName(), e);
            response.setStatus(RpcStatus.NOT_FOUND);
            response.setErrorMessage("Method not found: " + request.getMethodName());
        } catch (Exception e) {
            logger.error("Failed to handle request: {}", request, e);
            response.setStatus(RpcStatus.INTERNAL_ERROR);
            response.setErrorMessage(e.getMessage());

            // 在开发环境下返回详细异常信息
            if (System.getProperty("rpc.env", "prod").equals("dev")) {
                response.setError(e);
            }
        }

        return response;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.info("Idle check, close connection: {}", ctx.channel().remoteAddress());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("RpcRequestHandler error for client: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("Client disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        // 只有当所有连接都断开时才关闭线程池
        if (ctx.channel().parent().isOpen()) {
            executorService.shutdown();
            logger.info("All connections closed, shutting down executor service");
        }
    }
}