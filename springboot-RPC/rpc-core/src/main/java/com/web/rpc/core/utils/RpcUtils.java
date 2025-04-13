package com.web.rpc.core.utils;

import java.util.UUID;

public class RpcUtils {
    // 生成唯一请求 ID
    public static String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    // 获取服务全名（包含版本）
    public static String getServiceName(Class<?> interfaceClass, String version) {
        String serviceName = interfaceClass.getName();
        if (version != null && !version.isEmpty()) {
            serviceName += ":" + version;
        }
        return serviceName;
    }
}