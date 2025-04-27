package com.dongfeng.springboot.service;

import com.dongfeng.springboot.entity.Coupon;
import com.dongfeng.springboot.entity.CouponTemplate;
import com.dongfeng.springboot.repository.CouponRepository;
import com.dongfeng.springboot.repository.CouponTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 优惠券服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponTemplateRepository couponTemplateRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    /**
     * 批量生成优惠券记录（使用JPA批量保存）
     * 
     * @param count 生成数量
     * @return 生成的优惠券数量
     */
    @Transactional
    public int generateCoupons(int count) {
        log.info("开始生成{}张优惠券", count);
        long startTime = System.currentTimeMillis();

        // 获取所有可用的优惠券模板
        List<CouponTemplate> templates = couponTemplateRepository.findAll();
        if (templates.isEmpty()) {
            log.error("没有可用的优惠券模板，无法生成优惠券");
            return 0;
        }

        int templateSize = templates.size();
        List<Coupon> coupons = new ArrayList<>(1000); // 每批次1000条
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            // 随机选择一个模板
            CouponTemplate template = templates.get(random.nextInt(templateSize));
            
            // 随机用户ID (1-100000)
            Long userId = (long) (random.nextInt(100000) + 1);
            
            // 创建优惠券
            Coupon coupon = Coupon.builder()
                    .couponCode(UUID.randomUUID().toString().replace("-", ""))
                    .userId(userId)
                    .templateId(template.getId())
                    .status(0) // 未使用
                    .assignTime(now)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            
            coupons.add(coupon);
            
            // 每1000条保存一次，避免内存占用过多
            if (coupons.size() >= 10000 || i == count - 1) {
                couponRepository.saveAll(coupons);
                coupons.clear();
                log.info("已生成{}张优惠券", i);
            }
        }
        
        long endTime = System.currentTimeMillis();
        log.info("生成{}张优惠券完成，耗时{}ms", count, (endTime - startTime));
        
        return count;
    }
    
    /**
     * 批量生成优惠券记录（使用JDBC批量插入，性能更高）
     * 
     * @param count 生成数量
     * @return 生成的优惠券数量
     */
    @Transactional
    public int generateCouponsByJdbc(int count) {
        log.info("开始使用JDBC批量生成{}张优惠券", count);
        long startTime = System.currentTimeMillis();

        // 获取所有可用的优惠券模板
        List<CouponTemplate> templates = couponTemplateRepository.findAll();
        if (templates.isEmpty()) {
            log.error("没有可用的优惠券模板，无法生成优惠券");
            return 0;
        }

        int templateSize = templates.size();
        int batchSize = 10000; // 每批次插入数量
        LocalDateTime now = LocalDateTime.now();
        String sql = "INSERT INTO coupon (coupon_code, user_id, template_id, status, assign_time, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < count; i += batchSize) {
            int currentBatchSize = Math.min(batchSize, count - i);
            List<Object[]> batchArgs = new ArrayList<>(currentBatchSize);
            
            for (int j = 0; j < currentBatchSize; j++) {
                // 随机选择一个模板
                CouponTemplate template = templates.get(random.nextInt(templateSize));
                
                // 随机用户ID (1-100000)
                Long userId = (long) (random.nextInt(100000) + 1);
                
                // 准备批量插入的参数
                Object[] args = new Object[] {
                    UUID.randomUUID().toString().replace("-", ""),
                    userId,
                    template.getId(),
                    0, // 未使用
                    now,
                    now,
                    now
                };
                
                batchArgs.add(args);
            }
            
            // 执行批量插入
            jdbcTemplate.batchUpdate(sql, batchArgs);
            
            log.info("已生成{}张优惠券", i + currentBatchSize);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("使用JDBC批量生成{}张优惠券完成，耗时{}ms", count, (endTime - startTime));
        
        return count;
    }

    public Coupon selectById(Long id){
        return couponRepository.searchCouponById(id);
    }
}
