package threads_one_array;

import java.util.ArrayList;

public class Main {

    static final int ARRAY_SIZE = 20;
    static final int THREADS_QTY = 7;

    static int[] dataArray = new int[ARRAY_SIZE];

    public static void main(String[] args) {

        // Инициализация массива
        for(int i = 0; i < ARRAY_SIZE; i++){
            dataArray[i] = 1;
        }

        // Фабрика генерит потоки
        Thread[] threads = new ThreadFabric(dataArray, THREADS_QTY).generate();
        System.out.println("Created " + threads.length + " threads");
        System.out.println("Started");

        // Стартуем потоки
        for(Thread t : threads) t.start();

        // Ждем их завершения и выводим результат
        try {
            for (Thread t : threads) t.join();
            System.out.println("Finished");

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
    private final int[] sourceData;

    public ThreadFabric(int[] sourceData, int threads) {
        this.sourceData = sourceData;
        this.arraySize = sourceData.length;
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
            al.add(new Calculator(Integer.toString(i), sourceData, index, length));
            index += length;
        }
        return al.toArray(new Thread[al.size()]);
    }

}

class Calculator extends Thread {
    private final int indexStart;
    private final int length;
    private final int[] array;

    public Calculator(String name, int[] array, int indexStart, int length) {
        super(name);
        this.array = array;
        this.indexStart = indexStart;
        this.length = length;
    }

    @Override
    public void run() {
        System.out.println("Thread " + this.getName() + " started; startIndex=" + indexStart + ", range=" + length);
        for(int i = indexStart; i < (indexStart + length); i++) {
            mathProc(i);
        }
    }

    private void mathProc(int i) {
        array[i] += 1;
    }
}