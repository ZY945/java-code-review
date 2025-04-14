//package com.web.rpc.server.processor;
//
//import com.web.rpc.server.service.RpcServiceManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//
///**
// * RPC服务Bean后处理器
// * 负责在Bean初始化后处理@RpcService注解
// *
// * @deprecated 请使用RpcServerBootstrap和RpcServiceManager替代，它们提供了更集中的服务管理方式
// */
//@Deprecated
//public class RpcServiceBeanPostProcessor implements BeanPostProcessor {
//    private static final Logger logger = LoggerFactory.getLogger(RpcServiceBeanPostProcessor.class);
//    private final RpcServiceManager serviceManager;
//
//    /**
//     * 创建RPC服务Bean后处理器
//     *
//     * @param serviceManager RPC服务管理器
//     */
//    public RpcServiceBeanPostProcessor(RpcServiceManager serviceManager) {
//        this.serviceManager = serviceManager;
//        logger.warn("RpcServiceBeanPostProcessor is deprecated. Please use RpcServerBootstrap and RpcServiceManager instead.");
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (serviceManager != null) {
//            // 将服务注册委托给RpcServiceManager
//            serviceManager.registerService(bean, beanName);
//        } else {
//            logger.warn("RpcServiceManager is not configured, skipping service registration for {}", beanName);
//        }
//        return bean;
//    }
//}