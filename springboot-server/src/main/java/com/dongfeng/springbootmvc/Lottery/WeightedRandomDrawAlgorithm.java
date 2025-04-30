package com.dongfeng.springbootmvc.Lottery;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedRandomDrawAlgorithm {
    
    /**
     * 基于TreeMap的权重随机抽奖算法
     * 
     * @param <T> 奖品类型
     * @param weightMap 奖品及其权重映射
     * @return 抽中的奖品
     */
    public <T> T draw(Map<T, Integer> weightMap) {
        if (weightMap == null || weightMap.isEmpty()) {
            return null;
        }
        
        // 计算总权重
        int totalWeight = weightMap.values().stream().mapToInt(Integer::intValue).sum();
        if (totalWeight <= 0) {
            return null;
        }
        
        // 生成随机数
        int randomWeight = ThreadLocalRandom.current().nextInt(totalWeight);
        
        // 使用TreeMap进行区间查找
        TreeMap<Integer, T> treeMap = new TreeMap<>();
        int weightSum = 0;
        
        for (Map.Entry<T, Integer> entry : weightMap.entrySet()) {
            if (entry.getValue() > 0) {
                weightSum += entry.getValue();
                treeMap.put(weightSum, entry.getKey());
            }
        }
        
        // 找到大于等于随机数的最小键
        Map.Entry<Integer, T> entry = treeMap.ceilingEntry(randomWeight + 1);
        return entry != null ? entry.getValue() : null;
    }
    
    /**
     * 带有空奖的抽奖算法
     * 
     * @param <T> 奖品类型
     * @param weightMap 奖品及其权重映射
     * @param emptyWeight 不中奖的权重
     * @return 抽中的奖品，可能为null表示未中奖
     */
    public <T> T drawWithEmpty(Map<T, Integer> weightMap, int emptyWeight) {
        if (weightMap == null || weightMap.isEmpty() || emptyWeight < 0) {
            return null;
        }
        
        // 计算总权重(包含空奖权重)
        int totalWeight = weightMap.values().stream().mapToInt(Integer::intValue).sum() + emptyWeight;
        if (totalWeight <= 0) {
            return null;
        }
        
        // 生成随机数
        int randomWeight = ThreadLocalRandom.current().nextInt(totalWeight);
        
        // 如果随机数落在空奖区间，返回null
        if (randomWeight >= totalWeight - emptyWeight) {
            return null;
        }
        
        // 使用TreeMap进行区间查找
        TreeMap<Integer, T> treeMap = new TreeMap<>();
        int weightSum = 0;
        
        for (Map.Entry<T, Integer> entry : weightMap.entrySet()) {
            if (entry.getValue() > 0) {
                weightSum += entry.getValue();
                treeMap.put(weightSum, entry.getKey());
            }
        }
        
        // 找到大于等于随机数的最小键
        Map.Entry<Integer, T> entry = treeMap.ceilingEntry(randomWeight + 1);
        return entry != null ? entry.getValue() : null;
    }
    
    /**
     * 批量抽奖并统计结果
     * 
     * @param <T> 奖品类型
     * @param weightMap 奖品及其权重映射
     * @param times 抽奖次数
     * @return 各奖品抽中次数统计
     */
    public <T> Map<T, Integer> batchDraw(Map<T, Integer> weightMap, int times) {
        Map<T, Integer> result = new HashMap<>();
        
        for (int i = 0; i < times; i++) {
            T prize = draw(weightMap);
            if (prize != null) {
                result.put(prize, result.getOrDefault(prize, 0) + 1);
            }
        }
        
        return result;
    }
}