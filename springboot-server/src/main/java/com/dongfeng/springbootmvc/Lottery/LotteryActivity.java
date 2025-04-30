package com.dongfeng.springbootmvc.Lottery;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽奖活动类
 */
public class LotteryActivity {
    private String id;
    private String name;
    private Date startTime;
    private Date endTime;
    private List<Prize> prizePool;
    private int emptyWeight; // 不中奖权重
    private boolean active;

    // getter和setter方法

    public LotteryActivity(String id, String name, List<Prize> prizePool, int emptyWeight) {
        this.id = id;
        this.name = name;
        this.prizePool = prizePool;
        this.emptyWeight = emptyWeight;
        this.active = true;
    }

    public Map<Prize, Integer> getPrizeWeightMap() {
        Map<Prize, Integer> weightMap = new HashMap<>();
        for (Prize prize : prizePool) {
            if (prize.isAvailable()) {
                weightMap.put(prize, prize.getWeight());
            }
        }
        return weightMap;
    }

    public int getEmptyWeight() {
        return emptyWeight;
    }

    public boolean isActive() {
        return active;
    }
    
    public String getId() {
        return id;
    }
}