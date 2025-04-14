package com.web.rpc.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RPC服务端应用程序
 */
@SpringBootApplication
public class ServerApplication {
    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        logger.info("RPC服务端已启动，等待客户端连接...");
    }
}