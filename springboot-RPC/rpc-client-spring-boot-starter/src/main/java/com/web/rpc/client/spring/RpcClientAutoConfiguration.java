package com.web.rpc.client.spring;

import com.web.rpc.client.core.RpcClientCore;
import com.web.rpc.client.discovery.EtcdServiceDiscovery;
import com.web.rpc.client.discovery.ServiceDiscovery;
import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.client.processor.RpcClientBeanPostProcessor;
import com.web.rpc.client.proxy.RpcProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RPC客户端自动配置类
 */
@Configuration
@EnableConfigurationProperties({RpcClientProperties.class,EtcdRegistryProperties.class})
public class RpcClientAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientAutoConfiguration.class);

    
    private RpcClientCore rpcClientCore;
    
    /**
     * 配置ETCD服务端点
     */
    @Bean(name = "etcdEndpoints")
    @ConditionalOnMissingBean(name = "etcdEndpoints")
    public String[] createEtcdEndpoints(EtcdRegistryProperties properties) {
        // 构建ETCD端点数组
        String endpoint = "http://" + properties.getHost() + ":" + properties.getPort();
        logger.info("Configuring ETCD endpoint: {}", endpoint);
        return new String[]{endpoint};
    }
    
    /**
     * 配置服务发现组件
     */
    @Bean
    @ConditionalOnMissingBean
    public ServiceDiscovery serviceDiscovery(@Qualifier("etcdEndpoints") String[] etcdEndpoints) {
        logger.info("Initializing ETCD service discovery with endpoints: {}", String.join(", ", etcdEndpoints));
        return new EtcdServiceDiscovery(etcdEndpoints);
    }
    
    /**
     * 配置Netty客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public NettyClient nettyClient(RpcClientProperties properties) {
        logger.info("Initializing Netty client connecting to {}:{} with connectTimeout={}, requestTimeout={}", 
                properties.getHost(), properties.getPort(), 
                properties.getConnectTimeout(), properties.getRequestTimeout());
        
        // 创建Netty客户端实例并配置超时
        NettyClient client = new NettyClient(properties.getHost(), properties.getPort());
        client.setConnectTimeout(properties.getConnectTimeout());
        client.setRequestTimeout(properties.getRequestTimeout());
        return client;
    }
    
    /**
     * 配置RPC客户端核心
     * 注意：这个方法只在明确指定serviceInterface时才会被调用
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "serviceInterface")
    public RpcClientCore rpcClientCore(NettyClient nettyClient, ServiceDiscovery serviceDiscovery, RpcClientProperties properties) {
        // 确保serviceInterface不为null
        if (properties.getServiceInterface() == null) {
            logger.warn("Cannot create RpcClientCore: serviceInterface is null");
            throw new IllegalArgumentException("serviceInterface must not be null");
        }
        
        logger.info("Initializing RPC client core for service: {}, version: {}", 
                properties.getServiceInterface().getName(), 
                properties.getVersion());
        
        // 使用新的构造函数创建RpcClientCore实例
        // 直接传入已经创建的ServiceDiscovery实例
        // 这样可以避免重复创建ETCD连接
        logger.info("Using existing ServiceDiscovery instance for RpcClientCore");
        RpcClientCore core = new RpcClientCore(
                properties.getHost(),
                properties.getPort(),
                serviceDiscovery,  // 使用已经创建的ServiceDiscovery实例
                properties.getServiceInterface(),
                properties.getVersion()
        );
        
        this.rpcClientCore = core;
        return core;
    }
    
    /**
     * 配置用于注解式引用的RPC客户端核心
     * 这个方法在没有明确指定serviceInterface时使用
     */
    @Bean(name = "annotationRpcClientCore", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = "rpcClientCore")
    public RpcClientCore annotationRpcClientCore(NettyClient nettyClient, ServiceDiscovery serviceDiscovery, RpcClientProperties properties) {
        logger.info("Initializing annotation-based RPC client core");
        
        // 创建一个特殊的RpcClientCore实例，用于注解式引用
        // 这里我们使用Object.class作为一个占位符，因为实际的服务接口将在注解处理时指定
        RpcClientCore core = new RpcClientCore(
                properties.getHost(),
                properties.getPort(),
                serviceDiscovery,
                Object.class,  // 占位符，实际的服务接口将在注解处理时指定
                properties.getVersion()
        );
        
        this.rpcClientCore = core;
        return core;
    }
    
    /**
     * 配置RPC代理
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "serviceInterface")
    public RpcProxy rpcProxy(RpcClientCore rpcClientCore) {
        return rpcClientCore.getRpcProxy();
    }

    /**
     * 配置RPC客户端后置处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(ServiceDiscovery serviceDiscovery) {
        logger.info("Initializing RPC client bean post processor");
        return new RpcClientBeanPostProcessor(serviceDiscovery);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("RPC client auto-configuration initialized");
    }
    
    @Override
    public void destroy() throws Exception {
        if (rpcClientCore != null) {
            logger.info("Shutting down RPC client core");
            rpcClientCore.shutdown();
        }
    }
}