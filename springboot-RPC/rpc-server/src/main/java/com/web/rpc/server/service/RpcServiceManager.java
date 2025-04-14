package com.web.rpc.server.service;

import com.web.rpc.core.annotation.RpcService;
import com.web.rpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC服务管理器
 * 负责扫描、注册和管理RPC服务
 */
public class RpcServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(RpcServiceManager.class);

    // 服务映射表，key为服务名称，value为服务实现对象
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    // 服务注册中心
    private final ServiceRegistry serviceRegistry;

    // 服务器主机和端口
    private final String host;
    private final int port;

    // 标记是否已初始化
    private boolean initialized = false;

    /**
     * 创建RPC服务管理器
     *
     * @param serviceRegistry 服务注册中心
     * @param host            服务器主机
     * @param port            服务器端口
     */
    public RpcServiceManager(ServiceRegistry serviceRegistry, String host, int port) {
        this.serviceRegistry = serviceRegistry;
        this.host = host;
        this.port = port;
    }

    /**
     * 扫描并注册带有@RpcService注解的服务
     *
     * @param applicationContext Spring应用上下文
     * @return 服务映射表
     */
    public synchronized Map<String, Object> scanAndRegisterServices(ApplicationContext applicationContext) {
        if (initialized) {
            logger.warn("RPC services have already been scanned and registered");
            return serviceMap;
        }

        logger.info("Scanning for RPC services...");

        // 获取所有带有@RpcService注解的Bean
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(RpcService.class);
        logger.info("Found {} RPC services", beanNames.length);

        for (String beanName : beanNames) {
            Object serviceBean = applicationContext.getBean(beanName);
            RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
            Class<?>[] interfaces = serviceBean.getClass().getInterfaces();

            if (interfaces.length == 0) {
                logger.warn("RPC service {} implements no interfaces, skipping", beanName);
                continue;
            }

            // 使用接口名和版本号作为服务名
            String serviceName = interfaces[0].getName() + "#" + rpcService.version();
            serviceMap.put(serviceName, serviceBean);

            // 注册服务到注册中心
            if (serviceRegistry != null) {
                serviceRegistry.register(serviceName, host, port);
                logger.info("Registered service: {} at {}:{}", serviceName, host, port);
            } else {
                logger.warn("Service registry is null, skipping registration for {}", serviceName);
            }
        }

        initialized = true;
        return serviceMap;
    }

    /**
     * 注册单个服务
     *
     * @param bean     服务Bean
     * @param beanName Bean名称
     */
    public void registerService(Object bean, String beanName) {
        if (!bean.getClass().isAnnotationPresent(RpcService.class)) {
            return;
        }

        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        Class<?>[] interfaces = bean.getClass().getInterfaces();

        if (interfaces.length == 0) {
            logger.warn("RPC service {} implements no interfaces, skipping", beanName);
            return;
        }

        // 使用接口名和版本号作为服务名
        String serviceName = interfaces[0].getName() + "#" + rpcService.version();

        // 检查服务是否已注册
        if (serviceMap.containsKey(serviceName)) {
            logger.warn("Service {} is already registered, skipping", serviceName);
            return;
        }

        serviceMap.put(serviceName, bean);

        // 注册服务到注册中心
        if (serviceRegistry != null) {
            serviceRegistry.register(serviceName, host, port);
            logger.info("Registered service: {} at {}:{}", serviceName, host, port);
        } else {
            logger.warn("Service registry is null, skipping registration for {}", serviceName);
        }
    }

    /**
     * 获取所有已注册的服务
     *
     * @return 服务映射表
     */
    public Map<String, Object> getServiceMap() {
        return new HashMap<>(serviceMap);
    }

    /**
     * 注销所有服务
     */
    public void unregisterAllServices() {
        if (serviceRegistry == null) {
            logger.warn("Service registry is null, cannot unregister services");
            return;
        }

        for (String serviceName : serviceMap.keySet()) {
            serviceRegistry.unregister(serviceName, host, port);
            logger.info("Unregistered service: {} from {}:{}", serviceName, host, port);
        }

        serviceMap.clear();
        initialized = false;
    }
}
