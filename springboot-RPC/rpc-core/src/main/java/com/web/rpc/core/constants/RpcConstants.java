package com.web.rpc.core.constants;

/**
 * RPC常量定义
 */
public interface RpcConstants {
    /**
     * 默认组名
     */
    String DEFAULT_GROUP = "default";

    /**
     * 默认版本号
     */
    String DEFAULT_VERSION = "1.0.0";

    /**
     * 默认超时时间（毫秒）
     */
    int DEFAULT_TIMEOUT = 5000;

    /**
     * 心跳间隔（毫秒）
     */
    int HEARTBEAT_INTERVAL = 30000;

    /**
     * 重试间隔（毫秒）
     */
    int RETRY_INTERVAL = 1000;

    /**
     * 最大重试次数
     */
    int MAX_RETRIES = 3;

    /**
     * 默认权重
     */
    int DEFAULT_WEIGHT = 100;

    /**
     * 序列化类型
     */
    interface SerializationType {
        byte JSON = 1;
        byte PROTOBUF = 2;
        byte KRYO = 3;
    }

    /**
     * 压缩类型
     */
    interface CompressType {
        byte NONE = 0;
        byte GZIP = 1;
    }

    /**
     * 负载均衡策略
     */
    interface LoadBalance {
        String RANDOM = "random";
        String ROUND_ROBIN = "roundRobin";
        String CONSISTENT_HASH = "consistentHash";
    }

    /**
     * 请求头常量
     */
    interface Headers {
        String TRACE_ID = "traceId";
        String APP_NAME = "appName";
        String TOKEN = "token";
        String TIMEOUT = "timeout";
    }
}
