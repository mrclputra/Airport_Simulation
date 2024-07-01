

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * Simplistic passenger class with ID system to differentiate thematic
 * If I want to implement String names, I should define a name library here
 * 
 */
public class Passenger {
    private int ID;
    
    public Passenger(int ID) {
        this.ID = ID;
    }
    
    public void board(Plane plane) throws InterruptedException {
        Thread.sleep(100); // boarding time
        System.out.println(Main.getTime() + " Passenger " + ID + ": Boarding flight " + plane.getID());
    }
    
    public void disembark(Plane plane) throws InterruptedException {
        Thread.sleep(100); // disembark time
        System.out.println(Main.getTime() + " Passenger " + ID + ": Disembarking Flight " + plane.getID());
    }
}
