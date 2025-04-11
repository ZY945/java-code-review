package com.dongfeng.springbootmvc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    /**
     * 获取CPU核心数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 核心线程数 = CPU核心数 + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;

    /**
     * 最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    /**
     * 队列容量 = 1000
     */
    private static final int QUEUE_CAPACITY = 1000;

    /**
     * 线程空闲时间 = 60s
     */
    private static final long KEEP_ALIVE_TIME = 60L;

    @Bean("couponTaskExecutor")
    public ThreadPoolExecutor couponTaskExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadFactory() {
                    private int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("Coupon-Task-Thread-" + count++);
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 预热核心线程
        executor.prestartAllCoreThreads();

        // 添加线程池监控
        monitorThreadPool(executor);

        return executor;
    }

    private void monitorThreadPool(ThreadPoolExecutor executor) {
        ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(() -> {
            log.info("=========================");
            log.info("线程池状态:");
            log.info("核心线程数: {}", executor.getCorePoolSize());
            log.info("活动线程数: {}", executor.getActiveCount());
            log.info("最大线程数: {}", executor.getMaximumPoolSize());
            log.info("线程池活跃度: {}", divide(executor.getActiveCount(), executor.getMaximumPoolSize()));
            log.info("任务完成数: {}", executor.getCompletedTaskCount());
            log.info("队列大小: {}", executor.getQueue().size());
            log.info("当前排队线程数: {}", executor.getQueue().size());
            log.info("队列剩余大小: {}", executor.getQueue().remainingCapacity());
            log.info("队列使用度: {}", divide(executor.getQueue().size(), QUEUE_CAPACITY));
            log.info("=========================");
        }, 0, 5, TimeUnit.SECONDS);
    }

    private String divide(int num1, int num2) {
        return String.format("%.2f%%", (double) num1 / (double) num2 * 100);
    }

    @PreDestroy
    public void shutdown() {
        log.info("开始关闭线程池...");
        List<Runnable> tasks = couponTaskExecutor().shutdownNow();
        log.info("未完成的任务数量: {}", tasks.size());
        try {
            if (!couponTaskExecutor().awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("线程池未能在60秒内完全关闭");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待线程池关闭被中断", e);
        }
    }
} 