package com.dongfeng.springbootmvc.server.coupon.service;

import com.dongfeng.springbootmvc.server.coupon.dto.PreloadRequest;

public interface CouponSeckillService {
    void seckill(Long templateId, Long userId);
    void preloadCouponToRedis(PreloadRequest request);
} 