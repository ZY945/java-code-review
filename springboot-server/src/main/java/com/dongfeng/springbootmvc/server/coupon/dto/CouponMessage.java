package com.dongfeng.springbootmvc.server.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long templateId;
    private Long userId;
} 