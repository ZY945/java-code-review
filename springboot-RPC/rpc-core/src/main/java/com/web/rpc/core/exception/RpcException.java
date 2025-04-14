package com.web.rpc.core.exception;

import com.web.rpc.core.protocol.RpcStatus;
import lombok.Getter;

/**
 * RPC异常基类
 */
@Getter
public class RpcException extends RuntimeException {
    private final RpcStatus status;

    public RpcException(RpcStatus status, String message) {
        super(message);
        this.status = status;
    }

    public RpcException(RpcStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public static RpcException clientError(String message) {
        return new RpcException(RpcStatus.CLIENT_ERROR, message);
    }

    public static RpcException serviceNotFound(String serviceName) {
        return new RpcException(RpcStatus.NOT_FOUND, 
            String.format("Service not found: %s", serviceName));
    }

    public static RpcException methodNotFound(String methodName) {
        return new RpcException(RpcStatus.METHOD_NOT_FOUND,
            String.format("Method not found: %s", methodName));
    }

    public static RpcException timeout(String requestId) {
        return new RpcException(RpcStatus.TIMEOUT,
            String.format("Request timeout for request: %s", requestId));
    }

    public static RpcException serviceUnavailable(String serviceName) {
        return new RpcException(RpcStatus.SERVICE_UNAVAILABLE,
            String.format("Service unavailable: %s", serviceName));
    }
}
