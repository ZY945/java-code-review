package com.web.rpc.core.protocol;

import lombok.Getter;

/**
 * RPC状态码
 */
@Getter
public enum RpcStatus {
    SUCCESS(200, "Success"),
    CLIENT_ERROR(400, "Bad Request"),
    NOT_FOUND(404, "Service Not Found"),
    METHOD_NOT_FOUND(405, "Method Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    TIMEOUT(504, "Request Timeout"),
    CLIENT_TIMEOUT(408, "Client Timeout"),
    BAD_RESPONSE(502, "Bad Response"),
    SERIALIZATION_FAILURE(506, "Serialization Failed"),
    DESERIALIZATION_FAILURE(507, "Deserialization Failed");

    private final int code;
    private final String message;

    RpcStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static RpcStatus fromCode(int code) {
        for (RpcStatus status : RpcStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return INTERNAL_ERROR;
    }
}
