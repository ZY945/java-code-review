package Thread_learn.thread.states;

public class ThreadDemo {
    // 共享对象，用于 wait 和 notify
    private static final Object lock = new Object();

    public static void main(String[] args) {
        // 创建三个线程
        Thread waitThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("WaitThread: 进入同步块，准备 wait...");
                    lock.wait(); // 释放锁，进入 WAITING
                    System.out.println("WaitThread: 被唤醒，继续执行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "WaitThread");

        Thread sleepThread = new Thread(() -> {
            try {
                System.out.println("SleepThread: 准备 sleep...");
                Thread.sleep(2000); // 不释放锁，进入 TIMED_WAITING
                System.out.println("SleepThread: 睡醒了，继续执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "SleepThread");

        Thread notifyThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("NotifyThread: 进入同步块，准备 notify...");
                    Thread.sleep(1000); // 模拟工作
                    lock.notify(); // 唤醒 wait 的线程
                    System.out.println("NotifyThread: 已调用 notify");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "NotifyThread");

        // 监控线程状态
        Thread monitor = new Thread(() -> {
            while (true) {
                System.out.println("WaitThread: " + waitThread.getState());
                System.out.println("SleepThread: " + sleepThread.getState());
                System.out.println("NotifyThread: " + notifyThread.getState());
                System.out.println("------------------------");
                try {
                    Thread.sleep(500); // 每 0.5 秒检查
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 当所有线程结束时停止监控
                if (waitThread.getState() == Thread.State.TERMINATED &&
                        sleepThread.getState() == Thread.State.TERMINATED &&
                        notifyThread.getState() == Thread.State.TERMINATED) {
                    break;
                }
            }
        }, "Monitor");

        // 启动所有线程
        monitor.start();
        waitThread.start();
        sleepThread.start();

        // 让 main 线程等待 0.5 秒后启动 notifyThread
        try {
            Thread.sleep(500);
            notifyThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 演示 join
        try {
            System.out.println("Main: 等待 SleepThread 结束 (join)...");
            sleepThread.join(); // 主线程等待 SleepThread
            System.out.println("Main: SleepThread 已结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}