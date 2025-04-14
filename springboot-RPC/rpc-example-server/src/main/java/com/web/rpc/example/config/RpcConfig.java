package com.web.rpc.example.config;

import com.web.rpc.server.RpcServerBootstrap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RPC服务器配置类
 */
@Configuration
public class RpcConfig {
    @Value("${rpc.server.port:8888}")
    private int serverPort;

    @Value("${etcd.endpoints:http://localhost:2379}")
    private String etcdEndpoints;

    @Bean
    public RpcServerBootstrap rpcServerBootstrap() throws Exception {
        // 确保ETCD端点格式正确
        String[] endpoints = etcdEndpoints.split(",");
        for (int i = 0; i < endpoints.length; i++) {
            String endpoint = endpoints[i].trim();
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                endpoints[i] = "http://" + endpoint;
            }
        }
        return new RpcServerBootstrap(serverPort, endpoints);
    }
}
