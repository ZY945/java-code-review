package com.dongfeng.springboot;

import com.dongfeng.springboot.service.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 抽奖服务测试类
 */
@SpringBootTest
public class LotteryServiceTest {

    @Autowired
    private LotteryService lotteryService;

    /**
     * 测试发送抽奖消息
     */
    @Test
    public void testSendLotteryMessage() {
        // 模拟用户参与抽奖
        Long userId = 1001L;
        Long activityId = 101L;
        Long prizeId = 201L;
        
        // 发送抽奖消息
        boolean result = lotteryService.sendLotteryMessage(userId, activityId, prizeId);
        
        System.out.println("发送结果: " + result);
        
        // 等待消息处理完成
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 测试消息幂等性 - 重复发送相同消息
     */
    @Test
    public void testIdempotence() {
        // 模拟用户参与抽奖
        Long userId = 1002L;
        Long activityId = 102L;
        Long prizeId = 202L;
        
        // 连续发送3次相同参数的抽奖消息，测试幂等性处理
        for (int i = 0; i < 3; i++) {
            boolean result = lotteryService.sendLotteryMessage(userId, activityId, prizeId);
            System.out.println("第" + (i + 1) + "次发送结果: " + result);
        }
        
        // 等待消息处理完成
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
