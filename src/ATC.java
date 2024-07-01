/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.LinkedList;
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
    private final LinkedList<Plane> landing_queue;
    
    public ATC(List<Gate> gates) {
        this.gates = gates;
        this.available_gates = gates.size() - 1; // exclude the emergency gate
        this.landing_queue = new LinkedList<>();
        
        System.out.println(Main.getTime() + " ATC: Available Gates: " + available_gates); // debug
    }
    
    public void requestLanding(Plane plane) throws InterruptedException {
        // put all planes on initial hold order
        System.out.println(Main.getTime() + " ATC: Flight " + plane.getID() + ", hold position");
        
        synchronized (this) {
            if(plane.isEmergency()) {
                landing_queue.addFirst(plane); // emergency planes are added to the front
            } else {
                landing_queue.addLast(plane); // normal planes are added to the back
            }
            
            // check if its own turn in the queue
            while(!landing_queue.peek().equals(plane)) {
                wait(); // if not, wait for turn, recheck position in queue everytime it is notified
            }
            
            if(plane.isEmergency()) {
                System.out.println(Main.getTime() + " ATC: Flight " + plane.getID() + ", you are cleared to land on the Runway");
                plane.land();
            } else {
                while (available_gates == 0) {
                    // wait for an available gate
                    // in the case there are available gates, this will be bypassed
                    // current plane here is still the head of queue, will not conflict with previous wait
                    wait();
                }
                System.out.println(Main.getTime() + " ATC: Flight " + plane.getID() + ", you are cleared to land on the Runway");
                plane.land();
            }
            
            landing_queue.poll(); // remove from landing queue
            notifyAll();
        }
    }

    public synchronized void requestTakeoff(Plane plane) throws InterruptedException {
        System.out.println(Main.getTime() + " ATC: Flight " + plane.getID() + ", you are cleared to use the Runway for takeoff");
        plane.takeoff();

        if(plane.isEmergency()) {
            plane.setEmergency(false); // no notify here, as emergencies dont use normal gates
            return;
        }
        
        // notifies both waits
        notifyAll();
    }
    
    // as per detailed, there will always be a gate available to assign for landed planes
    // there is no need for additional queues or waits for the gate system, directly assign planes to a gate after landing
    // i kept this synchronized because of the Gate occupancy and available_gates variables being mutable and shared
    public synchronized Gate requestGate(Plane plane) throws InterruptedException {
        if (plane.isEmergency()) {
            System.out.println(Main.getTime() + " ATC: Flight " + plane.getID() +
                    ", please proceed to Gate 3 for docking");
            return gates.get(2); // assign emergency plane to gate 3
        }

        for (Gate gate : gates) {
            if (!gate.isOccupied()) {
                System.out.println(Main.getTime() + " ATC: Flight " + plane.getID() +
                        ", please proceed to Gate " + gate.getID() + " for docking");
                gate.occupyGate(plane);
                available_gates--;
                System.out.println(Main.getTime() + " New Available Gate Count: " + available_gates);
                return gate;
            }
        }
        throw new IllegalStateException("Error: No gates available"); // in theory, this should never happen :3
    }
    
    // as I do not store the plane object in the assigned Gate's class, I need to pass it in manually again
    // same argument as in 'assignGate'
    public synchronized void releaseGate(Gate gate, Plane plane) {
        gate.releaseGate(plane);
        if (plane.isEmergency()) {
            return;
        }

        // update regular planes gate count
        available_gates++;
        System.out.println(Main.getTime() + " New Available Gate Count: " + available_gates); // debug, this may be commented
    }
    
    // sanity checks here
    public void sanityCheck(Plane[] planes) {
        System.out.print("\n");
        System.out.println(Main.getTime() + " ATC: Beginning Sanity Checks");
        // check that all gates are empty
        for(Gate gate : gates) {
            String status = gate.isOccupied() ? "occupied" : "empty";
            System.out.println(Main.getTime() + " ATC: Gate " + gate.getID() + " status " + status);
        }
        
        // max, average, min waiting times
    }
}
