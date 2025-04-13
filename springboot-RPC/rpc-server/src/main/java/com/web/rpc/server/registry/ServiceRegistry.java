package com.web.rpc.server.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    public void register(String serviceName, String host, int port) {
        // 模拟注册到 etcd
        logger.info("Registered service {} at {}:{}", serviceName, host, port);
    }

    public void unregister(String serviceName, String host, int port) {
        logger.info("Unregistered service {} at {}:{}", serviceName, host, port);
    }
}