package com.example.demo.threads.elevator;

/**
 * Represents a request for an user to use the elevator
 */
public class ThreadElevatorRequest {
    private int requestFloor;
    private int targetFloor;

    public ThreadElevatorRequest(int requestFloor, int targetFloor){
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
    public ThreadElevator submitRequest(){
        return ThreadElevatorController.getInstance().selectElevator(this);
    }
}
