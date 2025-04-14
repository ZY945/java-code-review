package com.web.rpc.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RPC请求对象
 */
@Data
@Accessors(chain = true)
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 2L;

    /**
     * 请求唯一标识
     */
    private String requestId;

    /**
     * 服务接口名
     */
    private String serviceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 服务分组
     */
    private String group = "default";

    /**
     * 服务版本号
     */
    private String version = "1.0.0";

    /**
     * 请求超时时间
     */
    private long timeout = 5000;

    /**
     * 超时时间单位
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 请求头信息
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数值
     */
    private Object[] parameters;

    /**
     * 追踪ID，用于分布式追踪
     */
    private String traceId;

    /**
     * 创建时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 是否为单向调用（不需要返回结果）
     */
    private boolean oneWay = false;

    public String getServiceKey() {
        return String.format("%s:%s:%s", serviceName, group, version);
    }
}