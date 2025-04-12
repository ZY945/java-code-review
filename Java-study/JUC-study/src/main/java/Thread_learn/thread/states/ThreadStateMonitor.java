package Thread_learn.thread.states;

import java.util.ArrayList;
import java.util.List;

public class ThreadStateMonitor {
    private List<Thread> threads = new ArrayList<>();
    private boolean running = true;

    public void addThread(Thread thread) {
        threads.add(thread);
    }

    public void startMonitoring() {
        Thread monitorThread = new Thread(() -> {
            while (running) {
                for (Thread thread : threads) {
                    System.out.println("Thread: " + thread.getName() + ", State: " + thread.getState());
                }
                System.out.println("------------------------");
                try {
                    Thread.sleep(500); // 每0.5秒检查一次状态
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "MonitorThread");
        monitorThread.start();
    }

    public void stopMonitoring() {
        running = false;
    }
}