package com.example.demo.threads.elevator;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Building {

    private String buildingName;
    private Integer elevatorNumbers;
    private Integer floorNumbers;
    private Integer maxNumberPersons;
    ExecutorService ex = Executors.newSingleThreadExecutor();

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

    public synchronized void requestElevator(final Requestor requestor) {
        Runnable task = () -> {
            try {
                System.out.println("********************************************************" +
                        "\nWelcome to " + this.buildingName + " Mr/Miss " + requestor.getRequestorName());
                System.out.println("Elevator go " + requestor.getRequestFloor() + " floor to " + requestor.getTargetFloor()
                        + "\n*************************************************\n ");
                Thread.sleep(1000);

                Elevator elevator = ElevatorController.getInstance().selectElevator(requestor);

                int floorToMove = Math.abs((elevator.getCurrentFloor() - requestor.getRequestFloor())
                        + requestor.getTargetFloor());
                long millisToSleep = floorToMove > 5 ? floorToMove * 1300 : floorToMove * 1900;

                Thread.sleep(millisToSleep);
                System.out.println("Thanks for you visit Mr/Miss: " + requestor.getRequestorName() + "\n");
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
