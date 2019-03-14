package com.example.demo.threads;

public class ExecutionThread {
    public static void main(String args[]) throws InterruptedException {
        executeMyThread1();
//        executeMyThread2();

    }

    private static void executeMyThread1(){
        new MyThread("Thread One");
        new MyThread("Thread Two");
        new MyThread("Thread Three");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }
        System.out.println("Main thread exiting.");
    }

    private static void executeMyThread2() throws InterruptedException {
        MyThread2 t1 = new MyThread2("Thread One");
        t1.setPriority(Thread.MIN_PRIORITY);

        MyThread2 t2 = new MyThread2("Thread Two");
        t2.setPriority(Thread.MAX_PRIORITY);

        MyThread2 t3 = new MyThread2("Thread Three");
        t3.setPriority(Thread.NORM_PRIORITY);

        t1.start();
        t2.start();
        t3.start();

    }
}



//    Runnable task = () -> {
//        String name = "lambda";
//        try {
//            for (int i = 5; i > 0; i--) {
//                System.out.println(name + ": " + i);
//                Thread.sleep(1000);
//            }
//        } catch (InterruptedException e) {
//            System.out.println(name + "Interrupted");
//        }
//        System.out.println(name + " exiting.");
//    };
//    Thread thread = new Thread(task);
//        thread.start();