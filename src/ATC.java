/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;

/**
 *
 * @author admin
 * centralize all plane-related operations and actions here; 
 * should i want to implement multiple planes with emergencies, I need a new threading system
 * and be able to assign them to normal gates as well
 * manages takeoff, landing, and gates
 * 
 */
public class ATC {
    private int available_gates; // this variable only keeps track of regular planes in the first 2 gates
    private final List<Gate> gates;
    
    public ATC(Runway runway, List<Gate> gates) {
        this.gates = gates;
        this.available_gates = gates.size() - 1; // exclude the emergency gate
        System.out.println(Main.getCurrentTime() + " ATC: Available Gates: " + available_gates); // debug
    }
    
    public void requestLanding(Plane plane) throws InterruptedException {
        // all planes on hold
        System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", hold position");
        
        synchronized (this) {
            // check if emergency flight
            if(plane.isEmergency()) {
                // set emergency thread prioritya and land immediately
                plane.getThread().setPriority(Thread.MAX_PRIORITY); // backup
                System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", you are cleared to land on the Runway");
                plane.land();
                return;
            }
            
            // regular planes wait for available gates
            while(available_gates <= 0) {
                wait(); // wait until a normal gate is available on the ground
            }
            System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", you are cleared to land on the Runway");
            plane.land();
        }
        
    }
    
    public synchronized void requestTakeoff(Plane plane) throws InterruptedException {
        System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", you are cleared to use the Runway for takeoff");
        plane.takeoff();

        if(plane.isEmergency()) {
            plane.setEmergency(false); // after takeoff, reset emergency plane value to false
            return;
        }

        // this is not required for emergency planes, in single case
        // notify a waiting plane in the sky that a regular gate is now available
        notifyAll();
    }
    
    // as per assginment detailed, there will always be a gate available to assign to landed planes
    // no need for additional wait or notifies for the gate system
    // i kept this synchronized because of the Gate occupancy and available_gates variables are mutable and shared
    public synchronized Gate assignGate(Plane plane) throws InterruptedException {
        // for planes with an emergency
        if(plane.isEmergency()) {
            // reset priority to normal as the plane has landed
            // this should reset priority in the case of takeoff and fuel truck lines
            System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + 
                    ", please proceed to Gate 3 for docking");
            plane.getThread().setPriority(Thread.NORM_PRIORITY); 
            return gates.get(2); // assign plane to emergency gate
        }
        
        // for regular planes
        for(Gate gate : gates) {
            if(!gate.isOccupied()) {
                System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + 
                        ", please proceed to Gate " + gate.getID() + " for docking");
                gate.occupyGate(plane);
                available_gates--;
                return gate;
            }
        }
        throw new IllegalStateException("Error: No gates available"); // in theory this should never happen :3
        //return null;
    } 
    
    // as I do not store the plane object in the assigned Gate's class, I need to pass it in manually again
    // same argument as in 'assignGate'
    public synchronized void releaseGate(Gate gate, Plane plane) {
        gate.releaseGate(plane);
        if (plane.isEmergency()) {
            return;
        }

        // for regular planes
        available_gates++;
    }
}
