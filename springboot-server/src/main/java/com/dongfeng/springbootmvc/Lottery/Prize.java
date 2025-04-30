package com.dongfeng.springbootmvc.Lottery;

import java.util.*;

/**
 * 奖品实体类
 */
public class Prize {
    private String id;
    private String name;
    private PrizeType type;
    private int weight;
    private int stock;
    private boolean available;

    public enum PrizeType {
        COUPON,       // 优惠券
        PRODUCT,      // 实物商品
        POINTS,       // 积分
        VIRTUAL_ITEM, // 虚拟物品
        THANKS        // 谢谢参与
    }

    // 构造器、getter和setter方法省略...

    public Prize(String id, String name, PrizeType type, int weight, int stock) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.stock = stock;
        this.available = stock > 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PrizeType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    public int getStock() {
        return stock;
    }

    public boolean isAvailable() {
        return available;
    }

    public void decreaseStock() {
        if (this.stock > 0) {
            this.stock--;
            this.available = this.stock > 0;
        }
    }

    @Override
    public String toString() {
        return "Prize{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", stock=" + stock +
                '}';
    }
}





