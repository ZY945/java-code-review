package com.web.rpc.example;

import com.web.rpc.server.spring.ServerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ServerApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerConfig.class);
        context.start();
        System.out.println("RPC Server started on port 8080");
    }
}