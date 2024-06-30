/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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
    private final PriorityQueue<Plane> landing_queue;
    
    public ATC(List<Gate> gates) {
        this.gates = gates;
        this.available_gates = gates.size() - 1; // exclude the emergency gate
        this.landing_queue = new PriorityQueue<>(new PlanePriorityComparator());
        System.out.println(Main.getCurrentTime() + " ATC: Available Gates: " + available_gates); // debug
    }
    
    public void requestLanding(Plane plane) throws InterruptedException {
        // all planes on hold initially
        System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", hold position");
        
        synchronized (this) {
            landing_queue.offer(plane);
            while (!landing_queue.peek().equals(plane)) {
                wait(); // wait until it's this plane's turn in the queue, applies to all
            }
            
            // handle emergency flights
            if(plane.isEmergency()) {
                System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", you are cleared to land on the Runway");
                plane.land();
            } else {
                // handle normal flights
                while (available_gates == 0) {
                    wait(); // wait until a gate is available for regular landing
                }
                System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", you are cleared to land on the Runway");
                plane.land();
            }
            
            landing_queue.poll(); // remove plane from landing queue
            notifyAll(); // notifies queue that the list has been updated, moving it up
            // what the fuck
        }
        
    }
    
    public synchronized void requestTakeoff(Plane plane) throws InterruptedException {
        System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() + ", you are cleared to use the Runway for takeoff");
        plane.takeoff();

        if(plane.isEmergency()) {
            plane.setEmergency(false); // no notify here, as emergencies dont use normal gates
            return;
        }
        
        // notifies queue that there is now an empty gate
        notifyAll();
    }
    
    // as per assginment detailed, there will always be a gate available to assign to landed planes
    // no need for additional wait or notifies for the gate system
    // i kept this synchronized because of the Gate occupancy and available_gates variables are mutable and shared
    public synchronized Gate assignGate(Plane plane) throws InterruptedException {
        if (plane.isEmergency()) {
            System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() +
                    ", please proceed to Gate 3 for docking");
            return gates.get(2); // Assign emergency plane to the emergency gate
        }

        for (Gate gate : gates) {
            if (!gate.isOccupied()) {
                System.out.println(Main.getCurrentTime() + " ATC: Flight " + plane.getID() +
                        ", please proceed to Gate " + gate.getID() + " for docking");
                gate.occupyGate(plane);
                available_gates--;
                System.out.println(Main.getCurrentTime() + " Available Gates: " + available_gates);
                return gate;
            }
        }
        throw new IllegalStateException("Error: No gates available");
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
        System.out.println(Main.getCurrentTime() + " Available Gates: " + available_gates);
    }
    
    // always ensures emergency planes are placed first
    private static class PlanePriorityComparator implements Comparator<Plane> {
        @Override
        public int compare(Plane p1, Plane p2) {
            // prioritizes emergency planes
            if (p1.isEmergency() && !p2.isEmergency()) {
                return -1;
            } else if (!p1.isEmergency() && p2.isEmergency()) {
                return 1;
            } else {
                return 0; // equal priotity or both emergency
            }
        }
    }
}
