package nonatomic; // 或者你实际使用的包名 se.chalmers.firegroup54.raceconditions.nonatomic

import java.util.concurrent.atomic.AtomicInteger;

public class CorrectedNonAtomic {
    // AtomicInteger 提供了原子性的整数操作
    private AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        // incrementAndGet() 是一个原子操作
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }

    public static void main(String[] args) throws InterruptedException {
        final CorrectedNonAtomic counter = new CorrectedNonAtomic();
        final int numberOfIncrements = 100000; // 每个线程递增的次数
        final int numberOfThreads = 2; // 线程数量

        Runnable task = () -> {
            for (int i = 0; i < numberOfIncrements; i++) {
                counter.increment();
            }
        };

        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join(); // 等待所有线程完成
        }

        System.out.println("CorrectedNonAtomic Counter:");
        System.out.println("Expected count: " + (numberOfIncrements * numberOfThreads));
        System.out.println("Actual count:   " + counter.getCount());
        // 使用 AtomicInteger 后，Actual count 应该等于 Expected count
    }
}