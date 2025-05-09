package com.dongfeng.springboot.entity;

import lombok.Data;
import java.util.Date;

/**
 * 用户抽奖记录实体类
 */
@Data
public class UserLotteryRecord {
    /**
     * 记录ID
     */
    private Long id;
    
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
    
    /**
     * 消息ID（用于幂等性控制）
     */
    private String messageId;
    
    /**
     * 状态：0-未发放，1-已发放，2-发放失败
     */
    private Integer state;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
