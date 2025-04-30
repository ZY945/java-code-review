package com.dongfeng.springbootmvc.Lottery;

/**
 * 奖品发放服务
 */
public class PrizeDeliveryService {
    
    public void deliverPrize(String userId, Prize prize) {
        if (prize == null) {
            return;
        }
        
        switch (prize.getType()) {
            case COUPON:
                deliverCoupon(userId, prize);
                break;
            case PRODUCT:
                deliverProduct(userId, prize);
                break;
            case POINTS:
                deliverPoints(userId, prize);
                break;
            case VIRTUAL_ITEM:
                deliverVirtualItem(userId, prize);
                break;
            case THANKS:
                // 无需发放
                deliverThanks(userId,prize);
                break;
        }
    }
    
    private void deliverCoupon(String userId, Prize prize) {
        System.out.println("向用户" + userId + "发放优惠券: " + prize.getName());
        // 实际实现中，这里会调用优惠券系统的API
    }
    
    private void deliverProduct(String userId, Prize prize) {
        System.out.println("向用户" + userId + "发放实物商品: " + prize.getName() + "，等待用户填写收货地址");
        // 实际实现中，这里会创建物流订单
    }
    
    private void deliverPoints(String userId, Prize prize) {
        System.out.println("向用户" + userId + "发放积分: " + prize.getName());
        // 实际实现中，这里会调用积分系统的API
    }
    
    private void deliverVirtualItem(String userId, Prize prize) {
        System.out.println("向用户" + userId + "发放虚拟物品: " + prize.getName());
        // 实际实现中，这里会调用虚拟物品系统的API
    }

    private void deliverThanks(String userId, Prize prize) {
        System.out.println("谢谢惠顾");
        // 实际实现中，这里会调用虚拟物品系统的API
    }
}