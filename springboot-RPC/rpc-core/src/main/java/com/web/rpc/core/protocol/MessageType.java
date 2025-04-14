package com.web.rpc.core.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * RPC消息类型
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
public enum MessageType {
    REQUEST(1),           // 请求消息
    RESPONSE(2),         // 响应消息
    HEARTBEAT_REQUEST(3),// 心跳请求
    HEARTBEAT_RESPONSE(4);// 心跳响应

    private final int type;

    MessageType(int type) {
        this.type = type;
    }

    @JsonValue
    public int getType() {
        return type;
    }

    @JsonCreator
    public static MessageType fromType(@JsonProperty("type") int type) {
        for (MessageType msgType : MessageType.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        // 默认返回REQUEST类型，避免抛出异常
        return REQUEST;
    }
}
