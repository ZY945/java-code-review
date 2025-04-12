package Thread_learn.thread.states;

public class Consumer implements Runnable {
    private SharedResource resource;

    public Consumer(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            try {
                resource.consume();
                Thread.sleep(1500); // 模拟消费耗时
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}