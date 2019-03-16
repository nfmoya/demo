package com.example.demo.threads.elevator.executors;

public class Requestor {

    private int requestFloor;
    private int targetFloor;
    public Requestor(int requestFloor, int targetFloor){
        this.requestFloor = requestFloor;
        this.targetFloor = targetFloor;
    }

    public int getRequestFloor() {
        return requestFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
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
