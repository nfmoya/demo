package com.example.demo.threads.elevator.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElevatorMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException {


        Building laminar = new Building("Laminar", 1, 10, 8);

        Requestor requestor = new Requestor(laminar,0, 8);
        Requestor requestor2 = new Requestor(laminar,0, 6);
        Requestor requestor3 = new Requestor(laminar,6, 9);

//        List<Requestor> requestors = Arrays.asList(requestor, requestor2,requestor3);
//        laminar.requestElevator(requestors);

        ExecutorService ex = Executors.newFixedThreadPool(100);

        ex.execute(requestor);
        ex.execute(requestor2);
        ex.execute(requestor3);

    }
}
