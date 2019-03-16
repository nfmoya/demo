package com.example.demo.threads.elevator.executors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ElevatorMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException {


        Building laminar = new Building("Laminar", 1, 10, 8);

        Requestor requestor = new Requestor(0, 8);
        Requestor requestor2 = new Requestor(3, 0);
        Requestor requestor3 = new Requestor(6, 9);

        List<Requestor> requestors = Arrays.asList(requestor, requestor2,requestor3);
        laminar.requestElevator(requestors);

    }
}
