/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;

/**
 *
 * @author admin
 * centralize all plane-related operations and actions here
 * does not support more than one emergency at a time, that would require a new thread system
 * 
 */
public class ATC {
    private int available_gates; // this variable only keeps track of regular planes in the first 2 gates
    private final Runway runway;
    private final List<Gate> gates;
    
    public ATC(Runway runway, List<Gate> gates) {
        this.runway = runway;
        this.gates = gates;
        this.available_gates = gates.size() - 1; // exclude the emergency gate
        System.out.println("Available Gates " + available_gates);
    }
    
    public synchronized void requestLanding(Plane plane) throws InterruptedException {
        // check if emergency, bypass to assign directly to gate 3
        if(plane.isEmergency()) {
            System.out.println("Plane " + plane.getID() + " is preparing to land");
            runway.land(plane);
            System.out.println("Plane " + plane.getID() + " is coasting to emergency gate");
            return;
        }
        
        // regular planes
        while(available_gates <= 0) {
            System.out.println("Plane " + plane.getID() + " is waiting to land");
            wait(); // wait until a normal gate is available on the ground
        }
        System.out.println("Plane " + plane.getID() + " is preparing to land");
        runway.land(plane);
        available_gates--;
        System.out.println("Plane " + plane.getID() + " is coasting to a gate");
    }
    
    public synchronized void requestTakeoff(Plane plane) throws InterruptedException {
        System.out.println("Plane " + plane.getID() + " is preparing to takeoff");
        runway.takeoff(plane);
        
        if(plane.isEmergency()) {
            plane.setEmergency(false); // after takeoff, reset emergency plane value to false
            return;
        }
        
        // this is not required for emergency planes
        // notify waiting planes in the sky that a regular gate is now available
        notifyAll();
    }
    
    // as per assginment detailed, there will always be a gate available to assign to landed planes
    // no need for additional wait or notifies for the gate system
    // i kept this synchronized because of the Gate occupancy and available_gates variables are mutable and shared
    public synchronized Gate assignGate(Plane plane) throws InterruptedException {
        // assign to emergency gate 3
        if(plane.isEmergency()) {
            return gates.get(2);
        }
        
        // for regular planes
        for(Gate gate : gates) {
            if(!gate.isOccupied()) {
                gate.occupyGate(plane);
                System.out.println("Available Gates " + available_gates);
                return gate;
            }
        }
        throw new IllegalStateException("No gates available"); // in theory this will never happen :3
        //return null;
    } 
    
    // as I do not store the plane object in the assigned Gate's class, I need to pass it in manually again
    // same argument as in 'assignGate'
    public synchronized void releaseGate(Gate gate, Plane plane) {
        gate.releaseGate(plane);
        if (plane.isEmergency()) {
            return;
        }

        // For regular planes
        available_gates++;
        System.out.println("Available Gates " + available_gates); // debug
}
}
