package testcases;

//public class UnsafePublisher {
//    public static Holder holder;
//
//    public static void publish() {
//        holder = new Holder();
//    }
//
//    static class Holder {
//        int data = 42;
//    }
//}

public class UnsafePublisher {
    public static Holder holder;

    public static void publish() {
        Holder temp = new Holder();
        holder = temp;
    }

    public static class Holder {
        int data = 42;
    }

    public static void main(String[] args) {
        Thread writer = new Thread(() -> {
            publish();
            System.out.println("Published holder.");
        });
        Thread reader = new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Holder h = holder;
            if (h != null) {
                System.out.println("Read holder data = " + h.data);
            } else {
                System.out.println("Holder is null.");
            }
        });

        writer.start();
        reader.start();
    }
}


