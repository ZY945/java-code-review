package com.dongfeng.springboot.entity;

import lombok.Data;
import java.util.Date;

/**
 * 抽奖活动实体类
 */
@Data
public class LotteryActivity {
    
    /**
     * 活动ID
     */
    private Long id;
    
    /**
     * 活动名称
     */
    private String activityName;
    
    /**
     * 活动描述
     */
    private String activityDesc;
    
    /**
     * 开始时间
     */
    private Date beginDateTime;
    
    /**
     * 结束时间
     */
    private Date endDateTime;
    
    /**
     * 库存
     */
    private Integer stockCount;
    
    /**
     * 已领取数量
     */
    private Integer takeCount;
    
    /**
     * 活动状态：0-未开始，1-进行中，2-已结束
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
