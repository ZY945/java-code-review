package com.dongfeng.springboot.repository;

import com.dongfeng.springboot.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 优惠券记录数据访问层
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Coupon searchCouponById(Long id);
}
