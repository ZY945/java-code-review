package DesignPattern.all;

interface Logger {
    void log(String message);
}

class FileLogger implements Logger {
    public void log(String message) {
        System.out.println("File Log: " + message);
    }
}

class LoggerFactory {
    public Logger createLogger() {
        return new FileLogger();
    }
}

public class FactoryMethod {
    public static void main(String[] args) {
        LoggerFactory factory = new LoggerFactory();
        Logger logger = factory.createLogger();
        logger.log("Error occurred!");
    }
}