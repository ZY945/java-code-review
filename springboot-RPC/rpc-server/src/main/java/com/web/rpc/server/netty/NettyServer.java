package com.web.rpc.server.netty;

import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.serialize.JsonSerializer;
import com.web.rpc.core.serialize.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final int port;
    private final Map<String, Object> serviceMap;
    private final Serializer serializer = new JsonSerializer();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public NettyServer(int port, Map<String, Object> serviceMap) {
        this.port = port;
        this.serviceMap = serviceMap;
    }

    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(new SimpleChannelInboundHandler<byte[]>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
                                        RpcRequest request = serializer.deserialize(msg, RpcRequest.class);
                                        RpcResponse response = handleRequest(request);
                                        ctx.writeAndFlush(serializer.serialize(response));
                                    }
                                });
                    }
                });

        bootstrap.bind(port).sync();
        logger.info("Netty server started on port {}", port);
    }

    private RpcResponse handleRequest(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try {
            String serviceName = request.getServiceName();
            Object service = serviceMap.get(serviceName);
            if (service == null) {
                throw new RuntimeException("Service not found: " + serviceName);
            }

            java.lang.reflect.Method method = service.getClass().getMethod(
                    request.getMethodName(), request.getParameterTypes());
            Object result = method.invoke(service, request.getParameters());
            response.setResult(result);
        } catch (Exception e) {
            logger.error("Failed to handle request: {}", request, e);
            response.setError(e);
        }

        return response;
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("Netty server stopped");
    }
}