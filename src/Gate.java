/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * This Gate class exists because I need to keep track of multiple available gates
 * do not put any sleep() or delaying functions here, as this runs on the main thread
 * if we want to implement parking slots, manage it here

 * 
 */
public class Gate {
    private int ID;
    private boolean occupied;
    
    // constructor
    public Gate(int ID) {
        this.ID = ID;
        this.occupied = false;
    }
    
    // sets occupied to true, updates message
    public void occupyGate(Plane plane) throws InterruptedException {
        occupied = true;
        System.out.println(Main.getCurrentTime() + " Plane " + plane.getID() + " is using gate " + ID);
    }
    
    // sets occupied to false, updates message
    public void releaseGate(Plane plane) {
        occupied = false;
        System.out.println(Main.getCurrentTime() + " Plane " + plane.getID() + " has left gate " + ID);
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public int getID() {
        return ID;
    }
}
