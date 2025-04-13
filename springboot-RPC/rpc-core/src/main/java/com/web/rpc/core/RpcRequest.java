package com.web.rpc.core;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    // 请求唯一标识
    private String requestId;
    // 服务接口名
    private String serviceName;
    // 方法名
    private String methodName;
    // 参数类型
    private Class<?>[] parameterTypes;
    // 参数值
    private Object[] parameters;
}