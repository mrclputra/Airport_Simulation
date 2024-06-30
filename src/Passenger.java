

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author admin
 * to be implemented
 * 
 */
public class Passenger {
    private int ID;
    
    public Passenger(int ID) {
        this.ID = ID;
    }
    
    public void board(Plane plane) throws InterruptedException {
        Thread.sleep(500); // boarding time
        System.out.println(Main.getCurrentTime() + " Passenger " + ID + " has boarded plane " + plane.getID());
    }
    
    public void disembark(Plane plane) throws InterruptedException {
        Thread.sleep(500); // disembark time
        System.out.println(Main.getCurrentTime() + " Passenger " + ID + " has disembarked from plane " + plane.getID());
    }
}
