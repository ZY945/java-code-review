package cyclicBarrier_study;


import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier--屏障，常用await进行阻塞，屏障
 *
 * @author dongfeng
 * @date 2022/12/3 11:36
 */
public class _02_CyclicBarrier {

    private static final int num = 12;

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(num, () -> {
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("集齐十二张卡片合成红包");
        });

        for (int i = 0; i < 12; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "已获得");
                    cyclicBarrier.await();
                    System.out.println("开奖");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
