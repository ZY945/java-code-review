package com.dongfeng.springbootmvc.server.coupon.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PreloadRequest {

    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @NotNull(message = "预热数量不能为空")
    @Min(value = 1, message = "预热数量必须大于0")
    private Integer count;

    @NotNull(message = "活动ID不能为空")
    private String activityId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
} 