package com.web.rpc.client;

import com.web.rpc.client.discovery.EtcdServiceDiscovery;
import com.web.rpc.client.discovery.ServiceDiscovery;
import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.client.proxy.RpcClientProxy;
import com.web.rpc.client.proxy.RpcProxy;
import com.web.rpc.core.constants.RpcConstants;
import com.web.rpc.core.registry.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * RPC客户端工厂
 * 负责创建和管理RPC客户端
 */
public class RpcClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientFactory.class);
    
    private final ServiceDiscovery serviceDiscovery;
    private final Map<String, NettyClient> clientMap = new ConcurrentHashMap<>();
    private final String version;
    private final long timeout;
    private final TimeUnit timeUnit;
    private final byte serializationType;
    private final byte compressionType;
    
    public RpcClientFactory(String... etcdEndpoints) {
        this(RpcConstants.DEFAULT_VERSION, RpcConstants.DEFAULT_TIMEOUT,
                TimeUnit.MILLISECONDS, RpcConstants.SerializationType.JSON,
                RpcConstants.CompressType.GZIP, etcdEndpoints);
    }
    
    public RpcClientFactory(String version, long timeout, TimeUnit timeUnit, 
                           byte serializationType, byte compressionType, 
                           String... etcdEndpoints) {
        this.serviceDiscovery = new EtcdServiceDiscovery(etcdEndpoints);
        this.version = version;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.serializationType = serializationType;
        this.compressionType = compressionType;
    }
    
    /**
     * 创建服务接口的代理对象，使用默认版本号
     */
    public <T> T createService(Class<T> serviceClass) {
        return createService(serviceClass, this.version);
    }
    
    /**
     * 创建服务接口的代理对象，指定版本号
     * 
     * @param serviceClass 服务接口类
     * @param version 版本号
     * @return 服务代理对象
     * @param <T> 服务接口类型
     */
    public <T> T createService(Class<T> serviceClass, String version) {
        String serviceName = serviceClass.getName() + "#" + version;
        logger.info("尝试发现服务: {}", serviceName);
        
        // 从服务发现获取服务地址
        ServiceInfo serviceInfo = serviceDiscovery.discover(serviceName);
        if (serviceInfo == null) {
            throw new RuntimeException("Service not found: " + serviceName);
        }
        String host = serviceInfo.getHost();
        int port = serviceInfo.getPort();
        logger.info("发现服务实例: {}:{}", host, port);
        
        // 获取或创建客户端
        String clientKey = host + ":" + port;
        NettyClient client = clientMap.computeIfAbsent(clientKey, k -> {
            try {
                NettyClient newClient = new NettyClient(host, port);
                newClient.start();
                return newClient;
            } catch (Exception e) {
                logger.error("Failed to create client for " + clientKey, e);
                throw new RuntimeException("Failed to create client", e);
            }
        });
        
        // 创建代理 - 支持两种方式
        // 1. 使用RpcClientProxy (新方式)
        RpcClientProxy clientProxy = new RpcClientProxy(client, version, timeout, 
                timeUnit, serializationType, compressionType);
        return clientProxy.getProxy(serviceClass);
        
        // 2. 使用RpcProxy (兼容旧方式)
        // return RpcProxy.createProxy(serviceClass, version, client);
    }
    
    /**
     * 关闭所有客户端连接
     */
    public void shutdown() {
        for (NettyClient client : clientMap.values()) {
            try {
                client.stop();
            } catch (Exception e) {
                logger.error("Error stopping client", e);
            }
        }
        clientMap.clear();
        
        try {
            serviceDiscovery.close();
        } catch (Exception e) {
            logger.error("Error closing service discovery", e);
        }
        
        logger.info("RPC client factory shutdown");
    }
}
