package com.dongfeng.springboot.consumer;

import com.dongfeng.springboot.config.RabbitMQConfig;
import com.dongfeng.springboot.message.LotteryMessage;
import com.dongfeng.springboot.service.LotteryService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 抽奖消息消费者
 */
@Slf4j
@Component
public class LotteryConsumer {

    @Autowired
    private LotteryService lotteryService;

    /**
     * 处理抽奖消息
     * 
     * @param message 消息内容
     * @param channel 通道
     * @param deliveryTag 投递标签
     * @throws IOException IO异常
     */
    @RabbitListener(queues = RabbitMQConfig.LOTTERY_QUEUE)
    public void handleLotteryMessage(LotteryMessage message, Channel channel, 
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("收到抽奖消息：{}", message);
        
        try {
            // 处理消息（幂等性处理）
            boolean success = lotteryService.processLotteryMessage(message);
            
            if (success) {
                // 处理成功，手动确认消息
                channel.basicAck(deliveryTag, false);
                log.info("消息处理成功并确认，messageId: {}", message.getMessageId());
            } else {
                // 处理失败，拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
                log.warn("消息处理失败，拒绝消息并重新入队，messageId: {}", message.getMessageId());
            }
        } catch (Exception e) {
            log.error("消息处理异常，messageId: {}", message.getMessageId(), e);
            
            // 处理异常，拒绝消息并重新入队（最大重试次数由RabbitMQ配置控制）
            channel.basicNack(deliveryTag, false, true);
        }
    }
    
    /**
     * 处理死信队列消息
     * 
     * @param message 消息内容
     * @param channel 通道
     * @param deliveryTag 投递标签
     * @throws IOException IO异常
     */
    @RabbitListener(queues = RabbitMQConfig.LOTTERY_DEAD_LETTER_QUEUE)
    public void handleDeadLetterMessage(Message message, Channel channel, 
                                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.error("收到死信队列消息：{}", new String(message.getBody()));
        
        // 记录死信消息，可以进行告警或其他处理
        // 这里简单处理，直接确认消息
        channel.basicAck(deliveryTag, false);
    }
}
