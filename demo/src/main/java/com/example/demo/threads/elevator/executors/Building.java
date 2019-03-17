package com.example.demo.threads.elevator.executors;

import com.example.demo.threads.elevator.ThreadElevator;
import org.bouncycastle.util.Times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Building {

    public String getBuildingName() {
        return buildingName;
    }

    private String buildingName;
    private Integer elevatorNumbers;
    private Integer floorNumbers;
    private Integer maxNumberPersons;
    ExecutorService ex = Executors.newSingleThreadExecutor();
    private List<Requestor> requestors = new ArrayList<>();

    private static List<ThreadElevator> threadElevatorList = new ArrayList<>();
    private static ElevatorController elevatorController;

    public Building(final String buildingName,
                    final Integer elevatorNumbers,
                    final Integer floorNumber,
                    final Integer maxNumberPersons) {
        this.buildingName = buildingName;
        this.elevatorNumbers = elevatorNumbers;
        this.floorNumbers = floorNumber;
        this.maxNumberPersons = maxNumberPersons;
        initElevatorController();
    }

    private void initElevatorController() {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        elevatorController = ElevatorController.getInstance();
        elevatorController.initializeElevators(elevatorNumbers, maxNumberPersons);
        ex.execute(elevatorController);
    }

    public void requestElevator(final Requestor requestor) {
        Runnable task = () -> {
            try {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                System.out.println("Welcome to :" + this.buildingName);
                Thread.sleep(6000);
                System.out.println("Elevator go " + requestor.getRequestFloor() + " floor to " + requestor.getTargetFloor());

                Elevator elevator = ElevatorController.getInstance().selectElevator(requestor);

                Thread.sleep(12000);
                System.out.println("Thanks for you visit");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        ex.execute(task);

    }

    public void requestElevator(final List<Requestor> requestors) throws InterruptedException, ExecutionException {
        ExecutorService ex = Executors.newFixedThreadPool(2);

//        List<Callable<Elevator>> callables = new ArrayList<>();
        for (Requestor requestor :
                requestors) {
            ex.execute(requestor);
            ex.awaitTermination(5, TimeUnit.SECONDS);
//            Callable task1 = () ->
//                    ElevatorController.getInstance().selectElevator(requestor);
//            callables.add(task1);
        }
//
//        ex.invokeAll(callables);
//        for (Requestor requestor :
//                requestors) {
//            Runnable task = () -> ElevatorController.getInstance().selectElevator(requestor);
//            Thread t = new Thread(task);
//            t.setPriority(Thread.MAX_PRIORITY);
//            t.start();
//        }
    }
}
