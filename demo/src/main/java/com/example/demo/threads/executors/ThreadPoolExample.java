package com.example.demo.threads.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExample {

    public static void main(String args[]) {
        System.out.println("Start execution");
//        executeWithSingleThread();
//        executeWithFixedPool();
//        executeWithCachedThreadPool();
    }

    /**
     * This is an example for executor framework with single Thread.
     */
    public static void executeWithSingleThread() {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Task t1 = new Task("Task 1-1");
        Task2 t2 = new Task2("Task 2-1");
        Task t3 = new Task("Task 1-2");
        Task2 t4 = new Task2("Task 2-2");
        Task t5 = new Task("Task 1-3");
        Task2 t6 = new Task2("Task 2-3");

        ex.execute(t1);
        ex.execute(t2);
        ex.execute(t3);
        ex.execute(t4);
        ex.execute(t5);
        ex.execute(t6);
        ex.shutdown();
    }

    /**
     * This is an example for executor framework with fixed thread pool.
     */
    public static void executeWithFixedPool() {
        ExecutorService ex = Executors.newFixedThreadPool(6);
        Task t1 = new Task("Task 1-1");
        Task2 t2 = new Task2("Task 2-1");
        Task t3 = new Task("Task 1-2");
        Task2 t4 = new Task2("Task 2-2");
        Task t5 = new Task("Task 1-3");
        Task2 t6 = new Task2("Task 2-3");

        ex.execute(t1);
        ex.execute(t2);
        ex.execute(t3);
        ex.execute(t4);
        ex.execute(t5);
        ex.execute(t6);
        ex.shutdown();
    }

    /**
     * This is an example for executor framework with cache thread pool.
     */
    public static void executeWithCachedThreadPool() {
        ExecutorService ex = Executors.newCachedThreadPool();
        for (int i = 1; i <= 10; i++) {
            Task t1 = new Task("Task 1-" + i);
            Task2 t2 = new Task2("Task 2-" + i);
            ex.execute(t1);
            ex.execute(t2);
        }
        ex.shutdown();
    }
}
