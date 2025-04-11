package com.dongfeng.springboot.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteNettyServer implements RemoteServer {

    private boolean started = false;

    //上面注意，boss线程一般设置一个线程，设置多个也只会用到一个，而且多个目前没有应用场景，
    // worker线程通常要根据服务器调优，如果不写默认就是cpu的两倍。
    EventLoopGroup bossGroup; // boss专门用来接收连接，可以理解为处理accept事件，
    EventLoopGroup workerGroup; // worker，可以关注除了accept之外的其它事件，处理子任务。
    ServerBootstrap bootstrap; // 服务启动类

    public RemoteNettyServer(int port) {
        // 初始化Netty的线程组和ServerBootstrap
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        this.bootstrap = new ServerBootstrap();

        this.bootstrap.group(bossGroup, workerGroup)
                .channel(io.netty.channel.socket.nio.NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        // 在这里可以添加处理器,添加handler，也就是具体的IO事件处理器
                        channel.pipeline().addLast();
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("Netty server started on port: {}", port);
            this.started = true;
            channelFuture.channel().closeFuture().sync();
            log.info("等待服务端监听端口关闭");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public void start() {
        // 启动远程服务的逻辑
        System.out.println("Starting remote netty server...");
        started = true;
    }

    @Override
    public void stop() {
        // 停止远程服务的逻辑
        System.out.println("Stopping remote netty server...");
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
