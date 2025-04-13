package com.web.rpc.client.netty;

import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.serialize.JsonSerializer;
import com.web.rpc.core.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private Channel channel;
    private final Serializer serializer = new JsonSerializer();
    private final ConcurrentHashMap<String, CompletableFuture<RpcResponse>> responseFutures = new ConcurrentHashMap<>();
    private final EventLoopGroup group = new NioEventLoopGroup();

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(new NettyClientEncoder(serializer))
                                .addLast(new SimpleChannelInboundHandler<byte[]>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
                                        RpcResponse response = serializer.deserialize(msg, RpcResponse.class);
                                        CompletableFuture<RpcResponse> future = responseFutures.remove(response.getRequestId());
                                        if (future != null) {
                                            future.complete(response);
                                        }
                                    }
                                });
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel();
        logger.info("Netty client connected to {}:{}", host, port);
    }

    private void reconnect() throws InterruptedException {
        if (channel != null) {
            channel.close();
        }
        start();
        logger.info("Reconnected to {}:{}", host, port);
    }

    private boolean isChannelActive() {
        return channel != null && channel.isActive();
    }

    public RpcResponse sendRequest(RpcRequest request) throws Exception {
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        responseFutures.put(request.getRequestId(), future);

        int maxRetries = 3;
        int currentRetry = 0;
        Exception lastException = null;

        while (currentRetry < maxRetries) {
            try {
                if (!isChannelActive()) {
                    logger.warn("Channel is inactive, attempting to reconnect...");
                    reconnect();
                }

                ChannelFuture writeFuture = channel.writeAndFlush(request);
                CompletableFuture<RpcResponse> finalFuture = future;
                writeFuture.addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        finalFuture.completeExceptionally(f.cause());
                        responseFutures.remove(request.getRequestId());
                    }
                });

                RpcResponse response = future.get(15, TimeUnit.SECONDS);
                if (response.getError() != null) {
                    throw new Exception(response.getError());
                }
                return response;

            } catch (TimeoutException e) {
                lastException = e;
                logger.warn("Request timeout, retry {} of {}", currentRetry + 1, maxRetries);
                currentRetry++;
                if (currentRetry >= maxRetries) {
                    break;
                }
                responseFutures.remove(request.getRequestId());
                future = new CompletableFuture<>();
                responseFutures.put(request.getRequestId(), future);
                Thread.sleep(1000); // 重试前等待1秒
            } catch (Exception e) {
                logger.error("Error during RPC call", e);
                throw e;
            }
        }
        
        throw new TimeoutException("Request failed after " + maxRetries + " retries: " + lastException.getMessage());
    }

    public void stop() {
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
        logger.info("Netty client stopped");
    }
}