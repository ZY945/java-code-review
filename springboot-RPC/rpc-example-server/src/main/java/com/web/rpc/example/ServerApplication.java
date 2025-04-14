package com.web.rpc.example;

import com.web.rpc.server.RpcServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * RPC服务端应用程序
 * 使用RpcServerBootstrap构造器来创建和管理RPC服务器
 */
@SpringBootApplication
public class ServerApplication {
    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    // 硬编码配置项
    public static final int SERVER_PORT = 8080;
    public static final String ETCD_ENDPOINT = "http://localhost:2379";
    public static final String applicationName = "rpc-example-server";
    public static final String version = "1.0.0";

    private static RpcServerBootstrap rpcServerBootstrap;

    public static void main(String[] args) {
        // 启动Spring应用，获取Spring上下文
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ServerApplication.class, args);

        try {
            // 创建并初始化RpcServerBootstrap
            rpcServerBootstrap = new RpcServerBootstrap(SERVER_PORT, ETCD_ENDPOINT);
            // 设置Spring上下文，用于扫描@RpcService注解的Bean
            rpcServerBootstrap.setApplicationContext(applicationContext);
            // 初始化服务器，扫描服务并启动Netty服务器
            rpcServerBootstrap.afterPropertiesSet();

            // 添加关闭钩子，确保应用关闭时能够正确停止RPC服务器
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (rpcServerBootstrap != null) {
                        rpcServerBootstrap.destroy();
                        logger.info("RPC服务器已关闭");
                    }
                    applicationContext.close();
                } catch (Exception e) {
                    logger.error("RPC服务器关闭失败", e);
                }
            }));

            logger.info("RPC服务端已启动，等待客户端连接...");
            logger.info("RPC服务器监听端口: {}", SERVER_PORT);
            logger.info("ETCD服务注册中心: {}", ETCD_ENDPOINT);
        } catch (Exception e) {
            logger.error("RPC服务器启动失败", e);
            // 关闭Spring应用
            applicationContext.close();
            System.exit(1);
        }
    }
}