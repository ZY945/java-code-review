package CompletableFuture_learn.two.both;


import CompletableFuture_learn.Task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CompletableFuture<Void><br/>
 * thenAcceptBoth<br/>
 * 无参,无返回值<br/>
 *
 * @author dongfeng
 * @date 2023/8/4 16:14
 */
public class RunAfterBoth {

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // pool-1-thread-1
        CompletableFuture<String> taskA = CompletableFuture.supplyAsync(Task.getSupplierTask("第一次任务"), executorService);
        // pool-1-thread-2
        CompletableFuture<String> taskB = CompletableFuture.supplyAsync(Task.getLongTimeSupplierTask("第二次任务"), executorService);

        // pool-1-thread-1
        // main 当两个任务执行过快时,会使用main线程,当两个任务时常不一致时,使用时常长的线程
        CompletableFuture<Void> result = taskA.runAfterBoth(taskB, () -> {
            System.out.println(Thread.currentThread().getName() + "整合两个任务的结果");
        });
        System.out.println("---------result.get()---------");
        System.out.println(result.get());
        // 关闭线程池
        executorService.shutdown();
    }
}
