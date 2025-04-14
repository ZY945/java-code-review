package com.web.rpc.example;

import com.web.rpc.client.RpcClientFactory;
import com.web.rpc.example.api.HelloService;
import com.web.rpc.example.api.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RPC客户端应用程序
 */
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);
    
    private final HelloService helloService;
    
    public ClientApplication(HelloService helloService) {
        this.helloService = helloService;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
    
    @Override
    public void run(String... args) {
        logger.info("RPC客户端启动，开始测试...");
        
        try {
            // 测试简单调用
            testSimpleCall();
            
            // 测试延迟调用
            testDelayedCall();
            
            // 测试批量调用
            testBatchCall();
            
            // 获取服务器信息
            testServerInfo();
            
        } catch (Exception e) {
            logger.error("RPC调用出错", e);
        }
    }
    
    private void testSimpleCall() {
        logger.info("测试简单调用...");
        String result = helloService.sayHello("World");
        logger.info("结果: {}", result);
    }
    
    private void testDelayedCall() {
        logger.info("测试延迟调用...");
        try {
            // 设置2秒延迟
            String result = helloService.helloWithDelay("Delayed World", 2000);
            logger.info("延迟调用结果: {}", result);
        } catch (Exception e) {
            logger.error("延迟调用失败", e);
        }
    }
    
    private void testBatchCall() {
        logger.info("测试批量调用...");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        List<String> results = helloService.batchHello(names);
        logger.info("批量调用结果: {}", results);
    }
    
    private void testServerInfo() {
        logger.info("获取服务器信息...");
        ServerInfo serverInfo = helloService.getServerInfo();
        logger.info("服务器信息: {}", serverInfo);
        logger.info("服务器运行时间: {} 秒", 
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - serverInfo.getStartTime()));
    }
    
    @Configuration
    public static class RpcConfig {
        
        @Bean
        public RpcClientFactory rpcClientFactory() {
            // 创建RPC客户端工厂
            return new RpcClientFactory("http://localhost:2379");
        }
        
        @Bean
        public HelloService helloService(RpcClientFactory clientFactory) {
            // 获取HelloService的代理对象
            return clientFactory.createService(HelloService.class);
        }
    }
}