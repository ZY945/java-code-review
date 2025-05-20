package DesignPattern.all;

interface PaymentStrategy {
    void pay(int amount);
}

class Alipay implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " via Alipay");
    }
}

class WechatPay implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid " + amount + " via Wechat");
    }
}

class PaymentContext {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(int amount) {
        strategy.pay(amount);
    }
}

public class Strategy {
    public static void main(String[] args) {
        PaymentContext context = new PaymentContext();
        context.setStrategy(new Alipay());
        context.executePayment(100);
        context.setStrategy(new WechatPay());
        context.executePayment(200);
    }
}