package com.web.rpc.client.core;

import com.web.rpc.client.discovery.EtcdServiceDiscovery;
import com.web.rpc.client.discovery.ServiceDiscovery;
import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.client.proxy.RpcProxy;

/**
 * RPC客户端核心类，用于管理客户端的核心组件
 */
public class RpcClientCore {
    private final NettyClient nettyClient;
    private final ServiceDiscovery serviceDiscovery;
    private final RpcProxy rpcProxy;

    /**
     * 创建RPC客户端核心，使用ETCD端点创建ServiceDiscovery
     *
     * @param host 客户端主机
     * @param port 客户端端口
     * @param etcdEndpoints ETCD端点数组
     * @param serviceInterface 服务接口类
     * @param version 服务版本
     */
    public RpcClientCore(String host, int port, String[] etcdEndpoints, Class<?> serviceInterface, String version) {
        this.nettyClient = new NettyClient(host, port);
        this.serviceDiscovery = etcdEndpoints != null ? new EtcdServiceDiscovery(etcdEndpoints) : null;
        this.rpcProxy = new RpcProxy(serviceInterface, version, nettyClient);
    }
    
    /**
     * 创建RPC客户端核心，直接使用提供的ServiceDiscovery实例
     *
     * @param host 客户端主机
     * @param port 客户端端口
     * @param serviceDiscovery 服务发现组件
     * @param serviceInterface 服务接口类
     * @param version 服务版本
     */
    public RpcClientCore(String host, int port, ServiceDiscovery serviceDiscovery, Class<?> serviceInterface, String version) {
        this.nettyClient = new NettyClient(host, port);
        this.serviceDiscovery = serviceDiscovery;
        this.rpcProxy = new RpcProxy(serviceInterface, version, nettyClient);
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public RpcProxy getRpcProxy() {
        return rpcProxy;
    }

    public void start() throws InterruptedException {
        nettyClient.start();
    }

    public void shutdown() {
        if (nettyClient != null) {
            nettyClient.stop();
        }
        if (serviceDiscovery != null) {
            serviceDiscovery.close();
        }
    }
}