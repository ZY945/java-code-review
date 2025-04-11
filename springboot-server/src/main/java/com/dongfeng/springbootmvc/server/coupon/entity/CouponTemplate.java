package com.dongfeng.springbootmvc.server.coupon.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "c_coupon_template")
@org.hibernate.annotations.Table(
        appliesTo = "c_coupon_template",
        comment = "优惠券模板表"
)
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "主键")
    private Long id;

    @Column(name = "name", nullable = false, length = 64, columnDefinition = "优惠券名称")
    private String name;

    @Column(name = "description", length = 256, columnDefinition = "描述")
    private String description;

    @Column(name = "type", nullable = false, columnDefinition = "券类型:1-满减券,2-折扣券,3-立减券")
    private Integer type;

    @Column(name = "discount", nullable = false, precision = 10, scale = 2, columnDefinition = "优惠金额或折扣率")
    private BigDecimal discount;

    @Column(name = "threshold", precision = 10, scale = 2, columnDefinition = "使用门槛金额")
    private BigDecimal threshold;

    @Column(name = "total", nullable = false, columnDefinition = "发行数量")
    private Integer total;

    @Column(name = "remaining", nullable = false, columnDefinition = "剩余数量")
    private Integer remaining;

    @Column(name = "start_time", nullable = false, columnDefinition = "有效期开始时间")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "有效期结束时间")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false, columnDefinition = "状态:1-未开始,2-进行中,3-已结束,4-已关闭")
    @ColumnDefault("1")
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "创建时间")
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false, columnDefinition = "更新时间")
    private LocalDateTime updateTime;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "版本号")
    private Integer version = 0;
} 