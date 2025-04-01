package com.dongfeng.springbootmvc.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitListenerConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);  // 设置并发消费者数量
        factory.setMaxConcurrentConsumers(10);  // 最大并发消费者数量
        
        // 配置重试策略
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);  // 初始重试间隔：1秒
        backOffPolicy.setMultiplier(2.0);        // 间隔倍数
        backOffPolicy.setMaxInterval(10000);     // 最大间隔：10秒
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        factory.setRetryTemplate(retryTemplate);
        
        return factory;
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, 
            RabbitMQConfig.COUPON_DLX_EXCHANGE, 
            RabbitMQConfig.COUPON_DLX_ROUTING_KEY);
    }
} 