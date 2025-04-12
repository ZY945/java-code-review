package Thread_learn.thread.states;

public class Producer implements Runnable {
    private SharedResource resource;

    public Producer(SharedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            try {
                resource.produce(i);
                Thread.sleep(1000); // 模拟生产耗时
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}