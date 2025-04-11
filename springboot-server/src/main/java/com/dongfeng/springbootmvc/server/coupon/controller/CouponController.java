package com.dongfeng.springbootmvc.server.coupon.controller;

import com.dongfeng.springbootmvc.common.Result;
import com.dongfeng.springbootmvc.server.coupon.dto.CouponResponse;
import com.dongfeng.springbootmvc.server.coupon.dto.CouponTemplateRequest;
import com.dongfeng.springbootmvc.server.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/templates")
    public Result<CouponResponse> createTemplate(
            @RequestBody @Valid CouponTemplateRequest request) {
        return Result.success(couponService.createTemplate(request));
    }

    @GetMapping("/templates")
    public Result<List<CouponResponse>> listTemplates() {
        return Result.success(couponService.listAvailableTemplates());
    }

    @PostMapping("/grab/{templateId}")
    public Result<Void> grabCoupon(
            @PathVariable Long templateId,
            @RequestHeader("X-User-Id") Long userId) {
        couponService.grabCoupon(templateId, userId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<CouponResponse>> getMyCoupons(
            @RequestHeader("X-User-Id") Long userId) {
        return Result.success(couponService.getUserCoupons(userId));
    }
} 