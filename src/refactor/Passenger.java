/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package refactor;

/**
 *
 * @author admin
 * Simplistic passenger class with ID system to differentiate their threads
 * If i want to implement String names, it should be done inside the plane class and called in constructor
 * 
 */
public class Passenger {
    private int ID;
    
    public Passenger(int ID) {
        this.ID = ID;
    }
    
    public void board(Plane plane) throws InterruptedException {
        System.out.println(Simulation.getTime() + " Passenger " + ID + ": Boarding flight " + plane.getID());
    }
    
    public void disembark(Plane plane) throws InterruptedException {
        System.out.println(Simulation.getTime() + " Passenger " + ID + ": Disembarking Flight " + plane.getID());
    }
}
