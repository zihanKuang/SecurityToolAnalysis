package iterator; // 或者你实际的包名，例如 se.chalmers.firegroup54.raceconditions.iterator

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlawedIterator {
    private List<String> sharedList = new ArrayList<>();

    public FlawedIterator() {
        // 初始化一些数据
        for (int i = 0; i < 100; i++) {
            sharedList.add("Item " + i);
        }
    }

    public void iterateAndPrint() {
        System.out.println(Thread.currentThread().getName() + " starts iterating.");
        try {
            // 迭代共享列表
            Iterator<String> iterator = sharedList.iterator();
            while (iterator.hasNext()) {
                String item = iterator.next();
                // System.out.println(Thread.currentThread().getName() + " processing: " + item);
                Thread.sleep(1); // 模拟处理时间，增加竞态发生概率
            }
        } catch (Exception e) { // 通常是 ConcurrentModificationException
            System.err.println(Thread.currentThread().getName() + " caught an exception during iteration: " + e.getMessage());
        }
        System.out.println(Thread.currentThread().getName() + " finished iterating.");
    }

    public void modifyList() {
        System.out.println(Thread.currentThread().getName() + " starts modifying.");
        try {
            Thread.sleep(10); // 稍微延迟，让迭代线程先开始
            sharedList.add("New Item from " + Thread.currentThread().getName()); // 在迭代过程中修改列表
            sharedList.remove(0); // 再删除一个元素
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(Thread.currentThread().getName() + " finished modifying.");
    }

    public static void main(String[] args) {
        FlawedIterator flawedIterator = new FlawedIterator();

        Thread iteratorThread1 = new Thread(flawedIterator::iterateAndPrint, "IteratorThread-1");
        Thread modifierThread = new Thread(flawedIterator::modifyList, "ModifierThread");
        // Thread iteratorThread2 = new Thread(flawedIterator::iterateAndPrint, "IteratorThread-2"); // 也可以再加一个迭代线程

        iteratorThread1.start();
        modifierThread.start();
        // iteratorThread2.start();

        // 等待线程结束不是这个测试的核心，核心是观察是否抛出 ConcurrentModificationException
        // 分析工具应该在静态分析阶段就能指出这种不安全的并发访问模式
    }
}