package com.web.rpc.example.api;

import java.io.Serializable;
import java.util.Map;

/**
 * 服务器信息类
 * 用于返回服务器的基本信息
 */
public class ServerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String host;
    private int port;
    private String version;
    private long startTime;
    private Map<String, String> properties;

    public ServerInfo() {
    }

    public ServerInfo(String host, int port, String version, long startTime, Map<String, String> properties) {
        this.host = host;
        this.port = port;
        this.version = version;
        this.startTime = startTime;
        this.properties = properties;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", version='" + version + '\'' +
                ", startTime=" + startTime +
                ", properties=" + properties +
                '}';
    }
}
