package com.dongfeng.springboot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 消息ID前缀
     */
    private static final String MESSAGE_ID_PREFIX = "lottery:message:";

    /**
     * 设置消息ID到Redis，用于幂等性检查
     * 
     * @param messageId 消息ID
     * @param expireTime 过期时间（秒）
     * @return 是否设置成功
     */
    public boolean setMessageIdIfAbsent(String messageId, long expireTime) {
        String key = MESSAGE_ID_PREFIX + messageId;
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "1", expireTime, TimeUnit.SECONDS);
        return result != null && result;
    }

    /**
     * 检查消息ID是否存在
     * 
     * @param messageId 消息ID
     * @return 是否存在
     */
    public boolean existsMessageId(String messageId) {
        String key = MESSAGE_ID_PREFIX + messageId;
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }
}
