package DesignPattern.all;

abstract class OrderProcessor {
    public void processOrder() {
        validateOrder();
        processPayment();
        shipOrder();
    }

    abstract void validateOrder();
    abstract void processPayment();
    abstract void shipOrder();
}

class OnlineOrderProcessor extends OrderProcessor {
    void validateOrder() {
        System.out.println("Validating online order");
    }

    void processPayment() {
        System.out.println("Processing online payment");
    }

    void shipOrder() {
        System.out.println("Shipping online order");
    }
}

public class TemplateMethod {
    public static void main(String[] args) {
        OrderProcessor processor = new OnlineOrderProcessor();
        processor.processOrder();
    }
}