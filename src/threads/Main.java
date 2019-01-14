package threads;

import java.util.ArrayList;

public class Main {

    static final int size = 200;
    static final int h = size / 2;

    static int[] dataArray = new int[size];

    public static void main(String[] args) {
        System.out.println("Hello");

        // Инициализация массива
        for(int i = 0; i < size; i++){
            dataArray[i] = 1;
        }

        // Фабрика потоков
//        ThreadFabric tf = new ThreadFabric(size, 7);
        ThreadFabric tf = new ThreadFabric(size, 17);
        Thread[] threads = tf.generate();

        System.out.println("Created " + threads.length + " threads");

        for(Thread t : threads) t.start();

        try {
            for (Thread t : threads) t.join();

            for (int i = 0; i < dataArray.length; i++){
                System.out.println(Main.dataArray[i] + "\n");
            }

        } catch (InterruptedException e) {}
    }
}



/**
 *
 */

class ThreadFabric {
    private final int arraySize;
    private final int threads;

    public ThreadFabric(int arraySize, int threads) {
        this.arraySize = arraySize;
        this.threads = threads;
    }

    /**
     * Нужно сгенерить threads потоков и равномерно их нагрузить. Если длина исходного
     * массива не делится нацело на число потоков, то количество элементов массива,
     * равное остатку от деления равномерно "размазываем" между потоками. В результате
     * некторомы потокам придется обрабатывать на один элемент массива больше.
     */
    Thread[] generate(){
        int subArraySize = arraySize / threads;
        int remainder = arraySize % threads;
        int index = 0;

        ArrayList<Thread> al = new ArrayList<>();

        for(int i = 0; i < threads; i++) {
            int length = (i < remainder) ?  (subArraySize  + 1) : subArraySize;
            al.add(new Calculator(Integer.toString(i), index, length));
            index += length;
        }

        return al.toArray(new Thread[al.size()]);
    }

}

class Calculator extends Thread {
    private int indexStart;
    private int qty;

    public Calculator(String name, int indexStart, int qty) {
        super(name);
        this.indexStart = indexStart;
        this.qty = qty;
    }

    @Override
    public void run() {
        System.out.println("Thread " + this.getName() + ", startIndex=" + indexStart + ", range=" + qty);
        for(int i = indexStart; i < (indexStart + qty); i++) {
            Main.dataArray[i] += 1;
        }
    }


}

