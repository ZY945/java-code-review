package com.web.rpc.core.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class JsonSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        // 创建一个全新的ObjectMapper实例，使用基本配置
        this.objectMapper = createObjectMapper();
    }
    
    /**
     * 创建一个安全的ObjectMapper实例，避免使用高级特性
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 基本配置
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        
        // 忽略null值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // 使用字段而不是方法访问，避免一些反射问题
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        
        return mapper;
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
            if (clazz == RpcResponse.class) {
                // 特殊处理RpcResponse，因为它包含泛型result字段
                RpcResponse response = objectMapper.readValue(bytes, RpcResponse.class);
                // 如果result是LinkedHashMap，尝试转换为正确的类型
                handleResponseResult(response);
                return (T) response;
            } else {
                // 使用更安全的反序列化方式
                T result = objectMapper.readValue(bytes, clazz);
                logger.debug("Successfully deserialized object of class: {}", clazz.getName());
                return result;
            }
        } catch (Exception e) {
            logger.error("Deserialization failed for class: {}, error: {}", clazz.getName(), e.getMessage());
            // 创建一个新的异常，避免暴露原始异常的堆栈跟踪
            throw new RuntimeException("Failed to deserialize: " + e.getMessage());
        }
    }
    
    /**
     * 处理RpcResponse中的result字段，尝试将LinkedHashMap转换为正确的类型
     */
    private void handleResponseResult(RpcResponse response) {
        if (response.getResult() != null && response.getResult() instanceof java.util.LinkedHashMap) {
            try {
                // 将LinkedHashMap转换为JSON字符串，然后再转换为目标类型
                String json = objectMapper.writeValueAsString(response.getResult());
                // 这里我们需要猜测目标类型，通常可以从类名或包名推断
                String className = guessResultClassName(response.getResult());
                if (className != null) {
                    try {
                        Class<?> resultClass = Class.forName(className);
                        Object convertedResult = objectMapper.readValue(json, resultClass);
                        response.setResult(convertedResult);
                        logger.debug("Successfully converted result to: {}", resultClass.getName());
                    } catch (ClassNotFoundException e) {
                        logger.warn("Could not find class: {}", className);
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to convert result: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 尝试从LinkedHashMap中猜测目标类名
     */
    private String guessResultClassName(Object result) {
        if (result instanceof java.util.LinkedHashMap) {
            java.util.LinkedHashMap<?, ?> map = (java.util.LinkedHashMap<?, ?>) result;
            // 检查是否有类名相关的字段
            if (map.containsKey("@class")) {
                return (String) map.get("@class");
            }
            
            // 尝试从字段组合推断类型
            if (map.containsKey("host") && map.containsKey("port") && map.containsKey("version") && map.containsKey("startTime")) {
                return "com.web.rpc.example.api.ServerInfo";
            }
        }
        return null;
    }
}