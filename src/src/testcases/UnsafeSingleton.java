package testcases;

public class UnsafeSingleton {
    private static UnsafeSingleton instance;

    public static UnsafeSingleton getInstance() {
        if (instance == null) {
            synchronized (UnsafeSingleton.class) {
                if (instance == null) {
                    instance = new UnsafeSingleton(); 
                }
            }
        }
        return instance;
    }

    private UnsafeSingleton() {}
}
