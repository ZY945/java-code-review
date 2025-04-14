package com.web.rpc.client.spring;

import com.web.rpc.client.core.RpcClientCore;
import com.web.rpc.client.processor.RpcClientBeanPostProcessor;
import com.web.rpc.core.registry.EtcdServiceRegistry;
import com.web.rpc.core.registry.ServiceRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RpcClientProperties.class, EtcdRegistryProperties.class})
public class RpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public String[] etcdEndpoints(EtcdRegistryProperties properties) {
        // 构建ETCD端点数组
        String endpoint = "http://" + properties.getHost() + ":" + properties.getPort();
        return new String[]{endpoint};
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientCore rpcClientCore(String[] etcdEndpoints, RpcClientProperties properties) {
        return new RpcClientCore(
            properties.getHost(),
            properties.getPort(),
            etcdEndpoints,
            properties.getServiceInterface(),
            properties.getVersion()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(RpcClientCore rpcClientCore) {
        return new RpcClientBeanPostProcessor(rpcClientCore.getServiceDiscovery());
    }
}