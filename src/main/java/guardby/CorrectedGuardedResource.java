package guardby; // 或者你实际的包名

public class CorrectedGuardedResource {
    private final Object lock = new Object();
    private int sharedData = 0; // @GuardedBy("lock")

    public void setData(int data) {
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

    public int getData() {
        synchronized (lock) {
            System.out.println(Thread.currentThread().getName() + " acquired lock, reading data: " + this.sharedData);
            return this.sharedData;
        }
    }

    public static void main(String[] args) {
        CorrectedGuardedResource resource = new CorrectedGuardedResource();

        Thread writer1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                resource.setData(i * 10);
            }
        }, "WriterThread-1");

        Thread writer2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                resource.setData(i * 100 + 5);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "WriterThread-2");

        Thread reader1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + " read: " + resource.getData());
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "ReaderThread-1");

        writer1.start();
        reader1.start();
        writer2.start();

        // 在这个版本中，所有对 sharedData 的访问都是线程安全的。
    }
}