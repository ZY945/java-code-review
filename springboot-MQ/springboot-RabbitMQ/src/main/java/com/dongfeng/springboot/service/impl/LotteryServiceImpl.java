package com.dongfeng.springboot.service.impl;

import com.dongfeng.springboot.config.RabbitMQConfig;
import com.dongfeng.springboot.entity.UserLotteryRecord;
import com.dongfeng.springboot.mapper.UserLotteryRecordMapper;
import com.dongfeng.springboot.message.LotteryMessage;
import com.dongfeng.springboot.service.LotteryService;
import com.dongfeng.springboot.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 抽奖服务实现类
 */
@Slf4j
@Service
public class LotteryServiceImpl implements LotteryService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private UserLotteryRecordMapper userLotteryRecordMapper;
    
    @Autowired
    private RedisUtil redisUtil;
    
    /**
     * Redis缓存过期时间（秒）
     */
    private static final long REDIS_EXPIRE_TIME = 24 * 60 * 60;

    @Override
    public boolean sendLotteryMessage(Long userId, Long activityId, Long prizeId) {
        // 生成唯一消息ID
        String messageId = UUID.randomUUID().toString().replace("-", "");
        
        // 构建消息
        LotteryMessage message = new LotteryMessage();
        message.setMessageId(messageId);
        message.setUserId(userId);
        message.setActivityId(activityId);
        message.setPrizeId(prizeId);
        
        log.info("发送抽奖消息，messageId: {}, userId: {}, activityId: {}, prizeId: {}", 
                messageId, userId, activityId, prizeId);
        
        // 发送消息
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LOTTERY_EXCHANGE, 
                RabbitMQConfig.LOTTERY_ROUTING_KEY, 
                message);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processLotteryMessage(LotteryMessage message) {
        String messageId = message.getMessageId();
        Long userId = message.getUserId();
        Long activityId = message.getActivityId();
        Long prizeId = message.getPrizeId();
        
        log.info("处理抽奖消息，messageId: {}, userId: {}, activityId: {}, prizeId: {}", 
                messageId, userId, activityId, prizeId);
        
        try {
            // 1. 使用Redis进行消息去重（第一层幂等性保障）
            if (redisUtil.existsMessageId(messageId)) {
                log.info("消息已处理，跳过，messageId: {}", messageId);
                return true;
            }
            
            // 2. 查询数据库是否已存在记录（第二层幂等性保障）
            UserLotteryRecord existRecord = userLotteryRecordMapper.selectByMessageId(messageId);
            if (existRecord != null) {
                log.info("消息已处理，数据库已存在记录，messageId: {}", messageId);
                // 设置Redis缓存，避免下次重复查询数据库
                redisUtil.setMessageIdIfAbsent(messageId, REDIS_EXPIRE_TIME);
                return true;
            }
            
            // 3. 查询是否存在用户-活动-奖品的记录（业务唯一性约束）
            UserLotteryRecord businessRecord = userLotteryRecordMapper.selectByUserActivityPrize(userId, activityId, prizeId);
            if (businessRecord != null) {
                log.info("用户已领取过该活动奖品，userId: {}, activityId: {}, prizeId: {}", userId, activityId, prizeId);
                // 设置Redis缓存，避免下次重复查询数据库
                redisUtil.setMessageIdIfAbsent(messageId, REDIS_EXPIRE_TIME);
                return true;
            }
            
            // 4. 插入抽奖记录
            UserLotteryRecord record = new UserLotteryRecord();
            record.setUserId(userId);
            record.setActivityId(activityId);
            record.setPrizeId(prizeId);
            record.setMessageId(messageId);
            record.setState(0); // 初始状态：未发放
            
            userLotteryRecordMapper.insert(record);
            
            // 5. 处理奖品发放逻辑（实际业务中可能涉及库存扣减、奖品发放等）
            // 这里简化处理，直接更新状态为已发放
            userLotteryRecordMapper.updateState(record.getId(), 1);
            
            // 6. 设置Redis缓存，标记消息已处理
            redisUtil.setMessageIdIfAbsent(messageId, REDIS_EXPIRE_TIME);
            
            log.info("抽奖消息处理成功，messageId: {}", messageId);
            return true;
        } catch (DuplicateKeyException e) {
            // 捕获数据库唯一约束异常（第三层幂等性保障）
            log.warn("数据库唯一约束异常，消息重复处理，messageId: {}", messageId, e);
            // 设置Redis缓存，避免下次重复处理
            redisUtil.setMessageIdIfAbsent(messageId, REDIS_EXPIRE_TIME);
            return true;
        } catch (Exception e) {
            log.error("处理抽奖消息异常，messageId: {}", messageId, e);
            return false;
        }
    }
}
