package com.dongfeng.springbootmvc.server.coupon.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponTemplateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "优惠券名称不能为空")
    @Size(max = 64, message = "优惠券名称最长64字符")
    private String name;

    @Size(max = 256, message = "描述最长256字符")
    private String description;

    @NotNull(message = "券类型不能为空")
    @Min(value = 1, message = "券类型不正确")
    @Max(value = 3, message = "券类型不正确")
    private Integer type;

    @NotNull(message = "优惠金额不能为空")
    @DecimalMin(value = "0.01", message = "优惠金额必须大于0")
    private BigDecimal discount;

    @DecimalMin(value = "0.01", message = "门槛金额必须大于0")
    private BigDecimal threshold;

    @NotNull(message = "发行数量不能为空")
    @Min(value = 1, message = "发行数量必须大于0")
    private Integer total;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    private LocalDateTime endTime;
} 