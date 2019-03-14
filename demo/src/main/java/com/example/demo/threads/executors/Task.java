package com.example.demo.threads.executors;

public class Task implements Runnable {

    private int sleepTime;
    private String name;

    public Task(String name) {
        this.name = name;
        sleepTime = 5000;
    }


    @Override
    public void run() {
        try {
            System.out.println(name+", sleep... for 5 seconds");
            Thread.sleep(sleepTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        System.out.println("End sleep "+ name);
    }

}
