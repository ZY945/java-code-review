package com.web.rpc.client.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RPC客户端配置属性
 */
@ConfigurationProperties(prefix = "rpc.client")
public class RpcClientProperties {
    /**
     * 客户端连接的服务器主机地址，默认为localhost
     */
    private String host = "localhost";
    
    /**
     * 客户端连接的服务器端口，默认为8080
     */
    private int port = 8080;
    
    /**
     * 服务接口类
     */
    private Class<?> serviceInterface;
    
    /**
     * 服务版本号，默认为1.0.0
     */
    private String version = "1.0.0";
    
    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;
    
    /**
     * 请求超时时间（毫秒）
     */
    private int requestTimeout = 10000;

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

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    

    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getRequestTimeout() {
        return requestTimeout;
    }
    
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}