package com.web.rpc.client.proxy;

import com.web.rpc.client.netty.NettyClient;
import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
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
 * RPC客户端动态代理
 * 负责创建远程服务的本地代理对象
 */
public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final NettyClient nettyClient;
    private final String version;
    private final long timeout;
    private final TimeUnit timeUnit;
    private final byte serializationType;
    private final byte compressionType;

//    public RpcClientProxy(NettyClient nettyClient) {
//        this(nettyClient, RpcConstants.DEFAULT_VERSION, RpcConstants.DEFAULT_TIMEOUT,
//                TimeUnit.MILLISECONDS, RpcConstants.SerializationType.PROTOBUF,
//                RpcConstants.CompressType.GZIP);
//    }

    public RpcClientProxy(NettyClient nettyClient, String version, long timeout,
                          TimeUnit timeUnit, byte serializationType, byte compressionType) {
        this.nettyClient = nettyClient;
        this.version = version;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.serializationType = serializationType;
        this.compressionType = compressionType;
    }

    /**
     * 创建代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 对Object类的方法直接调用
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        // 构建RPC请求
        RpcRequest request = buildRequest(method, args);

        // 构建RPC消息
        RpcMessage message = RpcMessage.createRequest(UUID.randomUUID().getMostSignificantBits(), request);
        message.setSerializationType(serializationType);
        message.setCompressionType(compressionType);

        logger.debug("Sending request: {}, method: {}, args: {}",
                request.getRequestId(), method.getName(), args);

        // 发送请求并等待响应
        CompletableFuture<RpcResponse> responseFuture = nettyClient.sendRequest(message);

        // 等待响应，带超时
        RpcResponse response = responseFuture.get(timeout, timeUnit);

        // 处理响应
        if (response.getStatus() != RpcStatus.SUCCESS) {
            logger.error("RPC call failed: {}", response.getErrorMessage());
            throw new RuntimeException("RPC call failed: " + response.getErrorMessage());
        }

        return response.getResult();
    }

    /**
     * 构建RPC请求
     */
    private RpcRequest buildRequest(Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setServiceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setVersion(version);
        request.setTimeout(timeout);
        request.setTimeUnit(timeUnit);
        request.setTimestamp(System.currentTimeMillis());
        return request;
    }
}
