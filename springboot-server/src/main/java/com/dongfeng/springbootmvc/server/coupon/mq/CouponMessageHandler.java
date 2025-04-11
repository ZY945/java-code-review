package com.dongfeng.springbootmvc.server.coupon.mq;

import com.dongfeng.springbootmvc.common.BizException;
import com.dongfeng.springbootmvc.server.coupon.entity.UserCoupon;
import com.dongfeng.springbootmvc.server.coupon.repository.CouponTemplateRepository;
import com.dongfeng.springbootmvc.server.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponMessageHandler {

    private final CouponTemplateRepository templateRepository;
    private final UserCouponRepository userCouponRepository;
    private static final Logger log = LoggerFactory.getLogger(CouponMessageHandler.class);

    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Transactional(rollbackFor = Exception.class)
    public void handleCouponGrab(Long templateId, Long userId) {
        try {
            // 1. 检查是否已领取
            if (userCouponRepository.countByUserIdAndTemplateId(userId, templateId) > 0) {
                log.warn("用户{}已领取过优惠券{}", userId, templateId);
                return;
            }

            // 2. 扣减数据库库存
            int updated = templateRepository.decreaseStock(templateId);
            if (updated == 0) {
                throw new BizException(400, "优惠券库存不足");
            }

            // 3. 保存用户优惠券
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setTemplateId(templateId);
            userCoupon.setStatus(1);
            userCouponRepository.save(userCoupon);

            log.info("用户{}领取优惠券{}成功", userId, templateId);
        } catch (Exception e) {
            log.error("处理优惠券消息失败: templateId={}, userId={}", templateId, userId, e);
            throw e;
        }
    }
} 