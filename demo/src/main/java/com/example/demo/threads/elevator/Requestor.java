package com.example.demo.threads.elevator;

public class Requestor implements Runnable {

    private int requestFloor;
    private int targetFloor;
    private Building building;

    private String requestorName;

    public Requestor(Building building, int requestFloor, int targetFloor, String requestorName) {
        this.requestFloor = requestFloor;
        this.targetFloor = targetFloor;
        this.building = building;
        this.requestorName = requestorName;
    }

    public Requestor(int requestFloor, int targetFloor) {
        this.requestFloor = requestFloor;
        this.targetFloor = targetFloor;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public int getRequestFloor() {
        return requestFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    @Override
    public void run() {
        synchronized (this){
            building.requestElevator(this);
        }
    }
}
