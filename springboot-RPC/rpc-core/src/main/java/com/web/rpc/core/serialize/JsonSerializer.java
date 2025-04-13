package com.web.rpc.core.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(Object obj) throws Exception {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            logger.error("Serialization failed for object: {}", obj, e);
            throw e;
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            logger.error("Deserialization failed for class: {}", clazz.getName(), e);
            throw e;
        }
    }
}