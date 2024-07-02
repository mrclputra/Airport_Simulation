/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package refactor;

/**
 *
 * @author admin
 * This Gate class exists because I need to keep track of multiple available gates
 * do not put any sleep() or delaying functions here, as this runs on the main thread
 * Try to keep it as simplistic as possible
 * Object should not make any print statements
 */
public class Gate {
    private final int ID;
    private boolean occupied;
    
    // constructor
    public Gate(int ID) {
        this.ID = ID;
        this.occupied = false;
    }
    
    // sets occupied to true, updates message
    public void occupyGate(Plane plane) throws InterruptedException {
        occupied = true;
    }
    
    // sets occupied to false, updates message
    public void releaseGate(Plane plane) {
        occupied = false;
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public int getID() {
        return ID;
    }
}
