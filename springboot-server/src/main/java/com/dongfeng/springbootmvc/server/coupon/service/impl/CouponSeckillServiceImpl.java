package com.dongfeng.springbootmvc.server.coupon.service.impl;

import com.dongfeng.springbootmvc.common.BizException;
import com.dongfeng.springbootmvc.server.coupon.dto.PreloadRequest;
import com.dongfeng.springbootmvc.server.coupon.entity.CouponTemplate;
import com.dongfeng.springbootmvc.server.coupon.repository.CouponTemplateRepository;
import com.dongfeng.springbootmvc.server.coupon.repository.UserCouponRepository;
import com.dongfeng.springbootmvc.server.coupon.service.CouponSeckillService;
import com.dongfeng.springbootmvc.server.coupon.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponSeckillServiceImpl implements CouponSeckillService {

    private final StringRedisTemplate redisTemplate;
    private final CouponTemplateRepository templateRepository;
    private final UserCouponRepository userCouponRepository;
    private final MessageService messageService;
    private final ThreadPoolExecutor couponTaskExecutor;
    private final TransactionTemplate transactionTemplate;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String LIMIT_KEY_PREFIX = "seckill:limit:";
    private static final String ACTIVITY_KEY_PREFIX = "seckill:activity:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void preloadCouponToRedis(PreloadRequest request) {
        String stockKey = STOCK_KEY_PREFIX + request.getTemplateId();
        String activityKey = ACTIVITY_KEY_PREFIX + request.getActivityId();

        // 1. 检查Redis中是否已存在库存,避免重复预热
        if (Boolean.TRUE.equals(redisTemplate.hasKey(activityKey))) {
            throw new BizException(400, "该活动优惠券已被预热");
        }

        // 2. 在事务中执行数据库操作
        transactionTemplate.execute(status -> {
            try {
                // 2.1 检查优惠券模板是否存在且有效
                CouponTemplate template = templateRepository.findById(request.getTemplateId())
                        .orElseThrow(() -> new BizException(404, "优惠券模板不存在"));

                if (template.getRemaining() < request.getCount()) {
                    throw new BizException(400, "库存不足");
                }

                // 2.2 扣减数据库库存
                int updated = templateRepository.decreaseStock(
                        request.getTemplateId(),
                        request.getCount()
                );

                if (updated == 0) {
                    throw new BizException(400, "扣减库存失败");
                }

                // 2.3 将库存预热到Redis
                redisTemplate.execute(new SessionCallback<Object>() {
                    @Override
                    public Object execute(RedisOperations operations) throws DataAccessException {
                        operations.multi();

                        // 设置库存
                        operations.opsForValue().set(
                                stockKey,
                                String.valueOf(request.getCount()),
                                24,
                                TimeUnit.HOURS
                        );

                        // 设置活动信息
                        Map<String, String> activityInfo = new HashMap<>();
                        activityInfo.put("templateId", request.getTemplateId().toString());
                        activityInfo.put("count", request.getCount().toString());
                        activityInfo.put("startTime", request.getStartTime().toString());
                        activityInfo.put("endTime", request.getEndTime().toString());

                        operations.opsForHash().putAll(activityKey, activityInfo);
                        operations.expire(activityKey, 24, TimeUnit.HOURS);

                        return operations.exec();
                    }
                });

                log.info("优惠券预热成功: templateId={}, count={}, activityId={}",
                        request.getTemplateId(), request.getCount(), request.getActivityId());

                return null;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void seckill(Long templateId, Long userId) {
        String stockKey = STOCK_KEY_PREFIX + templateId;
        String limitKey = LIMIT_KEY_PREFIX + templateId + ":" + userId;

        try {
            // 1. 用户限制检查
            Boolean notLimited = redisTemplate.opsForValue().setIfAbsent(limitKey, "1", 24, TimeUnit.HOURS);
            if (Boolean.FALSE.equals(notLimited)) {
                throw new BizException(400, "已参与过秒杀");
            }

            // 2. Redis原子性扣减库存
            Long stock = redisTemplate.opsForValue().decrement(stockKey);
            if (stock == null || stock < 0) {
                handleFailure(stockKey, limitKey);
                throw new BizException(400, "库存不足");
            }

            // 3. 异步处理数据库操作
            CompletableFuture.runAsync(() -> {
                try {
                    messageService.sendToMessageQueue(templateId, userId);
                } catch (Exception e) {
                    log.error("异步处理失败，templateId: {}, userId: {}", templateId, userId, e);
                    handleFailure(stockKey, limitKey);
                    throw new BizException("秒杀失败");
                }
            }, couponTaskExecutor).exceptionally(throwable -> {
                log.error("异步处理异常", throwable);
                handleFailure(stockKey, limitKey);
                throw new BizException("系统繁忙，请稍后再试");
            });

        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            handleFailure(stockKey, limitKey);
            log.error("秒杀异常，templateId: {}, userId: {}", templateId, userId, e);
            throw new BizException("系统异常");
        }
    }

    private void handleFailure(String stockKey, String limitKey) {
        try {
            redisTemplate.opsForValue().increment(stockKey);
            redisTemplate.delete(limitKey);
        } catch (Exception e) {
            log.error("回滚Redis数据失败", e);
        }
    }
} 