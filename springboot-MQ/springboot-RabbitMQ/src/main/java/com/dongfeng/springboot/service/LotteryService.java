package com.dongfeng.springboot.service;

import com.dongfeng.springboot.message.LotteryMessage;

/**
 * 抽奖服务接口
 */
public interface LotteryService {
    
    /**
     * 发送抽奖消息
     * 
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param prizeId 奖品ID
     * @return 是否发送成功
     */
    boolean sendLotteryMessage(Long userId, Long activityId, Long prizeId);
    
    /**
     * 处理抽奖消息（幂等性处理）
     * 
     * @param message 抽奖消息
     * @return 是否处理成功
     */
    boolean processLotteryMessage(LotteryMessage message);
}
