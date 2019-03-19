package com.example.demo.threads.elevator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElevatorMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException {


        Building laminar = new Building("Laminar", 1, 10, 8);

        Requestor requestor = new Requestor(laminar,0, 8,"La Mora");
        Requestor requestor2 = new Requestor(laminar,6, 8, "Rochi");
        Requestor requestor3 = new Requestor(laminar,0, 9, "Migoya");

        ExecutorService ex = Executors.newFixedThreadPool(100);

        ex.execute(requestor);
        ex.execute(requestor2);
        ex.execute(requestor3);

    }
}
