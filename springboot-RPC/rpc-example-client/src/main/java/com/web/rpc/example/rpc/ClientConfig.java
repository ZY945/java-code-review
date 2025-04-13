package com.web.rpc.example.rpc;

import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.client.spring.RpcClientBeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan("com.web.rpc.example")
@Configuration
public class ClientConfig {

    @Bean
    public NettyClient nettyClient() throws InterruptedException {
        NettyClient client = new NettyClient("localhost", 8080);
        client.start();
        return client;
    }

    @Bean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(NettyClient nettyClient) {
        return new RpcClientBeanPostProcessor(nettyClient);
    }
}