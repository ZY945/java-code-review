package com.dongfeng.springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板实体类
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon_template")
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 模板名称
     */
    @Column(nullable = false, length = 64)
    private String name;

    /**
     * 模板描述
     */
    @Column(length = 256)
    private String description;

    /**
     * 优惠券类型：1-满减券，2-折扣券，3-立减券，4-满赠券
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 优惠券面值
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    /**
     * 使用门槛（满多少金额可使用）
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal threshold;

    /**
     * 优惠券有效期开始时间
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * 优惠券有效期结束时间
     */
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * 优惠券状态：0-未启用，1-已启用，2-已过期
     */
    @Column(nullable = false)
    private Integer status;

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
