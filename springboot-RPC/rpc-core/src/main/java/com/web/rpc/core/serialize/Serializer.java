package com.web.rpc.core.serialize;

public interface Serializer {
    // 序列化为字节数组
    byte[] serialize(Object obj) throws Exception;
    // 从字节数组反序列化
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}