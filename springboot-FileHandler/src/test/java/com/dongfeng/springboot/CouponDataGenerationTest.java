package com.dongfeng.springboot;

import com.dongfeng.springboot.entity.Coupon;
import com.dongfeng.springboot.service.CouponService;
import com.dongfeng.springboot.service.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 优惠券数据生成测试类
 */
@Slf4j
@SpringBootTest
public class CouponDataGenerationTest {

    @Autowired
    private CouponTemplateService couponTemplateService;

    @Autowired
    private CouponService couponService;

    /**
     * 测试生成5000个优惠券模板
     */
    @Test
    public void testGenerateCouponTemplates() {
        log.info("开始测试生成优惠券模板");
        int count = 5000;
        int result = couponTemplateService.generateCouponTemplates(count);
        log.info("成功生成{}个优惠券模板", result);
    }

    /**
     * 测试使用JPA方式生成100万张优惠券（用于小规模测试）
     */
    @Test
    public void testGenerateCoupons() {
        log.info("开始测试生成优惠券");
        int count = 1_000_000; // 100万张优惠券
        int result = couponService.generateCoupons(count);
        log.info("成功生成{}张优惠券", result);
        // 生成1000000张优惠券完成，耗时416762ms
    }

    /**
     * 测试100万张优惠券查询
     */
    @Test
    public void testSelectCoupons() {
        long start = System.currentTimeMillis();
        Coupon result = couponService.selectById(468004L);
        long end = System.currentTimeMillis();
        System.out.println("result:" + result + "\n" + "耗时:" + (end - start) / 1000);
    }

    /**
     * 测试使用JDBC批量插入方式生成1000万张优惠券
     * 注意：此测试需要较长时间运行，请确保数据库有足够空间
     */
    @Test
    public void testGenerateLargeCoupons() {
        log.info("开始测试生成大量优惠券");
        int count = 10_000_000; // 1000万张优惠券
        int result = couponService.generateCouponsByJdbc(count);
        log.info("成功生成{}张优惠券", result);
    }

    /**
     * 完整测试：先生成模板，再生成优惠券
     * 注意：此测试将顺序执行所有步骤，需要较长时间运行
     */
    @Test
    public void testCompleteDataGeneration() {
        log.info("开始完整数据生成测试");

        // 1. 生成5000个优惠券模板
        log.info("第一步：生成优惠券模板");
        int templateCount = 5000;
        int templateResult = couponTemplateService.generateCouponTemplates(templateCount);
        log.info("成功生成{}个优惠券模板", templateResult);

        // 2. 生成1000万张优惠券
        log.info("第二步：生成优惠券记录");
        int couponCount = 10_000_000;
        int couponResult = couponService.generateCouponsByJdbc(couponCount);
        log.info("成功生成{}张优惠券", couponResult);

        log.info("完整数据生成测试完成");
    }
}
