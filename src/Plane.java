

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
    
    private int ID;
    private ATC atc;
    private boolean is_emergency;
    
    // to be implemented, arrays for passengers
    //private Passenger[] psg;
    
    // constructor
    public Plane(int ID, ATC atc, boolean is_emergency) {
        this.ID = ID;
        this.atc = atc;
        this.is_emergency = is_emergency;
    }
    
    @Override
    public void run() {
        // implement landing docking and takeoff reques processes here
        try {
            atc.requestLanding(this);   // request landing from atc

            Gate gate = atc.assignGate(this); // get assigned a gate from atc
            if(is_emergency) {
                System.out.println("Plane " + ID + " undergoing repairs");
            }
            System.out.println("Plane " + ID + " is doing shit on the ground");
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
}
