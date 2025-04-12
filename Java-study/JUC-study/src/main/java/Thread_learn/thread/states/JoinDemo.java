package Thread_learn.thread.states;

public class JoinDemo implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " started.");
        try {
            Thread.sleep(3000); // 模拟工作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " completed.");
    }
}