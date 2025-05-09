package com.dongfeng.springboot.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 抽奖队列名称
     */
    public static final String LOTTERY_QUEUE = "lottery.queue";
    
    /**
     * 抽奖交换机名称
     */
    public static final String LOTTERY_EXCHANGE = "lottery.exchange";
    
    /**
     * 抽奖路由键
     */
    public static final String LOTTERY_ROUTING_KEY = "lottery.routing.key";
    
    /**
     * 死信队列名称
     */
    public static final String LOTTERY_DEAD_LETTER_QUEUE = "lottery.dead.letter.queue";
    
    /**
     * 死信交换机名称
     */
    public static final String LOTTERY_DEAD_LETTER_EXCHANGE = "lottery.dead.letter.exchange";
    
    /**
     * 死信路由键
     */
    public static final String LOTTERY_DEAD_LETTER_ROUTING_KEY = "lottery.dead.letter.routing.key";

    /**
     * 声明交换机
     */
    @Bean
    public DirectExchange lotteryExchange() {
        return new DirectExchange(LOTTERY_EXCHANGE);
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue lotteryQueue() {
        // 设置死信交换机和路由键
        return QueueBuilder.durable(LOTTERY_QUEUE)
                .withArgument("x-dead-letter-exchange", LOTTERY_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", LOTTERY_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding lotteryBinding() {
        return BindingBuilder.bind(lotteryQueue()).to(lotteryExchange()).with(LOTTERY_ROUTING_KEY);
    }

    /**
     * 声明死信交换机
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(LOTTERY_DEAD_LETTER_EXCHANGE);
    }

    /**
     * 声明死信队列
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(LOTTERY_DEAD_LETTER_QUEUE).build();
    }

    /**
     * 绑定死信队列和死信交换机
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(LOTTERY_DEAD_LETTER_ROUTING_KEY);
    }

    /**
     * 配置消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        
        // 消息发送确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.out.println("消息发送失败：" + cause);
            }
        });
        
        // 消息发送失败回调
        rabbitTemplate.setReturnsCallback(returned -> {
            System.out.println("消息发送失败，返回消息：" + returned.getMessage());
        });
        
        return rabbitTemplate;
    }
}
