package com.example.demo.threads.elevator;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * This is the core component of the system. This class is responsible for
 * the complete orchestration of elevators and elevator requests.
 */
public final class ThreadElevatorController implements Runnable {

    private boolean stopController;

    // All the UP moving elevators
    private static Map<Integer, ThreadElevator> upMovingMap = new HashMap<Integer, ThreadElevator>();

    // All the DOWN moving elevators
    private static Map<Integer, ThreadElevator> downMovingMap = new HashMap<Integer, ThreadElevator>();
    // STATIONARY elevators are part of UP and DOWN map both.

    private static List<ThreadElevator> threadElevatorList = new ArrayList<ThreadElevator>(16);

    private static final ThreadElevatorController instance = new ThreadElevatorController();
    private ThreadElevatorController(){
        if(instance != null){
            throw new IllegalStateException("Already instantiated");
        }
        setStopController(false);
//        initializeElevators();
    }

    public static ThreadElevatorController getInstance(){
        return instance;
    }

    /**
     * Select an elevator from the pool of operational elevators that can serve the
     * the request optimally
     * @param threadElevatorRequest  Represents the request for an elevator
     * @return Selected ThreadElevator
     */
    public synchronized ThreadElevator selectElevator(ThreadElevatorRequest threadElevatorRequest) {

        ThreadElevator threadElevator = null;

        ElevatorState elevatorState = getRequestedElevatorDirection(threadElevatorRequest);
        int requestedFloor = threadElevatorRequest.getRequestFloor();
        int targetFloor = threadElevatorRequest.getTargetFloor();

        threadElevator = findElevator(elevatorState, requestedFloor, targetFloor);

        // So that elevators can start moving again.
        notifyAll();
        return threadElevator;


    }

    public void initializeElevators(Integer numberElevators){
        for(int i=0; i<numberElevators; i++){
            ThreadElevator threadElevator = new ThreadElevator(i+1);
            Thread t = new Thread(threadElevator);
            t.start();

            threadElevatorList.add(threadElevator);
        }
    }

    private static ElevatorState getRequestedElevatorDirection(ThreadElevatorRequest threadElevatorRequest){
        ElevatorState elevatorState = null;
        int requestedFloor = threadElevatorRequest.getRequestFloor();
        int targetFloor = threadElevatorRequest.getTargetFloor();

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
    private static ThreadElevator findElevator(ElevatorState elevatorState, int requestedFloor, int targetFloor) {
        ThreadElevator threadElevator = null;

        // Data structure to hold distance of eligible elevators from the request floor
        // The keys represent the current distance of an threadElevator from request floor
        TreeMap<Integer, Integer> sortedKeyMap = new TreeMap<Integer, Integer>();

        if(elevatorState.equals(ElevatorState.UP)){

            // Let's go over all elevators that are either going UP or are STATIONARY
            for(Map.Entry<Integer, ThreadElevator> elvMap : upMovingMap.entrySet()){
                ThreadElevator elv = elvMap.getValue();
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
            threadElevator = upMovingMap.get(selectedElevatorId);


        } else if(elevatorState.equals(ElevatorState.DOWN)){
            // Let's go over all elevators that are either going DOWN or are STATIONARY
            for(Map.Entry<Integer, ThreadElevator> elvMap : downMovingMap.entrySet()){
                ThreadElevator elv = elvMap.getValue();
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
            threadElevator = downMovingMap.get(selectedElevatorId);

        }

        // Instructing the selected threadElevator to stop/pass by relavent floors
        ThreadElevatorRequest newRequest = new ThreadElevatorRequest(threadElevator.getCurrentFloor(), requestedFloor);
        ElevatorState elevatorDirection = getRequestedElevatorDirection(newRequest);

        // helpful if we are moving in opposite direction to than that of request
        ThreadElevatorRequest newRequest2 = new ThreadElevatorRequest(requestedFloor, targetFloor);
        ElevatorState elevatorDirection2 = getRequestedElevatorDirection(newRequest2);

        NavigableSet<Integer> floorSet = threadElevator.floorStopsMap.get(elevatorDirection);
        if (floorSet == null) {
            floorSet = new ConcurrentSkipListSet<Integer>();
        }

        floorSet.add(threadElevator.getCurrentFloor());
        floorSet.add(requestedFloor);
        threadElevator.floorStopsMap.put(elevatorDirection, floorSet);

        NavigableSet<Integer> floorSet2 = threadElevator.floorStopsMap.get(elevatorDirection2);
        if (floorSet2 == null) {
            floorSet2 = new ConcurrentSkipListSet<Integer>();
        }

        floorSet2.add(requestedFloor);
        floorSet2.add(targetFloor);
        threadElevator.floorStopsMap.put(elevatorDirection2, floorSet2);

        return threadElevator;
    }


    /**
     * update the state of threadElevator as soon as it changes the direction
     * @param threadElevator
     */
    public static synchronized void updateElevatorLists(ThreadElevator threadElevator){
        if(threadElevator.getElevatorState().equals(ElevatorState.UP)){
            upMovingMap.put(threadElevator.getId(), threadElevator);
            downMovingMap.remove(threadElevator.getId());
        } else if(threadElevator.getElevatorState().equals(ElevatorState.DOWN)){
            downMovingMap.put(threadElevator.getId(), threadElevator);
            upMovingMap.remove(threadElevator.getId());
        } else if (threadElevator.getElevatorState().equals(ElevatorState.STATIONARY)){
            upMovingMap.put(threadElevator.getId(), threadElevator);
            downMovingMap.put(threadElevator.getId(), threadElevator);
        } else if (threadElevator.getElevatorState().equals(ElevatorState.MAINTAINANCE)){
            upMovingMap.remove(threadElevator.getId());
            downMovingMap.remove(threadElevator.getId());
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

    public synchronized List<ThreadElevator> getElevatorList() {
        return threadElevatorList;
    }

    public boolean isStopController() {
        return stopController;
    }
}
