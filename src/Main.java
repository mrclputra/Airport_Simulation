/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 * main function program runs from here
 * 
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
        
        Runway runway = new Runway();
        
        List<Gate> gates = new ArrayList<>();
        gates.add(new Gate(1));
        gates.add(new Gate(2));
        gates.add(new Gate(3));
        
        ATC atc = new ATC(runway, gates);
        
        // generate planes
        Thread[] planes = new Thread[8];
        for(int i = 0; i < 8; i++) {
            Plane plane = new Plane(i + 1, atc, false);
            planes[i] = new Thread(plane);
        }
        
        for(Thread plane : planes) {
            plane.start();
        }
    }
}
