

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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
    
    // reference to own thread, to allow external control (i.e. prioritization)
    private final Thread thread; 
    
    // to be implemented, arrays for passengers
    //private Passenger[] psg;
    
    // constructor
    public Plane(int ID, ATC atc, boolean is_emergency, Thread thread) {
        this.ID = ID;
        this.atc = atc;
        this.is_emergency = is_emergency;
        this.thread = thread;
    }
    
    @Override
    public void run() {
        // implement landing docking and takeoff reques processes here
        try {
            if(is_emergency) {
                System.out.println(Main.getCurrentTime() + " Plane " + ID + " has a fuel emergency");
            }
            atc.requestLanding(this);   // request landing from atc

            Gate gate = atc.assignGate(this); // get assigned a gate from atc
            System.out.println(Main.getCurrentTime() + " Plane " + ID + " is doing shit on the ground");
            Thread.sleep(5000); // simulate time on ground, do not put this in gate class

            atc.releaseGate(gate, this); // undock from gate
            atc.requestTakeoff(this);   // request takeoff from atc
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
    
    public int getID() {
        return ID;
    }
    
    public void setEmergency(boolean state) {
        this.is_emergency = state;
    }
    
    public boolean isEmergency() {
        return is_emergency;
    }
    
    public Thread getThread() {
        return thread;
    }
}
