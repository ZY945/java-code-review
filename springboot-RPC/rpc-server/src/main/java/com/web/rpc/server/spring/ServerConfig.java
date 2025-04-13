package com.web.rpc.server.spring;

import com.web.rpc.server.netty.NettyServer;
import com.web.rpc.server.registry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    public ServiceRegistry serviceRegistry() {
        return new ServiceRegistry();
    }

    @Bean
    public RpcServiceBeanPostProcessor rpcServiceBeanPostProcessor() {
        return new RpcServiceBeanPostProcessor();
    }

    @Bean
    public NettyServer nettyServer(RpcServiceBeanPostProcessor postProcessor) throws InterruptedException {
        NettyServer server = new NettyServer(8080, postProcessor.getServiceMap());
        server.start();
        return server;
    }
}