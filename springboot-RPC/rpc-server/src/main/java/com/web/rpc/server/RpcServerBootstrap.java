package com.web.rpc.server;

import com.web.rpc.core.registry.EtcdServiceRegistry;
import com.web.rpc.core.registry.ServiceRegistry;
import com.web.rpc.server.netty.NettyServer;
import com.web.rpc.server.service.RpcServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.util.Map;

/**
 * RPC服务器引导类
 * 负责初始化和启动RPC服务器
 */
public class RpcServerBootstrap implements InitializingBean, DisposableBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerBootstrap.class);

    private final String host;
    private final int port;
    private final String[] etcdEndpoints;
    private ApplicationContext applicationContext;
    private NettyServer nettyServer;
    private ServiceRegistry serviceRegistry;
    private RpcServiceManager serviceManager;

    /**
     * 创建RPC服务器引导类
     *
     * @param port          服务器端口
     * @param etcdEndpoints ETCD服务地址
     * @throws Exception 如果获取本机IP地址失败
     */
    public RpcServerBootstrap(int port, String... etcdEndpoints) throws Exception {
        this(InetAddress.getLocalHost().getHostAddress(),port, etcdEndpoints);
    }

    /**
     * 创建RPC服务器引导类
     *
     * @param port          服务器端口
     * @param etcdEndpoints ETCD服务地址
     * @throws Exception 如果获取本机IP地址失败
     */
    public RpcServerBootstrap(String host,int port, String... etcdEndpoints) throws Exception {
        this.host = host;
        this.port = port;
        this.etcdEndpoints = etcdEndpoints;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化服务注册中心，默认使用ETCD
        serviceRegistry = new EtcdServiceRegistry(etcdEndpoints);

        // 创建服务管理器
        serviceManager = new RpcServiceManager(serviceRegistry, host, port);

        // 扫描和注册服务
        Map<String, Object> serviceMap = serviceManager.scanAndRegisterServices(applicationContext);

        // 启动Netty服务器
        nettyServer = new NettyServer(host, port, serviceMap, serviceRegistry);
        nettyServer.start();
        logger.info("RPC Server started on {}:{}", host, port);
    }

    @Override
    public void destroy() throws Exception {
        if (nettyServer != null) {
            nettyServer.stop();
        }

        if (serviceManager != null) {
            serviceManager.unregisterAllServices();
        }

        if (serviceRegistry != null) {
            serviceRegistry.close();
        }

        logger.info("RPC Server stopped");
    }
}