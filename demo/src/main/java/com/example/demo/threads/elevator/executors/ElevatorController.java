package com.example.demo.threads.elevator.executors;

import com.example.demo.threads.elevator.ElevatorState;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the core component of the system. This class is responsible for
 * the complete orchestration of elevators and elevator requests.
 */
public final class ElevatorController implements Runnable {

    private boolean stopController;

    // All the UP moving elevators
    private static Map<Integer, Elevator> upMovingMap = new HashMap<Integer, Elevator>();

    // All the DOWN moving elevators
    private static Map<Integer, Elevator> downMovingMap = new HashMap<Integer, Elevator>();
    // STATIONARY elevators are part of UP and DOWN map both.

    private static List<Elevator> elevatorList = new ArrayList<>();

    private static final ElevatorController instance = new ElevatorController();
    private ElevatorController(){
        if(instance != null){
            throw new IllegalStateException("Already instantiated");
        }
        setStopController(false);
//        initializeElevators();
    }

    public static ElevatorController getInstance(){
        return instance;
    }

    /**
     * Select an elevator from the pool of operational elevators that can serve the
     * the request optimally
     * @param requestor  Represents the request for an elevator
     * @return Selected ThreadElevator
     */
    public synchronized Elevator selectElevator(Requestor requestor) {

        Elevator elevator = null;

        ElevatorState elevatorState = getRequestedElevatorDirection(requestor);
        int requestedFloor = requestor.getRequestFloor();
        int targetFloor = requestor.getTargetFloor();

        elevator = findElevator(elevatorState, requestedFloor, targetFloor);

        // So that elevators can start moving again.
        notifyAll();
        return elevator;


    }

    public void initializeElevators(final Integer elevatorNumbers, final Integer maxNumberPersons){
        ExecutorService ex = Executors.newFixedThreadPool(maxNumberPersons);
        for(int i=0; i<elevatorNumbers; i++){
            Elevator threadElevator = new Elevator(i+1);
            ex.execute(threadElevator);

            elevatorList.add(threadElevator);
        }
    }

    private static ElevatorState getRequestedElevatorDirection(Requestor requestor){
        ElevatorState elevatorState = null;
        int requestedFloor = requestor.getRequestFloor();
        int targetFloor = requestor.getTargetFloor();

        if(targetFloor - requestedFloor > 0){
            elevatorState = ElevatorState.UP;
        } else {
            elevatorState = ElevatorState.DOWN;
        }

        return elevatorState;
    }

    /**
     * Internal method to select an elevator and generate UP and/or DOWN paths for it.
     * @param elevatorState UP, DOWN or STATIONARY
     * @param requestedFloor Floor number where request is originating from
     * @param targetFloor Floor number where user wants to go
     * @return selected elevator
     */
    private static Elevator findElevator(ElevatorState elevatorState, int requestedFloor, int targetFloor) {
        Elevator elevator = null;

        // Data structure to hold distance of eligible elevators from the request floor
        // The keys represent the current distance of an threadElevator from request floor
        TreeMap<Integer, Integer> sortedKeyMap = new TreeMap<Integer, Integer>();

        if(elevatorState.equals(ElevatorState.UP)){

            // Let's go over all elevators that are either going UP or are STATIONARY
            for(Map.Entry<Integer, Elevator> elvMap : upMovingMap.entrySet()){
                Elevator elv = elvMap.getValue();
                Integer distance = requestedFloor - elv.getCurrentFloor();
                if(distance < 0 && elv.getElevatorState().equals(ElevatorState.UP)){
                    // No point selecting these elevators. They have already passed by our request floor
                    continue;
                } else {
                    sortedKeyMap.put(Math.abs(distance), elv.getId());
                }
            }

            // TODO - potential NullPointerException
            Integer selectedElevatorId = sortedKeyMap.firstEntry().getValue();
            elevator = upMovingMap.get(selectedElevatorId);


        } else if(elevatorState.equals(ElevatorState.DOWN)){
            // Let's go over all elevators that are either going DOWN or are STATIONARY
            for(Map.Entry<Integer, Elevator> elvMap : downMovingMap.entrySet()){
                Elevator elv = elvMap.getValue();
                Integer distance = elv.getCurrentFloor() - requestedFloor;
                if(distance < 0 && elv.getElevatorState().equals(ElevatorState.DOWN)){
                    // No point selecting these elevators. They have already passed by our requested floor
                    continue;
                } else {
                    sortedKeyMap.put(Math.abs(distance), elv.getId());
                }
            }
            // TODO - potential NullPointerException
            Integer selectedElevatorId = sortedKeyMap.firstEntry().getValue();
            elevator = downMovingMap.get(selectedElevatorId);

        }

        // Instructing the selected threadElevator to stop/pass by relavent floors
        Requestor newRequest = new Requestor(elevator.getCurrentFloor(), requestedFloor);
        ElevatorState elevatorDirection = getRequestedElevatorDirection(newRequest);

        // helpful if we are moving in opposite direction to than that of request
        Requestor newRequest2 = new Requestor(requestedFloor, targetFloor);
        ElevatorState elevatorDirection2 = getRequestedElevatorDirection(newRequest2);

        NavigableSet<Integer> floorSet = elevator.floorStopsMap.get(elevatorDirection);
        if (floorSet == null) {
            floorSet = new ConcurrentSkipListSet<Integer>();
        }

        floorSet.add(elevator.getCurrentFloor());
        floorSet.add(requestedFloor);
        elevator.floorStopsMap.put(elevatorDirection, floorSet);

        NavigableSet<Integer> floorSet2 = elevator.floorStopsMap.get(elevatorDirection2);
        if (floorSet2 == null) {
            floorSet2 = new ConcurrentSkipListSet<Integer>();
        }

        floorSet2.add(requestedFloor);
        floorSet2.add(targetFloor);
        elevator.floorStopsMap.put(elevatorDirection2, floorSet2);

        return elevator;
    }


    /**
     * update the state of threadElevator as soon as it changes the direction
     * @param elevator
     */
    public static synchronized void updateElevatorLists(Elevator elevator){
        if(elevator.getElevatorState().equals(ElevatorState.UP)){
            upMovingMap.put(elevator.getId(), elevator);
            downMovingMap.remove(elevator.getId());
        } else if(elevator.getElevatorState().equals(ElevatorState.DOWN)){
            downMovingMap.put(elevator.getId(), elevator);
            upMovingMap.remove(elevator.getId());
        } else if (elevator.getElevatorState().equals(ElevatorState.STATIONARY)){
            upMovingMap.put(elevator.getId(), elevator);
            downMovingMap.put(elevator.getId(), elevator);
        } else if (elevator.getElevatorState().equals(ElevatorState.MAINTAINANCE)){
            upMovingMap.remove(elevator.getId());
            downMovingMap.remove(elevator.getId());
        }
    }

    @Override
    public void run() {
        stopController =  false;
        while(true){
            try {
                Thread.sleep(100);
                if(stopController){
                    break;
                }
            } catch (InterruptedException e){
                System.out.println(e.getStackTrace());
            }
        }
    }

    public void setStopController(boolean stop){
        this.stopController = stop;

    }

    public synchronized List<Elevator> getElevatorList() {
        return elevatorList;
    }

    public boolean isStopController() {
        return stopController;
    }
}
