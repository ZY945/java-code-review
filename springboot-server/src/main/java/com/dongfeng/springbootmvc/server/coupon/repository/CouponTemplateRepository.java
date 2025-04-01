package com.dongfeng.springbootmvc.server.coupon.repository;

import com.dongfeng.springbootmvc.server.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    
    @Modifying
    @Query("UPDATE CouponTemplate c SET c.remaining = c.remaining - 1 WHERE c.id = :id AND c.remaining > 0")
    int decreaseStock(@Param("id") Long id);

    // 修改数据库库存
    @Modifying
    @Query("UPDATE CouponTemplate c SET c.remaining = c.remaining - :count WHERE c.id = :id AND c.remaining >= :count")
    int decreaseStock(@Param("id") Long id, @Param("count") Integer count);
    
    @Query("SELECT c FROM CouponTemplate c WHERE c.id = :id AND c.status = 2 AND c.remaining > 0")
    CouponTemplate findAvailableTemplate(@Param("id") Long id);
    
    List<CouponTemplate> findByStatus(Integer status);
} 