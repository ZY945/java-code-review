package com.web.rpc.server;

import com.web.rpc.core.annotation.RpcService;
import com.web.rpc.core.registry.EtcdServiceRegistry;
import com.web.rpc.core.registry.ServiceRegistry;
import com.web.rpc.server.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class RpcServerBootstrap implements InitializingBean, DisposableBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerBootstrap.class);

    private final String host;
    private final int port;
    private final String[] etcdEndpoints;
    private ApplicationContext applicationContext;
    private NettyServer nettyServer;
    private ServiceRegistry serviceRegistry;

    public RpcServerBootstrap(int port, String... etcdEndpoints) throws Exception {
        this.host = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
        this.etcdEndpoints = etcdEndpoints;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化服务注册中心
        serviceRegistry = new EtcdServiceRegistry(etcdEndpoints);

        // 扫描和注册服务
        Map<String, Object> serviceMap = new HashMap<>();
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(RpcService.class);
        
        for (String beanName : beanNames) {
            Object serviceBean = applicationContext.getBean(beanName);
            RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
            Class<?>[] interfaces = serviceBean.getClass().getInterfaces();
            
            if (interfaces.length == 0) {
                throw new RuntimeException("RPC service must implement an interface: " + serviceBean.getClass());
            }
            
            // 使用接口名和版本号作为服务名
            String serviceName = interfaces[0].getName() + "#" + rpcService.version();
            serviceMap.put(serviceName, serviceBean);
            
            // 注册服务到注册中心
            serviceRegistry.register(serviceName, host, port);
            logger.info("Registered service: {} at {}:{}", serviceName, host, port);
        }

        // 启动Netty服务器
        nettyServer = new NettyServer(host, port, serviceMap, serviceRegistry);
        nettyServer.start();
        logger.info("RPC Server started on {}:{}", host, port);
    }

    @Override
    public void destroy() throws Exception {
        if (nettyServer != null) {
            nettyServer.stop();
        }
        if (serviceRegistry != null) {
            serviceRegistry.close();
        }
        logger.info("RPC Server stopped");
    }
}