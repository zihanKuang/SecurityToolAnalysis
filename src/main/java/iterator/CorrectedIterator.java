package iterator; // 或者你实际的包名

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CorrectedIterator {
    // CopyOnWriteArrayList 在修改时会创建一个底层数组的副本，迭代器遍历的是原始数组的快照
    // 这保证了迭代的线程安全，但写操作的成本较高
    private List<String> sharedList = new CopyOnWriteArrayList<>();

    public CorrectedIterator() {
        for (int i = 0; i < 100; i++) {
            sharedList.add("Item " + i);
        }
    }

    public void iterateAndPrint() {
        System.out.println(Thread.currentThread().getName() + " starts iterating.");
        try {
            Iterator<String> iterator = sharedList.iterator();
            while (iterator.hasNext()) {
                String item = iterator.next();
                // System.out.println(Thread.currentThread().getName() + " processing: " + item);
                Thread.sleep(1);
            }
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName() + " caught an exception: " + e.getMessage());
        }
        System.out.println(Thread.currentThread().getName() + " finished iterating.");
    }

    public void modifyList() {
        System.out.println(Thread.currentThread().getName() + " starts modifying.");
        try {
            Thread.sleep(10);
            sharedList.add("New Item from " + Thread.currentThread().getName());
            if (!sharedList.isEmpty()){
                sharedList.remove(0);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(Thread.currentThread().getName() + " finished modifying.");
    }

    public static void main(String[] args) {
        CorrectedIterator correctedIterator = new CorrectedIterator();

        Thread iteratorThread1 = new Thread(correctedIterator::iterateAndPrint, "IteratorThread-1");
        Thread modifierThread = new Thread(correctedIterator::modifyList, "ModifierThread");

        iteratorThread1.start();
        modifierThread.start();

        // 在这个版本中，不应该抛出 ConcurrentModificationException
    }
}