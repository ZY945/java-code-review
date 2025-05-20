package DesignPattern.all;

public class Singleton {
    private static volatile Singleton instance;
    private String config;

    private Singleton() {
        this.config = "AppConfig"; // Simulate configuration
    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    public String getConfig() {
        return config;
    }

    public static void main(String[] args) {
        Singleton config1 = Singleton.getInstance();
        Singleton config2 = Singleton.getInstance();
        System.out.println("Config1: " + config1.getConfig());
        System.out.println("Config2: " + config2.getConfig());
        System.out.println("Same instance: " + (config1 == config2)); // True
    }
}