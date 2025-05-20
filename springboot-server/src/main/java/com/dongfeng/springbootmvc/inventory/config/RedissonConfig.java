package com.dongfeng.springbootmvc.inventory.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    /**
     * 创建Redisson客户端
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // 使用单节点模式
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password)
                .setDatabase(0)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(10)
                .setConnectTimeout(10000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        
        return Redisson.create(config);
    }
}
