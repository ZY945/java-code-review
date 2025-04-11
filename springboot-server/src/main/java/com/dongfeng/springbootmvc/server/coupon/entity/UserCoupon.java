package com.dongfeng.springbootmvc.server.coupon.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "c_user_coupon")
@org.hibernate.annotations.Table(
        appliesTo = "c_user_coupon",
        comment = "用户优惠券表"
)
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "主键")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "用户ID")
    private Long userId;

    @Column(name = "template_id", nullable = false, columnDefinition = "优惠券模板ID")
    private Long templateId;

    @Column(name = "status", nullable = false, columnDefinition = "状态:1-未使用,2-已使用,3-已过期")
    @ColumnDefault("1")
    private Integer status = 1;

    @Column(name = "order_id", columnDefinition = "使用的订单ID")
    private Long orderId;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "创建时间")
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false, columnDefinition = "更新时间")
    private LocalDateTime updateTime;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "版本号")
    private Integer version = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private CouponTemplate couponTemplate;
} 