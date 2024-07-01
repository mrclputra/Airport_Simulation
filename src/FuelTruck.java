/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * the single fuel truck in the entire airport
 * this runs on its own thread defined in the plane class
 * 
 */
public class FuelTruck {
    private boolean available = true; // initialize to readily available
    
    public synchronized void refuel(Plane plane) throws InterruptedException {
        while(!available) {
            wait(); // other plane is using the truck
        }
        available = false; // current thread is using the truck

        System.out.println(Main.getTime() + " Fuel Truck: Moving to Flight " + plane.getID());
        Thread.sleep(2000); // simulate time to move between planes
        System.out.println(Main.getTime() + " Fuel Truck: Refueling Flight " + plane.getID());
        Thread.sleep(5000); // simulate refueling time
        System.out.println(Main.getTime() + " Fuel Truck: Finished refueling Flight " + plane.getID());

        available = true;
        notify();
    }
}
