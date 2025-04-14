package com.web.rpc.server.spring;

import com.web.rpc.core.registry.EtcdServiceRegistry;
import com.web.rpc.core.registry.ServiceRegistry;
import com.web.rpc.server.netty.NettyServer;
import com.web.rpc.server.processor.RpcServiceBeanPostProcessor;
import com.web.rpc.server.registry.ServerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@EnableConfigurationProperties({RpcServerProperties.class, EtcdRegistryProperties.class})
public class RpcServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistry serviceRegistry(EtcdRegistryProperties properties) {
        return new EtcdServiceRegistry(properties.getHost(),properties.getPort());
    }

    @Bean
    @ConditionalOnMissingBean
    public NettyServer nettyServer(RpcServerProperties properties,ServiceRegistry serviceDiscovery) {
        return new NettyServer(properties.getHost(),properties.getPort(),new HashMap<>(),serviceDiscovery);
    }


    @Bean
    @ConditionalOnMissingBean
    public RpcServiceBeanPostProcessor rpcServiceBeanPostProcessor(ServiceRegistry serviceDiscovery) {
        return new RpcServiceBeanPostProcessor(new ServerRegistry(serviceDiscovery));
    }
}