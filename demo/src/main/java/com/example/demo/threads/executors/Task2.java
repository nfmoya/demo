package com.example.demo.threads.executors;

public class Task2 implements Runnable {

    private int sleepTime;
    private String name;

    public Task2(String name) {
        this.name = name;
        sleepTime = 3000;
    }


    @Override
    public void run() {
        try {
            System.out.println(name+", sleep... for 3 seconds");
            Thread.sleep(sleepTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        System.out.println("End sleep "+ name);
    }

}
