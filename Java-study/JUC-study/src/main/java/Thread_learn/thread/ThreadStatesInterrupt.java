package Thread_learn.thread;

/**
 * 线程中断的状态
 * 1.用户手动停止线程
 * 2.超时控制
 * 3.中断处理允许线程在退出前执行清理逻辑（如关闭流、释放锁），避免资源泄漏。
 * 4.中断是一种标准化的信号机制,可以用于线程间通信
 */
public class ThreadStatesInterrupt {
    public static void main(String[] args) throws InterruptedException {
        ThreadStatesInterrupt threadStatesInterrupt = new ThreadStatesInterrupt();
        try {
            threadStatesInterrupt.interruptThread();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadStatesInterrupt.wait();
        System.out.println("main thread is running...");

    }

    /**
     * 模拟超时控制
     *
     * @throws InterruptedException
     */
    public void interruptThread() throws InterruptedException {
        Thread waiter = new Thread(() -> {
            try {
                synchronized (this) {

                    wait(2001); // 等待5秒
                    System.out.println("Data received");
                }
            } catch (InterruptedException e) {
                System.out.println("Wait interrupted due to timeout");
            }
        });
        waiter.start();
        Thread.sleep(2000); // 主线程模拟超时
        waiter.interrupt();
    }
}
