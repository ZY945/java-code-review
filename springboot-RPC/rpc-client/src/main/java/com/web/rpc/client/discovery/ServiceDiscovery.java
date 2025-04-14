package com.web.rpc.client.discovery;

import com.web.rpc.core.registry.ServiceInfo;

import java.util.List;

/**
 * 服务发现接口
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     * @return 服务实例
     */
    ServiceInfo discover(String serviceName);

    /**
     * 获取服务的所有实例
     *
     * @param serviceName 服务名称
     * @return 服务实例列表
     */
    List<ServiceInfo> getServiceInstances(String serviceName);

    /**
     * 刷新服务缓存
     *
     * @param serviceName 服务名称
     */
    void refreshService(String serviceName);

    /**
     * 关闭服务发现
     */
    void close();
}