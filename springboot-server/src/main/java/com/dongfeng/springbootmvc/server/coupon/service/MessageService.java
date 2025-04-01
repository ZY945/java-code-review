package com.dongfeng.springbootmvc.server.coupon.service;

import com.dongfeng.springbootmvc.config.RabbitMQConfig;
import com.dongfeng.springbootmvc.server.coupon.dto.CouponMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final RabbitTemplate rabbitTemplate;
    private final ThreadPoolExecutor couponTaskExecutor;
    
    @Async("couponTaskExecutor")
    public CompletableFuture<Boolean> sendToMessageQueue(Long templateId, Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CouponMessage message = new CouponMessage(templateId, userId);
                rabbitTemplate.convertAndSend(RabbitMQConfig.COUPON_QUEUE, message);
                log.info("消息发送成功: templateId={}, userId={}", templateId, userId);
                return true;
            } catch (Exception e) {
                log.error("消息发送失败: templateId={}, userId={}", templateId, userId, e);
                throw new RuntimeException("消息发送失败", e);
            }
        }, couponTaskExecutor);
    }
} 