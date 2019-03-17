package com.example.demo.threads.elevator.executors;

public class Requestor implements Runnable {

    private int requestFloor;
    private int targetFloor;
    private Building building;

    public Requestor(Building building,int requestFloor, int targetFloor) {
        this.requestFloor = requestFloor;
        this.targetFloor = targetFloor;
        this.building = building;
    }

    public Requestor(int requestFloor, int targetFloor) {
        this.requestFloor = requestFloor;
        this.targetFloor = targetFloor;
    }

    public int getRequestFloor() {
        return requestFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    @Override
    public void run() {
        try {
            building.requestElevator(this);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Submit the request to the ThreadElevatorController to select the
     * optimal elevator for this request
     * @return
     */
//    public ThreadElevator submitRequest(){
//        return ElevatorController.getInstance().selectElevator(this);
//    }

}
