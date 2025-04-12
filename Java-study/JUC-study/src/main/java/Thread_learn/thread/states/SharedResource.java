package Thread_learn.thread.states;

import java.util.ArrayList;
import java.util.List;

public class SharedResource {
    private final List<Integer> queue = new ArrayList<>();
    private final int capacity = 10;

    public void produce(int num) throws InterruptedException {
        synchronized (queue) {
            while (queue.size() == capacity) {
                queue.wait(); // 队列满，等待消费者消费
            }
            queue.add(num);
            queue.notifyAll(); // 通知消费者
        }
    }

    public void consume() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait(); // 队列空，等待生产者生产
            }
            queue.remove(0);
            queue.notifyAll(); // 通知生产者
        }
    }
}