package com.web.rpc.server.netty;

import com.web.rpc.core.codec.RpcDecoder;
import com.web.rpc.core.codec.RpcEncoder;
import com.web.rpc.core.registry.ServiceRegistry;
import com.web.rpc.server.netty.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Netty服务器
 * 负责启动和管理RPC服务器，处理服务注册和请求分发
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final int port;
    private final String host;
    private final Map<String, Object> serviceMap;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ServiceRegistry serviceRegistry;

    public NettyServer(String host, int port, Map<String, Object> serviceMap, ServiceRegistry serviceRegistry) {
        this.host = host;
        this.port = port;
        this.serviceMap = serviceMap;
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * 启动Netty服务器
     */
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        logger.info("New client connected: {}", ch.remoteAddress());
                        ch.pipeline()
                                // 空闲检测，30秒没有读取到数据就关闭连接
                                .addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                // 解码器和编码器
                                .addLast(new RpcDecoder())
                                .addLast(new RpcEncoder())
                                // 业务处理器
                                .addLast(new RpcRequestHandler(serviceMap));
                    }
                });


        bootstrap.bind(host, port).sync();
        logger.info("Netty server started on {}:{}", host, port);

        // 注册所有服务到ETCD
        registerServices();
    }


    /**
     * 注册服务到注册中心
     */
    private void registerServices() {
        for (Map.Entry<String, Object> entry : serviceMap.entrySet()) {
            String serviceName = entry.getKey();
            try {
                serviceRegistry.register(serviceName, host, port);
                logger.info("Registered service: {} at {}:{}", serviceName, host, port);
            } catch (Exception e) {
                logger.error("Failed to register service: " + serviceName, e);
                throw new RuntimeException("Failed to register service: " + serviceName, e);
            }
        }
    }

    /**
     * 停止服务器并注销服务
     */
    public void stop() {
        // 注销所有服务
        for (String serviceName : serviceMap.keySet()) {
            try {
                serviceRegistry.unregister(serviceName, host, port);
                logger.info("Unregistered service: {} from {}:{}", serviceName, host, port);
            } catch (Exception e) {
                logger.error("Failed to unregister service: " + serviceName, e);
            }
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("Netty server stopped");
    }
}