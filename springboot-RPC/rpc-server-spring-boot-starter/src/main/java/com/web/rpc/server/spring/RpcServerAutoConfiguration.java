package com.web.rpc.server.spring;

import com.web.rpc.core.registry.EtcdServiceRegistry;
import com.web.rpc.core.registry.ServiceRegistry;
import com.web.rpc.server.RpcServerBootstrap;
import com.web.rpc.server.netty.NettyServer;
import com.web.rpc.server.service.RpcServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@EnableConfigurationProperties({RpcServerProperties.class, EtcdRegistryProperties.class})
public class RpcServerAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 获取本机IP地址
     */
    private String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * 配置服务注册中心
     */
    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistry serviceRegistry(EtcdRegistryProperties properties) {
        String host = properties.getHost();
        String port = properties.getPort();
        String endpoint = "http://" + host + ":" + port;
        return new EtcdServiceRegistry(endpoint);
    }

    /**
     * 配置服务管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public RpcServiceManager rpcServiceManager(ServiceRegistry serviceRegistry, RpcServerProperties properties) {
        String host = properties.getHost() != null ? properties.getHost() : getLocalHost();
        int port = properties.getPort();
        return new RpcServiceManager(serviceRegistry, host, port);
    }

    /**
     * 配置RPC服务器引导类
     */
    @Bean(initMethod = "afterPropertiesSet", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public RpcServerBootstrap rpcServerBootstrap(RpcServerProperties properties,EtcdRegistryProperties etcdRegistryProperties) throws Exception {
        String host = properties.getHost() != null ? properties.getHost() : getLocalHost();
        int port = properties.getPort();
        
        // 创建并配置RPC服务器引导类
        RpcServerBootstrap bootstrap = new RpcServerBootstrap(host,port, "http://" + etcdRegistryProperties.getHost() + ":" + etcdRegistryProperties.getPort());
        bootstrap.setApplicationContext(applicationContext);
        return bootstrap;
    }

    /**
     * 配置Netty服务器
     * 注意：通常不需要直接创建NettyServer，因为RpcServerBootstrap会创建并管理它
     * 这里保留是为了兼容性
     */
    @Bean
    @ConditionalOnMissingBean
    public NettyServer nettyServer(RpcServerProperties properties, ServiceRegistry serviceRegistry, RpcServiceManager serviceManager) {
        String host = properties.getHost() != null ? properties.getHost() : getLocalHost();
        int port = properties.getPort();
        return new NettyServer(host, port, serviceManager.getServiceMap(), serviceRegistry);
    }
}