package com.dongfeng.springbootmvc.Lottery;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽奖服务
 */
public class LotteryService {
    private WeightedRandomDrawAlgorithm drawAlgorithm;
    private Map<String, LotteryActivity> activityMap;

    public LotteryService() {
        this.drawAlgorithm = new WeightedRandomDrawAlgorithm();
        this.activityMap = new HashMap<>();
    }

    public void registerActivity(LotteryActivity activity) {
        activityMap.put(activity.getId(), activity);
    }

    /**
     * 执行抽奖
     * 
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 抽中的奖品，null表示未中奖
     */
    public synchronized Prize draw(String activityId, String userId) {
        LotteryActivity activity = activityMap.get(activityId);
        if (activity == null || !activity.isActive()) {
            throw new IllegalArgumentException("活动不存在或未激活");
        }

        // 检查用户抽奖资格
        if (!checkUserEligibility(userId, activityId)) {
            throw new IllegalStateException("用户没有抽奖资格");
        }

        // 获取奖品权重映射
        Map<Prize, Integer> weightMap = activity.getPrizeWeightMap();
        
        // 执行抽奖
        Prize prize = drawAlgorithm.drawWithEmpty(weightMap, activity.getEmptyWeight());
        
        // 记录抽奖结果
        recordDrawResult(userId, activityId, prize);
        
        // 如果中奖，减少库存
        if (prize != null) {
            prize.decreaseStock();
        }
        
        return prize;
    }

    private boolean checkUserEligibility(String userId, String activityId) {
        // 实际实现中，这里会检查用户是否有抽奖资格，如次数限制、黑名单等
        return true;
    }

    private void recordDrawResult(String userId, String activityId, Prize prize) {
        // 实际实现中，这里会记录抽奖结果到数据库
        String result = prize != null ? prize.getName() : "未中奖";
        System.out.println("用户" + userId + "在活动" + activityId + "中抽奖结果: " + result);
    }
}