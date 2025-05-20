package DesignPattern.all;

interface Service {
    void execute();
}

class RealService implements Service {
    public void execute() {
        System.out.println("Executing real service");
    }
}

class ProxyService implements Service {
    private RealService realService;

    public ProxyService() {
        realService = new RealService();
    }

    public void execute() {
        System.out.println("Checking permissions");
        realService.execute();
    }
}

public class Proxy {
    public static void main(String[] args) {
        Service service = new ProxyService();
        service.execute();
    }
}