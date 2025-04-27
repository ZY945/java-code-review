package com.dongfeng.springboot.repository;

import com.dongfeng.springboot.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 优惠券模板数据访问层
 */
@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
}
