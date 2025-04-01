package com.dongfeng.springbootmvc.server.coupon.mq;

import com.dongfeng.springbootmvc.config.RabbitMQConfig;
import com.dongfeng.springbootmvc.server.coupon.dto.CouponMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponMessageListener {

    private final CouponMessageHandler couponMessageHandler;

    @RabbitListener(queues = RabbitMQConfig.COUPON_QUEUE)
    public void handleCouponMessage(CouponMessage message) {
        try {
            log.info("收到优惠券消息: {}", message);
            couponMessageHandler.handleCouponGrab(message.getTemplateId(), message.getUserId());
            log.info("处理优惠券消息成功: {}", message);
        } catch (Exception e) {
            log.error("处理优惠券消息失败: {}", message, e);
            // 这里可以添加重试逻辑或者将失败消息写入死信队列
            throw e;
        }
    }
} 