package threads;

import org.omg.CORBA.MARSHAL;

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
        Main.initArray();

        // Вычисляем в один поток
        long millis = System.currentTimeMillis();
        calculateIn(ONE_THREAD);
        System.out.println("Result: threads " + ONE_THREAD + ", duration " + (System.currentTimeMillis() - millis) + " ms\n");

        // Вычисляем в несколько потоков
        Main.initArray();
        millis = System.currentTimeMillis();
        calculateIn(MULTI_THREADS);
        System.out.println("Result: threads " + MULTI_THREADS + ", duration " + (System.currentTimeMillis() - millis) + " ms\n");
    }

    /**
     * Сгенерить требуемое кол-во потоков. Каждому выдать копию части исходного массива для вычислений.
     */
    public static void calculateIn(int threadsQty){

        switch(threadsQty) {
            case ONE_THREAD:
                // Генерим один поток без создания копии основного массива
                Thread tS = new Calculator("single", Main.dataArray);
                System.out.println("Created 1 thread");
                tS.start();

                try {
                    tS.join();
                } catch (InterruptedException e) {}
                break;
            default:
                // Фабрика генерит несколько потоков, делая копии частей исходного массива для каждого потока
                Thread[] threads = ThreadGenerator.generate(Main.dataArray, threadsQty);
                System.out.println("Created " + threads.length + " threads");

                // Стартуем потоки
                for (Thread t : threads) t.start();

                // Ждем завершения
                try {
                    for (Thread t : threads) t.join();

                    // Собрать результирующий массив
                    int destPos = 0;

                    for(Thread t: threads){
                        Calculator calc = (Calculator)t;
                        System.arraycopy(calc.getArray(), 0, Main.dataArray, destPos, calc.getLength());
                        destPos += calc.getLength();
                    }
                } catch (InterruptedException e) {}
        }
    }

    /**
     * Инициализация исходного массива
     */
    static void initArray() {
        for(int i = 0; i < Main.dataArray.length; i++) Main.dataArray[i] = 1;
    }
}

/**
 *
 */
class ThreadGenerator {
    /**
     * Нужно сгенерить thrdQty потоков и равномерно их нагрузить. Если длина исходного
     * массива не делится нацело на число потоков, то количество элементов массива,
     * равное остатку от деления, равномерно "размазываем" между потоками. В результате
     * нектором потокам придется обрабатывать на один элемент массива больше.
     */
    static Thread[] generate(float[] sourceData, int thrdQty){
        int subArrayLength = sourceData.length / thrdQty;
        int remainder = sourceData.length % thrdQty;
        int index = 0;

        ArrayList<Thread> al = new ArrayList<>();

        for(int i = 0; i < thrdQty; i++) {
            int length = (i < remainder) ?  (subArrayLength  + 1) : subArrayLength;

            float[] subArray = new float[length];
            System.arraycopy(sourceData, index, subArray, 0, length);
            al.add(new Calculator(Integer.toString(i), subArray));
            index += length;
        }
        return al.toArray(new Thread[al.size()]);
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