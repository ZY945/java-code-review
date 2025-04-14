package com.web.rpc.core.registry;

public interface ServiceRegistry {
    /**
     * 注册服务
     *
     * @param serviceName 服务名称
     * @param host 主机地址
     * @param port 端口
     */
    void register(String serviceName, String host, int port);

    /**
     * 注销服务
     *
     * @param serviceName 服务名称
     * @param host 主机地址
     * @param port 端口
     */
    void unregister(String serviceName, String host, int port);

    /**
     * 获取服务信息
     *
     * @param serviceName 服务名称
     * @return 服务信息
     */
    ServiceInfo getService(String serviceName);

    /**
     * 关闭注册中心客户端
     */
    void close();
} 