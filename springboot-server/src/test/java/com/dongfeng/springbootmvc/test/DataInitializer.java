package com.dongfeng.springbootmvc.test;

import com.dongfeng.springbootmvc.server.coupon.entity.CouponTemplate;
import com.dongfeng.springbootmvc.server.coupon.repository.CouponTemplateRepository;
import com.dongfeng.springbootmvc.server.user.entity.User;
import com.dongfeng.springbootmvc.server.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CouponTemplateRepository templateRepository;

    @Test
    public void initTestData() {
        // 1. 初始化用户数据
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("test_user_" + (10001 + i));
            users.add(user);
        }
        userRepository.saveAll(users);
        log.info("用户数据初始化完成，共{}条", users.size());

        // 2. 初始化优惠券模板
        CouponTemplate template = new CouponTemplate();
        template.setName("双11优惠券");
        template.setDescription("满100减50");
        template.setType(1);
        template.setDiscount(new BigDecimal("50.00"));
        template.setThreshold(new BigDecimal("100.00"));
        template.setTotal(1000);
        template.setRemaining(1000);
        template.setStartTime(LocalDateTime.now());
        template.setEndTime(LocalDateTime.now().plusMonths(1));
        template.setStatus(2);
        templateRepository.save(template);
        log.info("优惠券模板初始化完成");
    }
} 