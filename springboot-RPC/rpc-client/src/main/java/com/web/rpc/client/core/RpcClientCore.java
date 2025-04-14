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

    public RpcClientCore(String host, int port, String[] etcdEndpoints, Class<?> serviceInterface, String version) {
        this.nettyClient = new NettyClient(host, port);
        this.serviceDiscovery = new EtcdServiceDiscovery(etcdEndpoints);
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