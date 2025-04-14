package com.web.rpc.client.netty;

import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.codec.RpcDecoder;
import com.web.rpc.core.codec.RpcEncoder;
import com.web.rpc.core.constants.RpcConstants;
import com.web.rpc.core.protocol.MessageType;
import com.web.rpc.core.protocol.RpcMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Netty客户端
 * 负责与服务器建立连接并发送RPC请求
 */
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private Channel channel;
    private final ConcurrentHashMap<Long, CompletableFuture<RpcResponse>> responseFutures = new ConcurrentHashMap<>();
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> heartbeatFuture;
    
    // 连接超时时间（毫秒），默认5秒
    private int connectTimeout = 5000;
    
    // 请求超时时间（毫秒），默认10秒
    private int requestTimeout = 10000;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    /**
     * 设置连接超时时间
     * 
     * @param connectTimeout 连接超时时间（毫秒）
     * @return this
     */
    public NettyClient setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }
    
    /**
     * 设置请求超时时间
     *
     * @param requestTimeout 请求超时时间（毫秒）
     */
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    /**
     * 启动客户端并连接服务器
     */
    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                // 心跳检测
                                .addLast(new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS))
                                // 编解码器
                                .addLast(new RpcDecoder())
                                .addLast(new RpcEncoder())
                                // 响应处理器
                                .addLast(new SimpleChannelInboundHandler<RpcMessage>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) {
                                        handleResponse(msg);
                                    }

                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        if (evt instanceof io.netty.handler.timeout.IdleStateEvent) {
                                            sendHeartbeat();
                                        } else {
                                            super.userEventTriggered(ctx, evt);
                                        }
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                        logger.error("Client exception: ", cause);
                                        ctx.close();
                                    }
                                });
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel();
        logger.info("Netty client connected to {}:{}", host, port);
    }

    /**
     * 重连服务器
     */
    private void reconnect() throws InterruptedException {
        if (channel != null) {
            channel.close();
        }
        start();
        logger.info("Reconnected to {}:{}", host, port);

        // 重新启动心跳
        startHeartbeat();
    }

    /**
     * 处理响应消息
     */
    private void handleResponse(RpcMessage message) {
        long requestId = message.getRequestId();
        logger.debug("Received message type: {}, requestId: {}", message.getMessageType(), requestId);

        // 处理心跳响应
        if (message.getMessageType() == MessageType.HEARTBEAT_RESPONSE) {
            logger.debug("Received heartbeat response");
            return;
        }

        // 处理RPC响应
        if (message.getMessageType() == MessageType.RESPONSE) {
            CompletableFuture<RpcResponse> future = responseFutures.remove(requestId);
            if (future != null) {
                RpcResponse response = (RpcResponse) message.getData();
                future.complete(response);
            } else {
                logger.warn("Received response for unknown request: {}", requestId);
            }
        }
    }

    /**
     * 发送心跳包
     */
    private void sendHeartbeat() {
        if (isChannelActive()) {
            RpcMessage heartbeat = RpcMessage.createHeartbeat(true);
            heartbeat.setSerializationType(RpcConstants.SerializationType.JSON);
            heartbeat.setCompressionType(RpcConstants.CompressType.NONE);
            channel.writeAndFlush(heartbeat).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.error("Failed to send heartbeat", future.cause());
                } else {
                    logger.debug("Sent heartbeat to server");
                }
            });
        }
    }

    /**
     * 启动心跳定时器
     */
    private void startHeartbeat() {
        stopHeartbeat();
        heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(
                this::sendHeartbeat,
                0,
                RpcConstants.HEARTBEAT_INTERVAL / 2,
                TimeUnit.MILLISECONDS);
    }

    private boolean isChannelActive() {
        return channel != null && channel.isActive();
    }

    /**
     * 发送RPC请求
     */
    public CompletableFuture<RpcResponse> sendRequest(RpcMessage message) {
        RpcRequest request = (RpcRequest) message.getData();
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        responseFutures.put(message.getRequestId(), future);

        try {
            if (!isChannelActive()) {
                logger.warn("Channel is inactive, attempting to reconnect...");
                reconnect();
            }

            logger.info("Sending request: {} method: {}", message.getRequestId(), request.getMethodName());
            channel.writeAndFlush(message).addListener((ChannelFutureListener) f -> {
                if (!f.isSuccess()) {
                    logger.error("Failed to send request: {}", f.cause().getMessage());
                    future.completeExceptionally(f.cause());
                    responseFutures.remove(message.getRequestId());
                } else {
                    logger.debug("Request sent successfully: {}", message.getRequestId());
                }
            });
            
            // 添加请求超时处理
            if (requestTimeout > 0) {
                // 使用调度线程池来处理超时
                heartbeatExecutor.schedule(() -> {
                    // 如果请求还没有完成，则标记为超时
                    if (!future.isDone()) {
                        responseFutures.remove(message.getRequestId());
                        future.completeExceptionally(
                            new TimeoutException("Request timeout after " + requestTimeout + " ms, requestId: " + message.getRequestId())
                        );
                        logger.warn("Request timed out after {} ms, requestId: {}", requestTimeout, message.getRequestId());
                    }
                }, requestTimeout, TimeUnit.MILLISECONDS);
            }

        } catch (Exception e) {
            logger.error("Error sending request", e);
            responseFutures.remove(message.getRequestId());
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 停止心跳定时器
     */
    private void stopHeartbeat() {
        if (heartbeatFuture != null && !heartbeatFuture.isCancelled()) {
            heartbeatFuture.cancel(true);
            heartbeatFuture = null;
        }
    }

    /**
     * 停止客户端
     */
    public void stop() {
        stopHeartbeat();
        heartbeatExecutor.shutdownNow();

        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
        logger.info("Netty client stopped");
    }
}