package com.dongfeng.springbootmvc.inventory.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁实现
 */
@Slf4j
@Component
public class RedisDistributedLock {

    private final StringRedisTemplate redisTemplate;

    public RedisDistributedLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁的键
     * @param requestId  请求标识（用于释放锁时验证）
     * @param expireTime 锁的过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("获取分布式锁异常", e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁的键
     * @param requestId 请求标识（用于验证是否为锁的持有者）
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long result = redisTemplate.execute((connection) -> {
                return connection.scriptingCommands().eval(
                        script.getBytes(),
                        org.springframework.data.redis.connection.ReturnType.INTEGER,
                        1,
                        lockKey.getBytes(),
                        requestId.getBytes()
                );
            });
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("释放分布式锁异常", e);
            return false;
        }
    }
}
