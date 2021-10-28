package ru.netology;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static final int RANDOM_BOUND = 100;
    private static final int THREADS_TO_WRITE_COUNT = 3;
    private static final int THREADS_TO_READ_COUNT = 3;

    public static void main(String[] args) {
        final Random random = new Random();

        final int[] arrayToWrite = generateIntArray(100_000, random);
        final int[] arrayToRead = generateIntArray(1_000_000, random);

        final long concurrentHashMapTime = testMap(new ConcurrentHashMap<>(), arrayToWrite, arrayToRead);
        System.out.println("ConcurrentHashMap time: " + concurrentHashMapTime);
        final long synchronizedMapTime = testMap(Collections.synchronizedMap(new HashMap<>()), arrayToWrite, arrayToRead);
        System.out.println("Collections.synchronizedMap time: " + synchronizedMapTime);
    }

    private static int[] generateIntArray(int size, Random random) {
        final int[] resultArray = new int[size];
        for (int i = 0; i < size; i++) {
            resultArray[i] = random.nextInt(RANDOM_BOUND);
        }
        return resultArray;
    }

    private static long testMap(Map<Integer, Integer> map, int[] arrayToWrite, int[] arrayToRead) {
        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREADS_TO_WRITE_COUNT; i++) {
            threads.add(new Thread(getWriteAction(map, arrayToWrite)));
        }
        for (int i = 0; i < THREADS_TO_READ_COUNT; i++) {
            threads.add(new Thread(getReadAction(map, arrayToRead)));
        }
        final long start = System.currentTimeMillis();
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return System.currentTimeMillis() - start;
    }

    private static Runnable getWriteAction(Map<Integer, Integer> map, int[] arrayToWrite) {
        return () -> {
            for (int i : arrayToWrite) {
                map.put(i, i);
            }
        };
    }

    private static Runnable getReadAction(Map<Integer, Integer> map, int[] arrayToRead) {
        return () -> {
            for (int i : arrayToRead) {
                map.get(i);
            }
        };
    }
}