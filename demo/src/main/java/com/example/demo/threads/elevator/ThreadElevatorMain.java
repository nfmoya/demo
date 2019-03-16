package com.example.demo.threads.elevator;

import java.util.Scanner;

/**
 *
 */
public class ThreadElevatorMain {

    private static ThreadElevatorController threadElevatorController;
    private static Thread elevatorControllerThread;

    public static void main(String [ ] args){

        threadElevatorController = ThreadElevatorController.getInstance();
        elevatorControllerThread = new Thread(threadElevatorController);
        elevatorControllerThread.start();

        int choice;

        while(true) {

            // Allows a person to enter his/her name
            Scanner input = new Scanner(System.in);
            System.out.println("Enter choice (number): \n 1. ThreadElevator status \n 2. Request elevator");
            choice = input.nextInt();

            if(choice == 1){
                input = new Scanner(System.in);
                System.out.println("Enter the threadElevator number (from 0 to 15): ");
                choice = input.nextInt();

                ThreadElevator threadElevator = ThreadElevatorController.getInstance().getElevatorList().get(choice);
                System.out.println("ThreadElevator - " + threadElevator.getId() + " | Current floor - " + threadElevator.getCurrentFloor()
                        + " | Status - " + threadElevator.getElevatorState());
            }

            if(choice == 2) {
                input = new Scanner(System.in);
                System.out.println("Enter the floor where threadElevator is requested from (0 to 15): ");
                int reqestFloor = input.nextInt();

                input = new Scanner(System.in);
                System.out.println("Enter the destination floor(0 to 15): ");
                int targetFloor = input.nextInt();

                ThreadElevatorRequest threadElevatorRequest = new ThreadElevatorRequest(reqestFloor, targetFloor);
                ThreadElevator threadElevator = threadElevatorRequest.submitRequest();

            }

        }

    }
}
