/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * deprecated: there are no longer any dependencies to this class
 * functions have been moved to plane class instead
 * 
 */
public class Runway {
    // these functions are already synchronized in their parent atc class
    
    public void land(Plane plane) throws InterruptedException {
        // plane landing process here
        System.out.println(Main.getCurrentTime() + " Plane " + plane.getID() + " is using the runway to land");
        Thread.sleep(2000); // simulate landing process
        System.out.println(Main.getCurrentTime() + " Plane " + plane.getID() + " has landed and is done using the runway");
    }
    
    public void takeoff(Plane plane) throws InterruptedException {
        // plane takeoff process here
        System.out.println(Main.getCurrentTime() + " Plane " + plane.getID() + " is using the runway to take off");
        Thread.sleep(2000); // simulate time to takeoff
        System.out.println(Main.getCurrentTime() + " Plane " + plane.getID() + " has taken off and is done using the runway");
    }
}
