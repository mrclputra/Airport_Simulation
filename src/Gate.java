/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * This Gate class is used only for keeping track of available Gates
 * do not put sleep() functions here, as it runs on main thread
 * if we want to use a "wait for empty gate" and parking spots, implement wait and notify functions here
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
        System.out.println("Plane " + plane.getID() + " is using gate " + ID);
    }
    
    // sets occupied to false, updates message
    public void releaseGate(Plane plane) {
        occupied = false;
        System.out.println("Plane " + plane.getID() + " has left gate " + ID);
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public int getID() {
        return ID;
    }
}
