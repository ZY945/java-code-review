package com.web.rpc.client.spring;

import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.client.proxy.RpcProxy;
import com.web.rpc.core.annotation.RpcReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class RpcClientBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientBeanPostProcessor.class);
    private final NettyClient nettyClient;

    public RpcClientBeanPostProcessor(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference reference = field.getAnnotation(RpcReference.class);
                field.setAccessible(true);
                try {
                    Object proxy = RpcProxy.createProxy(field.getType(), reference.version(), nettyClient);
                    field.set(bean, proxy);
                    logger.info("Injected RPC proxy for field: {}.{}", clazz.getName(), field.getName());
                } catch (IllegalAccessException e) {
                    logger.error("Failed to inject RPC proxy for field: {}.{}", clazz.getName(), field.getName(), e);
                }
            }
        }
        return bean;
    }
}