package com.web.rpc.client.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    private final List<String> serviceAddresses = Arrays.asList("localhost:8080"); // 模拟 etcd 返回
    private final Random random = new Random();

    public String discover(String serviceName) {
        // 随机负载均衡
        String address = serviceAddresses.get(random.nextInt(serviceAddresses.size()));
        logger.info("Discovered service {} at {}", serviceName, address);
        return address;
    }
}