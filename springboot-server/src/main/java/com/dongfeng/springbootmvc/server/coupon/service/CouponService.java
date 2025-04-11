package com.dongfeng.springbootmvc.server.coupon.service;

import com.dongfeng.springbootmvc.server.coupon.dto.CouponResponse;
import com.dongfeng.springbootmvc.server.coupon.dto.CouponTemplateRequest;

import java.util.List;

public interface CouponService {
    CouponResponse createTemplate(CouponTemplateRequest request);

    List<CouponResponse> listAvailableTemplates();

    void grabCoupon(Long templateId, Long userId);

    List<CouponResponse> getUserCoupons(Long userId);
} 