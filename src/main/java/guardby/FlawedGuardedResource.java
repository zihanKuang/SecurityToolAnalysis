package guardby; // 或者你实际的包名

// 假设我们有一个约定（或者在更复杂的系统中会有 @GuardedBy("lock") 注解）
// 即 sharedData 必须在持有 'lock' 对象锁的情况下才能被访问。
public class FlawedGuardedResource {
    private final Object lock = new Object();
    private int sharedData = 0; // 这个数据应该被 lock 保护

    // 正确的写方法，获取了锁
    public void setDataGuarded(int data) {
        synchronized (lock) {
            System.out.println(Thread.currentThread().getName() + " acquired lock, setting data to: " + data);
            this.sharedData = data;
            try {
                Thread.sleep(50); // 模拟一些工作
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName() + " releasing lock, data is now: " + this.sharedData);
        }
    }

    // 错误的写方法，没有获取锁（违反了 Guarded-By 约定）
    public void setDataUnguarded(int data) {
        System.out.println(Thread.currentThread().getName() + " (UNGUARDED) setting data to: " + data);
        this.sharedData = data; // 直接访问，没有同步
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(Thread.currentThread().getName() + " (UNGUARDED) data is now: " + this.sharedData);
    }

    // 错误的读方法，没有获取锁
    public int getDataUnguarded() {
        System.out.println(Thread.currentThread().getName() + " (UNGUARDED) reading data: " + this.sharedData);
        return this.sharedData;
    }

    // 正确的读方法
    public int getDataGuarded() {
        synchronized (lock) {
            System.out.println(Thread.currentThread().getName() + " acquired lock, reading data: " + this.sharedData);
            return this.sharedData;
        }
    }


    public static void main(String[] args) {
        FlawedGuardedResource resource = new FlawedGuardedResource();

        // 线程1：使用未受保护的方法写入数据
        Thread writer1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                resource.setDataUnguarded(i * 10);
            }
        }, "WriterThread-Unguarded");

        // 线程2：使用受保护的方法写入数据
        Thread writer2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                resource.setDataGuarded(i * 100 + 5);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "WriterThread-Guarded");

        // 线程3：使用未受保护的方法读取数据
        Thread reader1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                resource.getDataUnguarded();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ReaderThread-Unguarded");


        writer1.start();
        reader1.start();
        writer2.start();

        // 分析工具应该能够指出 setDataUnguarded 和 getDataUnguarded
        // 对 sharedData 的访问违反了它应该被 lock 保护的约定。
        // 运行时可能会看到不一致的数据。
    }
}