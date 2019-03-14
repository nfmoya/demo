package com.example.demo.threads;

public class MyThread2 extends Thread {

    public MyThread2(String threadname) {
        super(threadname);
    }

    @Override
    public void run() {
        try {
            for (int i = 5; i > 0; i--) {
                System.out.println(Thread.currentThread().getName() + ": " + i);

                Thread.sleep(1000);

            }

        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + "Interrupted");
        }
        System.out.println(Thread.currentThread().getName() + " exiting.");
    }

}
