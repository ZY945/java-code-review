package com.web.rpc.client.proxy;

import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.constants.RpcConstants;
import com.web.rpc.core.protocol.RpcMessage;
import com.web.rpc.core.protocol.RpcStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * RPC代理类
 * 用于创建远程服务的本地代理对象
 */
public class RpcProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);
    private final String serviceName;
    private final String version;
    private final NettyClient nettyClient;
    private final long timeout;
    private final TimeUnit timeUnit;
    private final byte serializationType;
    private final byte compressionType;

    public RpcProxy(Class<?> interfaceClass, String version, NettyClient nettyClient) {
        this(interfaceClass, version, nettyClient, RpcConstants.DEFAULT_TIMEOUT,
                TimeUnit.MILLISECONDS, RpcConstants.SerializationType.JSON,
                RpcConstants.CompressType.GZIP);
    }

    public RpcProxy(Class<?> interfaceClass, String version, NettyClient nettyClient,
                    long timeout, TimeUnit timeUnit, byte serializationType, byte compressionType) {
        this.serviceName = interfaceClass.getName();
        this.version = version;
        this.nettyClient = nettyClient;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.serializationType = serializationType;
        this.compressionType = compressionType;
    }

    /**
     * 创建代理对象
     */
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
        // 对Object类的方法直接调用
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        // 构造请求
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setServiceName(serviceName);
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setVersion(version);
        request.setTimeout(timeout);
        request.setTimeUnit(timeUnit);
        request.setTimestamp(System.currentTimeMillis());

        // 构建RPC消息
        RpcMessage message = RpcMessage.createRequest(UUID.randomUUID().getMostSignificantBits(), request);
        message.setSerializationType(serializationType);
        message.setCompressionType(compressionType);

        logger.info("Sending request: {} method: {}", request.getRequestId(), method.getName());

        try {
            // 通过 Netty 客户端发送请求
            CompletableFuture<RpcResponse> future = nettyClient.sendRequest(message);

            // 等待响应，带超时
            RpcResponse response = future.get(timeout, timeUnit);

            // 处理响应
            if (response.getStatus() != RpcStatus.SUCCESS) {
                logger.error("RPC call failed: {}", response.getErrorMessage());
                throw new RuntimeException("RPC call failed: " + response.getErrorMessage());
            }

            return response.getResult();
        } catch (Exception e) {
            logger.error("Error during RPC call", e);
            throw e;
        }
    }
}
