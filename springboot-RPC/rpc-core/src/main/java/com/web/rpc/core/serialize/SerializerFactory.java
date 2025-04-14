package com.web.rpc.core.serialize;

import com.web.rpc.core.constants.RpcConstants;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化工厂，用于管理不同的序列化实现
 */
public class SerializerFactory {
    private static final ConcurrentHashMap<Byte, Serializer> SERIALIZER_MAP = new ConcurrentHashMap<>();

    static {
        SERIALIZER_MAP.put(RpcConstants.SerializationType.JSON, new JsonSerializer());
        SERIALIZER_MAP.put(RpcConstants.SerializationType.PROTOBUF, new ProtobufSerializer());
    }

    /**
     * 获取序列化器
     *
     * @param serializationType 序列化类型
     * @return Serializer实例
     * @throws IllegalArgumentException 如果序列化类型不支持
     */
    public static Serializer getSerializer(byte serializationType) {
        Serializer serializer = SERIALIZER_MAP.get(serializationType);
        if (serializer == null) {
            throw new IllegalArgumentException("Serializer not found for type: " + serializationType);
        }
        return serializer;
    }

    /**
     * 注册序列化器
     *
     * @param serializationType 序列化类型
     * @param serializer 序列化器实例
     */
    public static void registerSerializer(byte serializationType, Serializer serializer) {
        SERIALIZER_MAP.put(serializationType, serializer);
    }
}
