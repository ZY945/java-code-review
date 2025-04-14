package com.web.rpc.core.protocol;

import lombok.Data;

/**
 * RPC统一消息格式
 */
@Data
public class RpcMessage {
    /**
     * 魔数，用于快速识别RPC消息
     */
    private static final short MAGIC_NUMBER = 0x10;

    /**
     * 协议版本号
     */
    private byte version = 1;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 序列化类型 1-JSON, 2-Protobuf, 3-Kryo
     */
    private byte serializationType;

    /**
     * 压缩类型 0-不压缩, 1-gzip
     */
    private byte compressionType;

    /**
     * 请求ID
     */
    private long requestId;

    /**
     * 消息体
     */
    private Object data;

    public static RpcMessage createRequest(long requestId, Object data) {
        RpcMessage message = new RpcMessage();
        message.setMessageType(MessageType.REQUEST);
        message.setRequestId(requestId);
        message.setData(data);
        return message;
    }

    public static RpcMessage createResponse(long requestId, Object data) {
        RpcMessage message = new RpcMessage();
        message.setMessageType(MessageType.RESPONSE);
        message.setRequestId(requestId);
        message.setData(data);
        return message;
    }

    public static RpcMessage createHeartbeat(boolean isRequest) {
        RpcMessage message = new RpcMessage();
        message.setMessageType(isRequest ? MessageType.HEARTBEAT_REQUEST : MessageType.HEARTBEAT_RESPONSE);
        message.setRequestId(0L);
        return message;
    }
}
