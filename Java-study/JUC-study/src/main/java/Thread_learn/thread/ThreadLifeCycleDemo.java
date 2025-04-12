package Thread_learn.thread;

public class ThreadLifeCycleDemo {
    // 用于同步的锁对象
    private static final Object lock = new Object();

    public static void main(String[] args) {
        // 创建线程1，用于展示完整的生命周期
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread1: Entering run() method, state: " + Thread.currentThread().getState());

            // 模拟RUNNABLE状态（线程正在运行）
            for (int i = 0; i < 1000000; i++) {
                // 模拟一些计算任务
                Math.random();
            }
            System.out.println("Thread1: Performing some computation, state: " + Thread.currentThread().getState());

            // 进入BLOCKED状态：尝试获取lock，但被thread2持有
            synchronized (lock) {
                System.out.println("Thread1: Acquired lock, state: " + Thread.currentThread().getState());
            }

            // 进入WAITING状态：调用wait()
            synchronized (lock) {
                try {
                    System.out.println("Thread1: Calling wait(), will enter WAITING");
                    lock.wait(); // 进入WAITING
                    System.out.println("Thread1: Woken up from wait(), state: " + Thread.currentThread().getState());
                } catch (InterruptedException e) {
                    System.out.println("Thread1: Interrupted during wait");
                }
            }

            // 进入TIMED_WAITING状态：调用sleep()
            try {
                System.out.println("Thread1: Calling sleep(), will enter TIMED_WAITING");
                Thread.sleep(2000); // 睡眠2秒
                System.out.println("Thread1: Woke up from sleep(), state: " + Thread.currentThread().getState());
            } catch (InterruptedException e) {
                System.out.println("Thread1: Interrupted during sleep");
            }

            System.out.println("Thread1: Exiting run(), will enter TERMINATED soon");
        }, "Thread-1");

        // 创建线程2，用于制造BLOCKED状态
        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("Thread2: Holding lock, state: " + Thread.currentThread().getState());
                    Thread.sleep(1000); // 持有锁1秒，使thread1进入BLOCKED
                } catch (InterruptedException e) {
                    System.out.println("Thread2: Interrupted");
                }
            }
        }, "Thread-2");
        thread2.yield();
        // 创建线程3，用于唤醒thread1的WAITING状态
        Thread thread3 = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(500); // 稍等片刻，确保thread1进入WAITING
                    System.out.println("Thread3: Notifying thread1 to wake up");
                    lock.notify(); // 唤醒thread1
                } catch (InterruptedException e) {
                    System.out.println("Thread3: Interrupted");
                }
            }
        }, "Thread-3");

        // 展示NEW状态
        System.out.println("Thread1: Created, state: " + thread1.getState());
        System.out.println("Thread2: Created, state: " + thread2.getState());
        System.out.println("Thread3: Created, state: " + thread3.getState());

        // 启动线程，进入RUNNABLE
        thread2.start(); // 先启动thread2，使其持有lock
        try {
            Thread.sleep(100); // 确保thread2先运行
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread1.start(); // 启动thread1
        System.out.println("Thread1: Started, state: " + thread1.getState());

        // 模拟thread1进入BLOCKED（thread2持有锁）
        try {
            Thread.sleep(200); // 等待thread1尝试获取锁
            System.out.println("Thread1: Likely BLOCKED, state: " + thread1.getState());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 启动thread3，唤醒thread1的WAITING状态
        thread3.start();

        // 等待thread1结束，观察TERMINATED状态
        try {
            thread1.join(); // 主线程等待thread1结束
            System.out.println("Thread1: Completed, state: " + thread1.getState());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}