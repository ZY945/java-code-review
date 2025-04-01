package com.dongfeng.springbootmvc.server.coupon.service.impl;

import com.dongfeng.springbootmvc.common.BizException;
import com.dongfeng.springbootmvc.server.coupon.dto.CouponResponse;
import com.dongfeng.springbootmvc.server.coupon.dto.CouponTemplateRequest;
import com.dongfeng.springbootmvc.server.coupon.entity.CouponTemplate;
import com.dongfeng.springbootmvc.server.coupon.entity.UserCoupon;
import com.dongfeng.springbootmvc.server.coupon.repository.CouponTemplateRepository;
import com.dongfeng.springbootmvc.server.coupon.repository.UserCouponRepository;
import com.dongfeng.springbootmvc.server.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final StringRedisTemplate redisTemplate;
    private final CouponTemplateRepository templateRepository;
    private final UserCouponRepository userCouponRepository;
    
    private static final String COUPON_LOCK_PREFIX = "coupon:lock:";
    private static final String TEMPLATE_CACHE_PREFIX = "coupon:template:";
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grabCoupon(Long templateId, Long userId) {
        if (templateId == null || userId == null) {
            throw new BizException(400, "参数不能为空");
        }

        String lockKey = COUPON_LOCK_PREFIX + templateId + ":" + userId;
        try {
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(locked)) {
                throw new BizException(400, "重复请求");
            }
            
            // 检查优惠券是否可用
            CouponTemplate template = templateRepository.findAvailableTemplate(templateId);
            if (template == null) {
                throw new BizException(400, "优惠券不可用");
            }
            
            // 检查是否已领取
            if (userCouponRepository.countByUserIdAndTemplateId(userId, templateId) > 0) {
                throw new BizException(400, "已经领取过该优惠券");
            }
            
            // 扣减库存
            int updated = templateRepository.decreaseStock(templateId);
            if (updated == 0) {
                throw new BizException(400, "优惠券已抢光");
            }
            
            // 保存用户优惠券
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setTemplateId(templateId);
            userCoupon.setStatus(1);
            userCouponRepository.save(userCoupon);
            
            // 清除相关缓存
            redisTemplate.delete(TEMPLATE_CACHE_PREFIX + templateId);
            
            log.info("用户{}成功领取优惠券{}", userId, templateId);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("领取优惠券异常, templateId: {}, userId: {}", templateId, userId, e);
            throw new BizException("领取优惠券失败");
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
    
    @Override
    @Cacheable(value = "userCoupons", key = "#userId")
    public List<CouponResponse> getUserCoupons(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        try {
            // 获取用户的优惠券列表
            List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndStatus(userId, 1);
            if (CollectionUtils.isEmpty(userCoupons)) {
                return Collections.emptyList();
            }
            
            // 批量查询优惠券模板
            Set<Long> templateIds = userCoupons.stream()
                .map(UserCoupon::getTemplateId)
                .collect(Collectors.toSet());
            
            Map<Long, CouponTemplate> templateMap = templateRepository.findAllById(templateIds)
                .stream()
                .collect(Collectors.toMap(CouponTemplate::getId, template -> template));
            
            // 组装响应数据
            return userCoupons.stream()
                .map(userCoupon -> {
                    CouponTemplate template = templateMap.get(userCoupon.getTemplateId());
                    if (template == null) {
                        log.warn("Template {} not found for user coupon {}", userCoupon.getTemplateId(), userCoupon.getId());
                        return null;
                    }
                    CouponResponse response = convertToResponse(template);
                    response.setUserCouponId(userCoupon.getId());
                    response.setStatus(userCoupon.getStatus());
                    return response;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while getting user coupons for user: {}", userId, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponResponse createTemplate(CouponTemplateRequest request) {
        CouponTemplate template = new CouponTemplate();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setType(request.getType());
        template.setDiscount(request.getDiscount());
        template.setThreshold(request.getThreshold());
        template.setTotal(request.getTotal());
        template.setRemaining(request.getTotal());
        template.setStartTime(request.getStartTime());
        template.setEndTime(request.getEndTime());
        template.setStatus(1);
        
        CouponTemplate saved = templateRepository.save(template);
        return convertToResponse(saved);
    }

    @Override
    @Cacheable(value = "availableTemplates")
    public List<CouponResponse> listAvailableTemplates() {
        try {
            List<CouponTemplate> templates = templateRepository.findByStatus(2);
            if (CollectionUtils.isEmpty(templates)) {
                return Collections.emptyList();
            }
            
            return templates.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while listing available templates", e);
            throw e;
        }
    }

    private CouponResponse convertToResponse(CouponTemplate template) {
        CouponResponse response = new CouponResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setType(template.getType());
        response.setDiscount(template.getDiscount());
        response.setThreshold(template.getThreshold());
        response.setStartTime(template.getStartTime());
        response.setEndTime(template.getEndTime());
        response.setStatus(template.getStatus());
        return response;
    }
} 