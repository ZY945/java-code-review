package com.web.rpc.server.registry;

import com.web.rpc.core.registry.ServiceInfo;
import com.web.rpc.core.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServerRegistry.class);

    private final ServiceRegistry serviceRegistry;
    public ServerRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void register(String serviceName, String host, int port) {
        serviceRegistry.register(serviceName, host,port);
        logger.info("Registered service {} at {}:{}", serviceName, host, port);
    }

    public void unregister(String serviceName, String host, int port) {
        serviceRegistry.unregister(serviceName,host,port);
        logger.info("Unregistered service {} at {}:{}", serviceName, host, port);
    }
}