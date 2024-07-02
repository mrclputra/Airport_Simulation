/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package refactor;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author admin
 * - centralize all plane-related operations and actions here
 * - manages takeoff, landing, and gate procedures
 * - should i want to implement multiple planes with emergencies, I need a new threading system, and add the ability to assign
 *      emergency planes to normal gates as well
 * 
 */
public class ATC {
    private int n_grounded; // only tracks first 2 gates
    private final List<Gate> gates;
    private final LinkedList<Plane> landing_queue;
    private final Object rwy_lock; // simulates a lock on the runway, so only one plane can use at any time
    
    // constructor
    public ATC(List<Gate> gates) {
        this.gates = gates;
        this.n_grounded = 0;
        this.landing_queue = new LinkedList<>();
        this.rwy_lock = new Object();
        
        // debug
        System.out.println(Simulation.getTime() + " ATC: Available Gates: " + (gates.size() - 1));
    }
    
    public void requestLanding(Plane plane) throws InterruptedException {
        // put all planes on initial hold order
        System.out.println(Simulation.getTime() + " ATC: Flight " + plane.getID() + ", hold position");
        
        // add plane to the queue
        // prioritize plane with an emergency
        if (plane.isEmergency()) {
            landing_queue.addFirst(plane);
        } else {
            landing_queue.addLast(plane);
        }
        
        synchronized (this) {
            // have the plane wait for its turn in the queue and check gate count
            // this fucking logic
            while (n_grounded >= gates.size() - 1 || !landing_queue.peek().equals(plane)) {
                if(plane.isEmergency()) {
                    break;
                }
                wait();
            }

            // code below should only runs for plane at the front of queue
            synchronized (rwy_lock) {
                System.out.println(Simulation.getTime() + " ATC: Flight " + plane.getID() + ", you are cleared to land on the Runway");
                plane.land();
                
                if(!plane.isEmergency()) {
                    n_grounded++;
                    System.out.println(Simulation.getTime() + " Flights Grounded: " + n_grounded);
                }
            }

            landing_queue.poll();
            notifyAll();
            }
    }
    
    public synchronized void requestTakeoff(Plane plane) throws InterruptedException {
        synchronized (rwy_lock) {
            
            System.out.println(Simulation.getTime() + " ATC: Flight " + plane.getID() + ", you are cleared to use the Runway for takeoff");
            plane.takeoff();
        }
        
        if(!plane.isEmergency()) {
                n_grounded--;
                System.out.println(Simulation.getTime() + " Flights Grounded: " + n_grounded);
            }
        plane.setEmergency(false);
        
        notifyAll(); // notify planes in sky that a gate is now available
    }
    
    public synchronized Gate requestGate(Plane plane) throws InterruptedException {
        // check if emergency plane
        if(plane.isEmergency()) {
            System.out.println(Simulation.getTime() + " ATC: Flight " + plane.getID() +
                    ", please proceed to Gate 3 for docking");
            return gates.get(2); // give gate 3
        }
        
        // search for an available normal gate
        for (Gate gate : gates) {
            if (!gate.isOccupied()) {
                System.out.println(Simulation.getTime() + " ATC: Flight " + plane.getID() +
                        ", please proceed to Gate " + gate.getID() + " for docking");
                gate.occupyGate(plane);
                return gate;
            }
        }
        throw new IllegalStateException("Error: No gates available"); // in theory, this should never happen :3
    }
    
    // as I do not store the plane object in the assigned Gate's class, I need to pass it in manually again
    // same argument as in 'assignGate'
    public synchronized void releaseGate(Gate gate, Plane plane) {
        gate.releaseGate(plane);
    }
    
    // sanity checks here
    public void sanityCheck(Plane[] planes) {
        System.out.print("\n");
        System.out.println(Simulation.getTime() + " ATC: Beginning Sanity Checks");
        // check that all gates are empty
        for(Gate gate : gates) {
            String status = gate.isOccupied() ? "occupied" : "empty";
            System.out.println(Simulation.getTime() + " ATC: Gate " + gate.getID() + " status " + status);
        }
    }
}
