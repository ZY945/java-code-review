package com.dongfeng.springboot.message;

import lombok.Data;
import java.io.Serializable;

/**
 * 抽奖消息实体
 */
@Data
public class LotteryMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID（用于幂等性控制）
     */
    private String messageId;
    
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
