package com.dongfeng.springboot.mapper;

import com.dongfeng.springboot.entity.UserLotteryRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户抽奖记录Mapper接口
 */
@Mapper
public interface UserLotteryRecordMapper {
    
    /**
     * 插入抽奖记录
     * 
     * @param record 抽奖记录
     * @return 影响行数
     */
    int insert(UserLotteryRecord record);
    
    /**
     * 根据消息ID查询抽奖记录
     * 
     * @param messageId 消息ID
     * @return 抽奖记录
     */
    UserLotteryRecord selectByMessageId(@Param("messageId") String messageId);
    
    /**
     * 根据用户ID、活动ID和奖品ID查询抽奖记录
     * 
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param prizeId 奖品ID
     * @return 抽奖记录
     */
    UserLotteryRecord selectByUserActivityPrize(@Param("userId") Long userId, 
                                               @Param("activityId") Long activityId, 
                                               @Param("prizeId") Long prizeId);
    
    /**
     * 更新抽奖记录状态
     * 
     * @param id 记录ID
     * @param state 状态
     * @return 影响行数
     */
    int updateState(@Param("id") Long id, @Param("state") Integer state);
}
