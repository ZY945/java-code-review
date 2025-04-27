package com.dongfeng.springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 优惠券记录实体类
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_template_id", columnList = "templateId"),
    @Index(name = "idx_status", columnList = "status")
})
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 优惠券码
     */
    @Column(nullable = false, length = 64, unique = true)
    private String couponCode;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 优惠券模板ID
     */
    @Column(nullable = false)
    private Long templateId;

    /**
     * 优惠券状态：0-未使用，1-已使用，2-已过期
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 领取时间
     */
    @Column(nullable = false)
    private LocalDateTime assignTime;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * 订单ID（使用时关联的订单）
     */
    private Long orderId;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
