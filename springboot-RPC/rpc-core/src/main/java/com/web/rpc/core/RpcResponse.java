package com.web.rpc.core;

import com.web.rpc.core.protocol.RpcStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * RPC响应对象
 */
@Data
@Accessors(chain = true)
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 2L;

    /**
     * 请求唯一标识
     */
    private String requestId;

    /**
     * 状态码
     */
    private RpcStatus status = RpcStatus.SUCCESS;

    /**
     * 返回结果
     */
    private Object result;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 详细异常信息，只在开发环境返回
     */
    private Throwable error;

    /**
     * 响应时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 创建成功响应
     */
    public static RpcResponse success(String requestId, Object result) {
        return new RpcResponse()
                .setRequestId(requestId)
                .setStatus(RpcStatus.SUCCESS)
                .setResult(result);
    }

    /**
     * 创建错误响应
     */
    public static RpcResponse error(String requestId, RpcStatus status, String errorMessage) {
        return new RpcResponse()
                .setRequestId(requestId)
                .setStatus(status)
                .setErrorMessage(errorMessage);
    }

    /**
     * 创建异常响应
     */
    public static RpcResponse exception(String requestId, Throwable throwable) {
        return new RpcResponse()
                .setRequestId(requestId)
                .setStatus(RpcStatus.INTERNAL_ERROR)
                .setErrorMessage(throwable.getMessage())
                .setError(throwable);
    }
}