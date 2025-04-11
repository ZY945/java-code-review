package com.dongfeng.springbootmvc.server.coupon.repository;

import com.dongfeng.springbootmvc.server.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT COUNT(u) FROM UserCoupon u WHERE u.userId = :userId AND u.templateId = :templateId")
    long countByUserIdAndTemplateId(@Param("userId") Long userId, @Param("templateId") Long templateId);

    List<UserCoupon> findByUserIdAndStatus(Long userId, Integer status);
} 