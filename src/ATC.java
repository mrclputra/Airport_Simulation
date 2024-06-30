/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;

/**
 *
 * @author admin
 * centralize all plane-related operations and actions here
 * (not requests)
 * 
 */
public class ATC {
    private int available_gates;
    private Runway runway;
    private List<Gate> gates;
    
    public ATC(Runway runway, List<Gate> gates) {
        this.runway = runway;
        this.gates = gates;
        this.available_gates = gates.size();
    }
    
    public synchronized void requestLanding(Plane plane) throws InterruptedException {
        while(available_gates <= 0) {
            System.out.println("Plane " + plane.getID() + " is waiting to land");
            wait(); // wait until a gate is available on the ground
        }
        System.out.println("Plane " + plane.getID() + " is preparing to land");
        runway.land(plane);
        available_gates--;
        System.out.println("Plane " + plane.getID() + " is coasting to a gate");
    }
    
    public synchronized void requestTakeoff(Plane plane) throws InterruptedException {
        System.out.println("Plane " + plane.getID() + " is preparing to takeoff");
        runway.takeoff(plane);
        notifyAll(); // notify waiting planes that a gate is now available
    }
    
    // as per assginment detailed, there will always be a gate available to assign to landed planes
    // no need for additional wait or notifies for the gate system
    public synchronized Gate assignGate(Plane plane) throws InterruptedException {
        for(Gate gate : gates) {
            if(!gate.isOccupied()) {
                gate.occupyGate(plane);
                return gate;
            }
        }
        throw new IllegalStateException("No gates available"); // in theory this will never happen :3
        //return null;
    } 
    
    // as I do not store the plane object in the assigned Gate's class, I need to pass it in manually again
    public synchronized void releaseGate(Gate gate, Plane plane) {
        gate.releaseGate(plane);
        available_gates++;
        System.out.println("Available Gates " + available_gates); // debug
    }
}
