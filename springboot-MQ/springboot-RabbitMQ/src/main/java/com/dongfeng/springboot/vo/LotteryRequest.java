package com.dongfeng.springboot.vo;

import lombok.Data;

/**
 * 抽奖请求对象
 */
@Data
public class LotteryRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 奖品ID
     */
    private Long prizeId;
}
