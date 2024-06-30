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
public class FuelTruck implements Runnable {
    private final ATC atc;
    private boolean running; // flag that activates when gates are filled
    
    public FuelTruck(ATC atc) {
        this.atc = atc;
    }
    
    @Override
    public void run() {
        while(running) {
            
        }
    }
}
