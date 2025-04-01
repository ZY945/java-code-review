package com.dongfeng.springbootmvc.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RabbitMQConfig {
    
    public static final String COUPON_QUEUE = "coupon.queue.v2";
    public static final String COUPON_DLX_QUEUE = "coupon.dlx.queue.v2";
    public static final String COUPON_DLX_EXCHANGE = "coupon.dlx.exchange.v2";
    public static final String COUPON_DLX_ROUTING_KEY = "coupon.dlx";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue couponQueue() {
        return QueueBuilder.durable(COUPON_QUEUE)
                .withArgument("x-dead-letter-exchange", COUPON_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", COUPON_DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // 消息过期时间：1分钟
                .build();
    }

    @Bean
    public Queue couponDlxQueue() {
        return QueueBuilder.durable(COUPON_DLX_QUEUE).build();
    }

    @Bean
    public DirectExchange couponDlxExchange() {
        return new DirectExchange(COUPON_DLX_EXCHANGE);
    }

    @Bean
    public Binding couponDlxBinding() {
        return BindingBuilder.bind(couponDlxQueue())
                .to(couponDlxExchange())
                .with(COUPON_DLX_ROUTING_KEY);
    }

    @Bean
    @DependsOn({"couponQueue", "couponDlxQueue", "couponDlxExchange", "couponDlxBinding"})
    public Object initializeQueues(RabbitAdmin rabbitAdmin) {
        // 删除旧队列（如果存在）
        rabbitAdmin.deleteQueue("coupon.queue");
        rabbitAdmin.deleteQueue("coupon.dlx.queue");
        rabbitAdmin.deleteExchange("coupon.dlx.exchange");

        // 声明新队列和交换机
        rabbitAdmin.declareQueue(couponQueue());
        rabbitAdmin.declareQueue(couponDlxQueue());
        rabbitAdmin.declareExchange(couponDlxExchange());
        rabbitAdmin.declareBinding(couponDlxBinding());

        return new Object(); // 返回一个空对象
    }
} 