package testcases;

public class ProxyProtocolSimulator {
    public static void main(String[] args) {
        SharedWriter writer = new SharedWriter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                writer.write("Thread-" + Thread.currentThread().getName() + " i=" + i);
            }
        };

        new Thread(task).start();
        new Thread(task).start();
    }
}
