package com.dongfeng.springbootmvc.server.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Integer type;
    private BigDecimal discount;
    private BigDecimal threshold;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private Long userCouponId;
} 