package com.web.rpc.client.processor;

import com.web.rpc.client.discovery.ServiceDiscovery;
import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.client.proxy.RpcProxy;
import com.web.rpc.core.annotation.RpcReference;
import com.web.rpc.core.registry.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClientBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientBeanPostProcessor.class);
    private final ServiceDiscovery serviceDiscovery;
    private final Map<String, NettyClient> clientMap = new ConcurrentHashMap<>();

    public RpcClientBeanPostProcessor(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference reference = field.getAnnotation(RpcReference.class);
                Class<?> interfaceClass = field.getType();
                
                try {
                    field.setAccessible(true);
                    // 发现服务
                    ServiceInfo serviceInfo = serviceDiscovery.discover(interfaceClass.getName());
                    if (serviceInfo == null) {
                        throw new RuntimeException("Service not found: " + interfaceClass.getName());
                    }

                    // 获取或创建NettyClient
                    String clientKey = serviceInfo.getHost() + ":" + serviceInfo.getPort();
                    NettyClient client = clientMap.computeIfAbsent(clientKey, k -> {
                        try {
                            NettyClient newClient = new NettyClient(serviceInfo.getHost(), serviceInfo.getPort());
                            newClient.start();
                            return newClient;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to create NettyClient", e);
                        }
                    });

                    // 创建代理对象
                    Object proxy = RpcProxy.createProxy(interfaceClass, reference.version(), client);
                    field.set(bean, proxy);
                    logger.info("Injected RPC proxy for {}", interfaceClass.getName());
                } catch (Exception e) {
                    logger.error("Failed to inject RPC proxy for " + interfaceClass.getName(), e);
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }

    public void destroy() {
        clientMap.values().forEach(NettyClient::stop);
        clientMap.clear();
    }
}