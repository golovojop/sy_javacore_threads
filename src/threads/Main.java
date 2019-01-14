package threads;

import java.util.ArrayList;

public class Main {

    static final int ARRAY_SIZE = 10000000;
    static float[] dataArray = new float[ARRAY_SIZE];

    static final int ONE_THREAD = 1;
    static final int MULTI_THREADS = 3;

    /**
     * TODO: Точка входа
     */
    public static void main(String[] args) {

        // Инициализация массива
        Main.initData();

        // Вычисляем в один поток
        long millis = System.currentTimeMillis();
        calculateIn(ONE_THREAD);
        System.out.println("Result: threads " + ONE_THREAD + ", duration " + (System.currentTimeMillis() - millis) + " ms\n");

        // Вычисляем в несколько потоков
        Main.initData();
        millis = System.currentTimeMillis();
        calculateIn(MULTI_THREADS);
        System.out.println("Result: threads " + MULTI_THREADS + ", duration " + (System.currentTimeMillis() - millis) + " ms\n");
    }

    /**
     * TODO: Сгенерить требуемое кол-во потоков. Если потоков несколько, то выдать каждому
     * TODO: копию части исходного массива для вычислений.
     *
     * @param threadsQty Количество потоков вычисления
     */
    public static void calculateIn(int threadsQty){

        if(threadsQty == 1) {
            // Фабрика генерит один поток без создания копии основного массива
            Thread t = new ThreadFabric(dataArray, threadsQty).generateNoCopy();
            System.out.println("Created 1 thread");
            t.start();

            try{
                t.join();
            } catch (InterruptedException e) {}
        }
        else {
            // Фабрика генерит несколько потоков, делая копию части исходного массива
            Thread[] threads = new ThreadFabric(dataArray, threadsQty).generateWithCopy();
            System.out.println("Created " + threads.length + " threads");

            // Стартуем потоки
            for(Thread t : threads) t.start();

            // Ждем завершения
            try {
                for (Thread t : threads) t.join();

                // Собрать результирующий массив
                int destPos = 0;
                for(int i = 0; i < threads.length; i++){
                    Calculator calc = (Calculator)threads[i];
                    System.arraycopy(calc.getArray(), 0, dataArray, destPos, calc.getLength());
                    destPos += calc.getLength();
                }
            } catch (InterruptedException e) {}
        }
    }

    /**
     * Инициализация исходного массива
     */
    static void initData() {
        for(int i = 0; i < Main.dataArray.length; i++) Main.dataArray[i] = 1;
    }
}

/**
 *
 */
class ThreadFabric {
    private final int sourceDataLength;
    private float[] sourceData;
    private final int threads;

    public ThreadFabric(float[] sourceData, int threads) {
        this.sourceDataLength = sourceData.length;
        this.sourceData = sourceData;
        this.threads = threads;
    }

    /**
     * Нужно сгенерить threads потоков и равномерно их нагрузить. Если длина исходного
     * массива не делится нацело на число потоков, то количество элементов массива,
     * равное остатку от деления равномерно "размазываем" между потоками. В результате
     * некторомы потокам придется обрабатывать на один элемент массива больше.
     */
    Thread[] generateWithCopy() {
        int subArraySize = sourceDataLength / threads;
        int remainder = sourceDataLength % threads;
        int index = 0;

        ArrayList<Thread> al = new ArrayList<>();

        for(int i = 0; i < threads; i++) {
            int length = (i < remainder) ?  (subArraySize  + 1) : subArraySize;

            float[] subArray = new float[length];
            System.arraycopy(sourceData, index, subArray, 0, length);

            al.add(new Calculator(Integer.toString(i), subArray));
            index += length;
        }
        return al.toArray(new Thread[al.size()]);
    }

    Thread generateNoCopy() {
        return new Calculator("single", sourceData);
    }
}

/**
 * Поток вычислений
 */
class Calculator extends Thread {
    private final int length;
    private final float[] array;

    public Calculator(String name, float[] array) {
        super(name);
        this.array = array;
        this.length = array.length;
    }

    public int getLength() {
        return length;
    }

    public float[] getArray() {
        return array;
    }

    @Override
    public void run() {
        System.out.println("Thread " + this.getName() + " started. Array length " + length);
        for(int i = 0; i < length; i++) mathProc(i);
    }

    private void mathProc(int i) {
        array[i] = (float)(array[i] * Math.sin(0.2f + i/5) * Math.cos(0.2f + i/5) * Math.cos(0.4f + i/2));
    }
}

