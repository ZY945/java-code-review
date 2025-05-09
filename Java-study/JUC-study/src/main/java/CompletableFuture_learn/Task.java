package CompletableFuture_learn;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Consumer---入参,无返回
 * 类比stream流进行学习
 *
 * @author dongfeng
 * @date 2023/8/4 10:11
 */
public class Task {
    public static Runnable getRunnableTask(String taskName) {
        return () -> {
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Runnable----无入参无返回值：");
        };
    }

    /**
     * stream流的forEach方法
     * void forEach(Consumer<? super T> action);
     * {@link Collection#forEach },
     *
     * @param taskName
     * @return
     */
    public static Consumer<String> getConsumer(String taskName) {
        List<String> list = Arrays.asList("1", "2", "3", "4", "5");
        Stream.of(list).forEach(System.out::println);
        return result -> {
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Consumer----入参无返回值：");
            System.out.println("入参为" + result);
        };
    }

    public static Supplier<String> getSupplierTask(String taskName) {
        return () -> {
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Supplier----无入参有返回值：");
            return "(Supplier返回值：)" + taskName;
        };
    }

    public static Function<String, String> getFunctionTask(String taskName) {
        return result -> {
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Function----有入参有返回值：");
            return "(Function返回值：)" + taskName + "--参数:" + result;
        };
    }

    public static Runnable getLongTimeRunnableTask(String taskName) {
        return () -> {
            sleep(5);
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Runnable----无入参无返回值：");
        };
    }

    public static Consumer<String> getLongTimeConsumer(String taskName) {
        return result -> {
            sleep(5);
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Consumer----入参无返回值：");
            System.out.println("入参为" + result);
        };
    }

    public static Supplier<String> getLongTimeSupplierTask(String taskName) {
        return () -> {
            sleep(5);
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Supplier----无入参有返回值：");
            return "(Supplier返回值：)" + taskName;
        };
    }

    public static Function<String, String> getLongTimeFunctionTask(String taskName) {
        return result -> {
            sleep(5);
            System.out.println(Thread.currentThread().getName() + "(任务名)" + taskName + "----Function----有入参有返回值：");
            return "(Function返回值：)" + taskName + "--参数:" + result;
        };
    }

    /**
     * 模拟耗时的任务
     *
     * @param seconds 耗时
     */
    public static void sleep(Integer seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}