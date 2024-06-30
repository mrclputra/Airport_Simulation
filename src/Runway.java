/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * simple runway object to handle plane landing thread
 * no identifier needed as there is only one runway
 * 
 */
public class Runway {
    public synchronized void land(Plane plane) throws InterruptedException {
        // plane landing process here
        System.out.println("Plane " + plane.getID() + " is using the runway to land");
        Thread.sleep(2000); // simulate landing process
        System.out.println("Plane " + plane.getID() + " has landed and is done using the runway");
    }
    
    public synchronized void takeoff(Plane plane) throws InterruptedException {
        // plane takeoff process here
        System.out.println("Plane " + plane.getID() + " is using the runway to take off");
        Thread.sleep(2000);
        System.out.println("Plane " + plane.getID() + " has taken off and is done using the runway");
    }
}
