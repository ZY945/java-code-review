package Thread_learn.thread.states;


public class Main {
    public static void main(String[] args) {
        // 创建共享资源
        SharedResource resource = new SharedResource();

        // 创建生产者和消费者线程
        Producer producer = new Producer(resource);
        Consumer consumer = new Consumer(resource);
        Thread producerThread = new Thread(producer, "Producer");
        Thread consumerThread = new Thread(consumer, "Consumer");

        // 创建用于展示 join 的线程
        JoinDemo joinDemo = new JoinDemo();
        Thread joinThread = new Thread(joinDemo, "JoinThread");

        // 创建线程状态监控器
        ThreadStateMonitor monitor = new ThreadStateMonitor();

        // 添加线程到监控器
        monitor.addThread(producerThread);
        monitor.addThread(consumerThread);
        monitor.addThread(joinThread);

        // 启动监控线程
        monitor.startMonitoring();

        // 启动生产者和消费者线程
        producerThread.start();
        consumerThread.start();

        // 启动 join 线程并演示 join
        joinThread.start();
        try {
            System.out.println("Main thread waiting for JoinThread to complete...");
            joinThread.join();
            System.out.println("JoinThread completed, Main thread resumes.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 模拟主线程休眠
        try {
            System.out.println("Main thread sleeping for 2 seconds...");
            Thread.sleep(2000);
            System.out.println("Main thread wakes up.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}