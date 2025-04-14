package com.web.rpc.server.processor;

import com.web.rpc.core.annotation.RpcService;
import com.web.rpc.core.registry.ServiceRegistry;
import com.web.rpc.core.utils.RpcUtils;
import com.web.rpc.server.registry.ServerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class RpcServiceBeanPostProcessor implements BeanPostProcessor{
    private static final Logger logger = LoggerFactory.getLogger(RpcServiceBeanPostProcessor.class);
    private ServerRegistry serverRegistry;
    private String serverHost;
    private int serverPort;

    public RpcServiceBeanPostProcessor(ServerRegistry serviceDiscovery) {
        this.serverRegistry = serviceDiscovery;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            if (interfaces.length == 0) {
                logger.warn("Bean {} annotated with @RpcService but implements no interfaces", beanName);
                return bean;
            }

            String serviceName = RpcUtils.getServiceName(interfaces[0], rpcService.version());

            // 注册到 etcd
            if (serverRegistry != null) {
                serverRegistry.register(serviceName, serverHost, serverPort);
                logger.info("Registered service to etcd: {} at {}:{}", serviceName, serverHost, serverPort);
            } else {
                logger.warn("ServiceRegistry is not configured, skipping etcd registration");
            }
        }
        return bean;
    }

}