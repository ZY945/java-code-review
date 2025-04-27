package com.dongfeng.springboot.service;

import com.dongfeng.springboot.entity.CouponTemplate;
import com.dongfeng.springboot.repository.CouponTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 优惠券模板服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponTemplateService {

    private final CouponTemplateRepository couponTemplateRepository;
    private final Random random = new Random();

    /**
     * 批量生成优惠券模板
     * @param count 生成数量
     * @return 生成的模板数量
     */
    @Transactional
    public int generateCouponTemplates(int count) {
        log.info("开始生成{}个优惠券模板", count);
        long startTime = System.currentTimeMillis();
        
        List<CouponTemplate> templates = new ArrayList<>(count);
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < count; i++) {
            int type = random.nextInt(4) + 1; // 1-4
            BigDecimal value;
            BigDecimal threshold = null;
            
            // 根据类型设置不同的面值和门槛
            switch (type) {
                case 1: // 满减券
                    threshold = new BigDecimal(random.nextInt(10) * 10 + 50); // 50-140
                    value = new BigDecimal(random.nextInt(5) * 5 + 10); // 10-30
                    break;
                case 2: // 折扣券
                    value = new BigDecimal(random.nextInt(5) + 5).divide(new BigDecimal(10)); // 0.5-0.9
                    break;
                case 3: // 立减券
                    value = new BigDecimal(random.nextInt(10) + 1); // 1-10
                    break;
                case 4: // 满赠券
                    threshold = new BigDecimal(random.nextInt(10) * 10 + 100); // 100-190
                    value = BigDecimal.ONE; // 表示赠品
                    break;
                default:
                    value = BigDecimal.TEN;
            }
            
            // 创建模板
            CouponTemplate template = CouponTemplate.builder()
                    .name("优惠券模板-" + (i + 1))
                    .description("这是第" + (i + 1) + "个优惠券模板")
                    .type(type)
                    .value(value)
                    .threshold(threshold)
                    .startTime(now)
                    .endTime(now.plusMonths(random.nextInt(6) + 1)) // 1-6个月有效期
                    .status(1) // 已启用
                    .createTime(now)
                    .updateTime(now)
                    .build();
            
            templates.add(template);
            
            // 每1000条保存一次，避免内存占用过多
            if (templates.size() >= 1000 || i == count - 1) {
                couponTemplateRepository.saveAll(templates);
                templates.clear();
                log.info("已生成{}个优惠券模板", i + 1);
            }
        }
        
        long endTime = System.currentTimeMillis();
        log.info("生成{}个优惠券模板完成，耗时{}ms", count, (endTime - startTime));
        
        return count;
    }
}
