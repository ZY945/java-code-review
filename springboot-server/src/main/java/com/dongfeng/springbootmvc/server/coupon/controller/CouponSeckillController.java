package com.dongfeng.springbootmvc.server.coupon.controller;

import com.dongfeng.springbootmvc.server.coupon.service.CouponSeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import com.dongfeng.springbootmvc.common.Result;
import com.dongfeng.springbootmvc.common.BizException;
import com.dongfeng.springbootmvc.server.coupon.dto.PreloadRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/coupon/seckill")
@RequiredArgsConstructor
public class CouponSeckillController {

    private final CouponSeckillService seckillService;
    private final RedisScript<Long> limitScript;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/{templateId}")
    public Result<Void> seckill(
            @PathVariable Long templateId,
            @RequestHeader("X-User-Id") Long userId) {
        String limitKey = "rate_limit:" + templateId;
        Long result = redisTemplate.execute(
            limitScript,
            Collections.singletonList(limitKey),
            "100",
            "1"
        );
        
        if (result != null && result == 0) {
            throw new BizException(429, "请求太频繁，请稍后再试");
        }
        
        seckillService.seckill(templateId, userId);
        return Result.success();
    }

    @PostMapping("/preload")
    public Result<Void> preloadCoupon(@RequestBody @Valid PreloadRequest request) {
        seckillService.preloadCouponToRedis(request);
        return Result.success();
    }
} 