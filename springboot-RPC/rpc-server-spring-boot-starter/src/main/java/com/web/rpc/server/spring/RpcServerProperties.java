package com.web.rpc.server.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RPC服务器配置属性
 */
@ConfigurationProperties(prefix = "rpc.server")
public class RpcServerProperties {
    /**
     * 服务器主机地址，默认为本机IP
     */
    private String host = "localhost";
    
    /**
     * 服务器端口，默认为8080
     */
    private int port = 8080;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}