/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit; // do i need this?
import java.util.Random;

/**
 *
 * @author admin
 * This is the main class for all Planes; all passenger objects are declared and used here
 * plane action requests and ATC function calls are done here
 * 
 */
public class Plane implements Runnable {
    
    private final int ID;
    private boolean is_emergency;
    private boolean is_refueled;
    
    private final ATC atc;
    private final FuelTruck truck;
    
    private final int max_passengers = 20; // change maximum number of passengers per plane here
    private Passenger[] passengers;
    private final ExecutorService disembark_exs;
    private final ExecutorService board_exs;
    
    private final Thread resupply_thread;
    private final Thread refuel_thread;
    private final Thread cleaning_thread;
    
    // reference to own thread, to allow external control (i.e. thread prioritization)
//    private Thread plane_thread; 
    
    // constructor
    public Plane(int ID, ATC atc, FuelTruck truck, boolean is_emergency) {
        this.ID = ID;
        this.atc = atc;
        this.truck = truck;
        this.is_emergency = is_emergency;
        this.is_refueled = false;
        
        // generate current passengers on entering airspace
        this.passengers = generatePassengers();
        
        // use single thread executors for sequential execution
        disembark_exs = Executors.newSingleThreadExecutor();
        board_exs = Executors.newSingleThreadExecutor();
        
        // initialize the resupply thread
        resupply_thread = new Thread(() -> {
            try {
                resupplyPlane();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
 
        });
        
        // initialize the cleaning thread
        cleaning_thread = new Thread(() -> {
            try {
                cleanPlane();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });
        
        // initialize the refueling thread
        refuel_thread = new Thread(() -> {
                try {
                    truck.refuel(this); // request refuel truck
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            });
    }
    
    // implement all landing, docking, and takeoff, ATC request processes here
    @Override
    public void run() {
        try {
            System.out.println(Main.getTime()+ " Flight " + ID + ": entering the airspace");
            
            if(is_emergency) {
                // declare emergency
                System.out.println(Main.getTime() + " Flight " + ID + ": Declaring a fuel emergency");
            }
            atc.requestLanding(this); // request landing from atc

            Gate gate = atc.requestGate(this); // request a gate from atc
            
            System.out.println(Main.getTime() + " Flight " + ID + ": Affirmative, Heading to gate " + gate.getID());
            Thread.sleep(2000); // simulate time to taxi
            System.out.println(Main.getTime() + " Flight " + ID + ": Docked at gate " + gate.getID());
            
            refuel_thread.start(); // start service plane threads
            resupply_thread.start(); 
            cleaning_thread.start();
//            
//            // uncomment/comment block below to toggle passenger simulation
            System.out.println(Main.getTime()+ " Flight " + ID + ": Disembarking passengers");
            disembarkPassengers(); // disembark passengers
            System.out.println(Main.getTime()+ " Flight " + ID + ": Finished disembarking passengers");
            Thread.sleep(200); // simulate preparation delay between disembark and boarding procedures
            System.out.println(Main.getTime()+ " Flight " + ID + ": Boarding new passengers");
            boardPassengers(generatePassengers()); // board new passengers
            System.out.println(Main.getTime()+ " Flight " + ID + ": Finished boarding passengers");

            // Explicitly wait for all processes to finish before proceeding
            refuel_thread.join();
            resupply_thread.join();
            cleaning_thread.join();
            
            System.out.println(Main.getTime() + " Flight " + ID + ": Completed all Ground Operations, Initiating Pre-Flight Checks");
            Thread.sleep(1000); // simulate startup sequence

            atc.releaseGate(gate, this); // undock from gate
            System.out.println(Main.getTime() + " Flight " + ID + ": Leaving gate " + gate.getID());
            System.out.println(Main.getTime() + " Flight " + ID + ": Requesting permission to takeoff");
            atc.requestTakeoff(this);   // request takeoff from atc
            
            Thread.sleep(3000); // simulate time to leave airspace
            System.out.println(Main.getTime() + " Flight " + ID + ": Leaving the airspace");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
    
    public void land() throws InterruptedException {
        System.out.println(Main.getTime() + " Flight " + ID + ": Affirmative, landing on the runway");
        Thread.sleep(2000); // simulate landing process
        System.out.println(Main.getTime() + " Flight " + ID + ": Landed on the runway");
    }
    
    public void takeoff() throws InterruptedException {
        System.out.println(Main.getTime() + " Flight " + ID + ": Affirmative, heading to runway to takeoff");
        Thread.sleep(3000); // simulate time to takeoff
        System.out.println(Main.getTime() + " Flight " + ID + ": Airborne, now climbing to altitude");
    }
    
    private void resupplyPlane() throws InterruptedException {
        System.out.println(Main.getTime() + " Flight " + ID + ": Being resupplied");
        Thread.sleep(5000); // simulate time to resupply plane
        System.out.println(Main.getTime() + " Flight " + ID + ": Finished resupplying");
    }
    
    private void cleanPlane() throws InterruptedException {
        System.out.println(Main.getTime() + " Flight " + ID + ": Being cleaned");
        Thread.sleep(4000); // simulate time to clean plane
        System.out.println(Main.getTime() + " Flight " + ID + ": Finished cleaning");
    }
    
    // each passenger runs on their own thread, but are run sequentially because of singlethread executioner
    // could i have made this entire process a single thread? yes, but this is more fun
    private void disembarkPassengers() throws InterruptedException {
        for(Passenger passenger : passengers) {
            board_exs.execute(() -> {
                try {
                    passenger.disembark(this);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                } 
            });
            Thread.sleep(250); // simulate time for each passenger disembarking
        }
        
        board_exs.shutdown(); // tells service to stop accepting new tasks, but completes the already submitted ones
        board_exs.awaitTermination(2, TimeUnit.SECONDS); // ensures all tasks are completed before exiting function
    }
    
    public void boardPassengers(Passenger[] passengers) throws InterruptedException {
        for(Passenger passenger : passengers) {
            disembark_exs.execute(() -> {
                try {
                    passenger.board(this);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            });
            Thread.sleep(250); // time for each passenger to board
        }
        disembark_exs.shutdown(); // tells service to stop accepting new tasks, but completes the already submitted ones
        disembark_exs.awaitTermination(2, TimeUnit.SECONDS); // ensures all tasks are completed before exiting function
    }
    
    // simulate random passenger generation. Used in initialization and boarding
    private Passenger[] generatePassengers() {
        Random rdm = new Random();
        int count = rdm.nextInt(max_passengers - 4) + 5;
        Passenger[] passengers = new Passenger[count];
        
        for(int i = 0; i < count; i++) {
            passengers[i] = new Passenger(i + 1);
        }
        
        return passengers;
    }
    
    public int getPassengerCount() {
        return passengers.length;
    }
    
    public int getID() {
        return ID;
    }
    
    // used by atc to reset emergency status
    public void setEmergency(boolean state) {
        this.is_emergency = state;
    }
    
    public boolean isEmergency() {
        return is_emergency;
    }
    
    public void setRefueled(boolean refueled) {
        this.is_refueled = refueled;
    }
    
    public boolean isRefueled() {
        return is_refueled;
    }
}
