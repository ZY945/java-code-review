package com.web.rpc.server.spring;

import com.web.rpc.core.annotation.RpcService;
import com.web.rpc.core.utils.RpcUtils;
import com.web.rpc.server.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServiceBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(RpcServiceBeanPostProcessor.class);
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;

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
            serviceMap.put(serviceName, bean);
            logger.info("Registered service: {}", serviceName);

            // 获取 ServiceRegistry 并注册服务
            ServiceRegistry registry = applicationContext.getBean(ServiceRegistry.class);
            registry.register(serviceName, "localhost", 8080); // 简化，实际需动态获取
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Map<String, Object> getServiceMap() {
        return serviceMap;
    }
}