package com.web.rpc.core;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    // 请求唯一标识
    private String requestId;
    // 返回结果
    private Object result;
    // 异常信息
    private Throwable error;
}