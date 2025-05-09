package com.dongfeng.springboot.entity;

import lombok.Data;
import java.util.Date;

/**
 * 奖品实体类
 */
@Data
public class LotteryPrize {
    /**
     * 奖品ID
     */
    private Long id;
    
    /**
     * 奖品名称
     */
    private String prizeName;
    
    /**
     * 奖品类型：0-虚拟奖品，1-实物奖品
     */
    private Integer prizeType;
    
    /**
     * 奖品内容
     */
    private String prizeContent;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
