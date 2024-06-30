



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * the single fuel truck in the entire airport
 * 
 */
public class FuelTruck {
    private boolean available = true; // initialize to readily available
    
    public void refuel(Plane plane) throws InterruptedException {
        synchronized (this) {
            while(!available) {
                wait(); // other plane is using the truck
            }
            available = false; // current thread is using the truck
        }
        
        System.out.println(Main.getCurrentTime() + " Fuel Truck is moving to plane " + plane.getID());
        Thread.sleep(2000); // simulate time to move between gates
        System.out.println(Main.getCurrentTime() + " Fuel Truck is refueling plane " + plane.getID());
        Thread.sleep(5000); // simulate refueling time
        System.out.println(Main.getCurrentTime() + " Fuel Truck has finished refueling plane " + plane.getID());
        
        synchronized (this) {
            available = true;
            notify(); // notify waiting threads that the truck is available to use
        }
    }
}
