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
    
    private final int max_passengers = 20;
    private Passenger[] passengers;
    private final ExecutorService disembark_exs;
    private final ExecutorService board_exs;
    
    private final Thread resupply_thread;
    private final Thread refuel_thread;
    private final Thread cleaning_thread;
    
    // reference to own thread, to allow external control (i.e. thread prioritization)
    private final Thread plane_thread; 
    
    // constructor
    public Plane(int ID, ATC atc, boolean is_emergency, Thread plane_thread) {
        this.ID = ID;
        this.atc = atc;
        this.is_emergency = is_emergency;
        this.is_refueled = false;
        this.plane_thread = plane_thread;
        
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
                    atc.requestRefuel(this); // request refuel truck
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
            if(is_emergency) {
                // declare emergency
                System.out.println(Main.getCurrentTime() + " Plane " + ID + " has a fuel emergency");
            }
            atc.requestLanding(this);   // request landing from atc

            Gate gate = atc.assignGate(this); // get assigned a gate from atc
            
            resupply_thread.start(); // start service plane threads
            cleaning_thread.start();
            refuel_thread.start();
            
            // uncomment below to include passenger simulation
//            System.out.println(Main.getCurrentTime() + " Plane " + ID + " is disembarking passengers");
//            disembarkPassengers(); // disembark passengers
//            Thread.sleep(500); // time between disembark and boarding processes
//            boardPassengers(generatePassengers()); // board new passengers

            // here I explicitly wait for all processes to finish before proceeding
            // through java 21 should handle it implicitly on its own
            refuel_thread.join();
            resupply_thread.join();
            cleaning_thread.join();

            atc.releaseGate(gate, this); // undock from gate
            atc.requestTakeoff(this);   // request takeoff from atc
            
            Thread.sleep(4000); // simulate time to leave airspace
            System.out.println(Main.getCurrentTime() + " Plane " + ID + " has left the airspace");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
    
    private void resupplyPlane() throws InterruptedException {
        System.out.println(Main.getCurrentTime() + " Plane " + ID + " is being resupplied");
        Thread.sleep(4000); // duration of plane resupplying
        System.out.println(Main.getCurrentTime() + " Plane " + ID + " has finished resupplying");
    }
    
    private void cleanPlane() throws InterruptedException {
        System.out.println(Main.getCurrentTime() + " Plane " + ID + " is being cleaned");
        Thread.sleep(3000); // duration of plane cleaning
        System.out.println(Main.getCurrentTime() + " Plane " + ID + " has finished cleaning");
    }
    
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
            Thread.sleep(500); // time between each passenger disembarking
        }
        
        board_exs.shutdown();
        board_exs.awaitTermination(1, TimeUnit.MINUTES);
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
            Thread.sleep(500);
        }
        disembark_exs.shutdown();
        disembark_exs.awaitTermination(1, TimeUnit.MINUTES);
    }
    
    // simulate random passenger generation. Used in initialization and boarding
    private Passenger[] generatePassengers() {
        Random rdm = new Random();
        int count = rdm.nextInt(max_passengers + 1);
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
    
    // for external thread control reference
    public Thread getThread() {
        return plane_thread;
    }
    
    public void setRefueled(boolean refueled) {
        this.is_refueled = refueled;
    }
    
    public boolean isRefueled() {
        return is_refueled;
    }
}
