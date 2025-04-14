package com.web.rpc.example.service.impl;

import com.web.rpc.core.annotation.RpcService;
import com.web.rpc.example.ServerApplication;
import com.web.rpc.example.api.HelloService;
import com.web.rpc.example.api.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HelloService的实现类
 */
@RpcService(version = "1.0.0")
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    private final long startTime = System.currentTimeMillis();

    private static final int serverPort = ServerApplication.SERVER_PORT;

    private static final String applicationName = ServerApplication.applicationName;

    private static final String version = ServerApplication.version;

    @Override
    public String sayHello(String name) {
        String message = "Hello, " + name;
        logger.info("Received hello request: {}", name);
        return message;
    }

    @Override
    public String helloWithDelay(String name, long delayMillis) {
        try {
            logger.info("Received hello request with delay: {}, delay: {}ms", name, delayMillis);
            Thread.sleep(delayMillis);
            return "Delayed hello, " + name;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public List<String> batchHello(List<String> names) {
        logger.info("Received batch hello request for {} names", names.size());
        List<String> results = new ArrayList<>(names.size());
        for (String name : names) {
            results.add("Hello, " + name);
        }
        return results;
    }

    @Override
    public ServerInfo getServerInfo() {
        logger.info("Received request for server info");

        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            host = "unknown";
            logger.error("Failed to get host address", e);
        }

        Map<String, String> properties = new HashMap<>();
        properties.put("application.name", applicationName);
        properties.put("java.version", System.getProperty("java.version"));
        properties.put("os.name", System.getProperty("os.name"));
        properties.put("os.version", System.getProperty("os.version"));
        properties.put("user.name", System.getProperty("user.name"));
        properties.put("available.processors", String.valueOf(Runtime.getRuntime().availableProcessors()));

        return new ServerInfo(host, serverPort, version, startTime, properties);
    }
}
