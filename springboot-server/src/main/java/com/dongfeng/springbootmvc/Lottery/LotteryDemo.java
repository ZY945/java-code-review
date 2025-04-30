package com.dongfeng.springbootmvc.Lottery;

import java.util.ArrayList;
import java.util.List;

public class LotteryDemo {
    public static void main(String[] args) {
        // 创建奖品池
        List<Prize> prizePool = new ArrayList<>();
        prizePool.add(new Prize("1", "iPhone 15", Prize.PrizeType.PRODUCT, 5, 2));
        prizePool.add(new Prize("2", "100元优惠券", Prize.PrizeType.COUPON, 20, 100));
        prizePool.add(new Prize("3", "50元优惠券", Prize.PrizeType.COUPON, 30, 200));
        prizePool.add(new Prize("4", "10元优惠券", Prize.PrizeType.COUPON, 50, 500));
        prizePool.add(new Prize("5", "1000积分", Prize.PrizeType.POINTS, 100, 1000));
        prizePool.add(new Prize("6", "谢谢参与", Prize.PrizeType.THANKS, 200, Integer.MAX_VALUE));
        
        // 创建抽奖活动
        LotteryActivity activity = new LotteryActivity("act001", "618购物节抽奖", prizePool, 500);
        
        // 创建抽奖服务
        LotteryService lotteryService = new LotteryService();
        lotteryService.registerActivity(activity);
        
        // 创建奖品发放服务
        PrizeDeliveryService deliveryService = new PrizeDeliveryService();
        
        // 模拟用户抽奖
        for (int i = 1; i <= 10; i++) {
            String userId = "user" + i;
            try {
                Prize prize = lotteryService.draw("act001", userId);
                deliveryService.deliverPrize(userId, prize);
                System.out.println("--------------------");
            } catch (Exception e) {
                System.out.println("用户" + userId + "抽奖失败: " + e.getMessage());
            }
        }
    }
}