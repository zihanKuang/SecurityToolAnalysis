package nonatomic; // 或者你实际使用的包名 se.chalmers.firegroup54.raceconditions.nonatomic

public class FlawedNonAtomic {
    private int count = 0;

    public void increment() {
        // 这个操作不是原子的。它实际上包含三个步骤：
        // 1. 读取 count 的当前值
        // 2. 将值加 1
        // 3. 将新值写回 count
        // 多个线程可能在这些步骤之间交错执行，导致更新丢失。
        count++;
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        final FlawedNonAtomic counter = new FlawedNonAtomic();
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

        System.out.println("FlawedNonAtomic Counter:");
        System.out.println("Expected count: " + (numberOfIncrements * numberOfThreads));
        System.out.println("Actual count:   " + counter.getCount());
        // 由于竞态条件，Actual count 通常会小于 Expected count
    }
}