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
 * plane action requests and ATC function calls are done here
 * 
 */
public class Plane implements Runnable {
    
    private final int ID;
    private final ATC atc;
    private boolean is_emergency;
    
    private final int max_passengers = 20;
    private Passenger[] passengers;
    private final ExecutorService disembark_exs;
    private final ExecutorService board_exs;
    
    private Thread resupply_thread;
    
    // reference to own thread, to allow external control (i.e. prioritization)
    private final Thread thread; 
    
    // constructor
    public Plane(int ID, ATC atc, boolean is_emergency, Thread thread) {
        this.ID = ID;
        this.atc = atc;
        this.is_emergency = is_emergency;
        this.thread = thread;
        
        // generate current passengers on entering airspace
        this.passengers = generatePassengers();
        
        disembark_exs = Executors.newSingleThreadExecutor(); // single thread executor for sequential execution
        board_exs = Executors.newSingleThreadExecutor();
        
        resupply_thread = new Thread(() -> {
            try {
                resupplyPlane();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
 
        });
    }
    
    // implement all landing, docking, and takeoff, request processes here
    @Override
    public void run() {
        try {
            if(is_emergency) {
                System.out.println(Main.getCurrentTime() + " Plane " + ID + " has a fuel emergency");
            }
            atc.requestLanding(this);   // request landing from atc

            Gate gate = atc.assignGate(this); // get assigned a gate from atc
            
            resupply_thread.start();
            
            System.out.println(Main.getCurrentTime() + " Plane " + ID + " is disembarking passengers");
            disembarkPassengers(); // disembark passengers
            Thread.sleep(500); // time between disembark and boarding processes
            boardPassengers(generatePassengers()); // board new passengers

            resupply_thread.join(); // explicitly wait for resupply to finish

            atc.releaseGate(gate, this); // undock from gate
            atc.requestTakeoff(this);   // request takeoff from atc
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
    
    private void resupplyPlane() throws InterruptedException {
        System.out.println(Main.getCurrentTime() + " Plane " + ID + " is being resupplied");
        Thread.sleep(4000);
        System.out.println(Main.getCurrentTime() + " Plane " + ID + " has finished resupplying");
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
        return thread;
    }
}
