package com.web.rpc.client.proxy;

import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.utils.RpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);
    private final String serviceName;
    private final String version;
    private final NettyClient nettyClient;

    public RpcProxy(Class<?> interfaceClass, String version, NettyClient nettyClient) {
        this.serviceName = RpcUtils.getServiceName(interfaceClass, version);
        this.version = version;
        this.nettyClient = nettyClient;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> interfaceClass, String version, NettyClient nettyClient) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new RpcProxy(interfaceClass, version, nettyClient)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求
        RpcRequest request = new RpcRequest();
        request.setRequestId(RpcUtils.generateRequestId());
        request.setServiceName(serviceName);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        logger.info("Sending request: {}", request);

        // 通过 Netty 客户端发送请求
        RpcResponse response = nettyClient.sendRequest(request);

        // 处理响应
        if (response.getError() != null) {
            logger.error("RPC call failed: {}", response.getError().getMessage());
            throw response.getError();
        }

        return response.getResult();
    }
}