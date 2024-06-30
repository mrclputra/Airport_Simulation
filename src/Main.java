/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        gates.add(new Gate(3)); // emergency gate
        
        ATC atc = new ATC(runway, gates);
        
        // generate planes
        Plane[] planes = new Plane[8]; // Array to store Plane instances
        for (int i = 0; i < 7; i++) {
            Thread thread = new Thread();
            planes[i] = new Plane(i + 1, atc, false, thread); // Create normal planes
            thread = new Thread(planes[i]);
        }
        
        // emergency plane
        Thread emergencyThread = new Thread();
        planes[7] = new Plane(8, atc, true, emergencyThread); // Create emergency plane
        emergencyThread = new Thread(planes[7]);
        
        // Shuffle the planes array to randomize the order
        shuffleArray(planes);
        
        // Start planes with random delays
        Random random = new Random();
        for (Plane plane : planes) {
            int delay = random.nextInt(2000); // Random delay up to 2 seconds
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getCurrentTime() + " Plane " + plane.getID() + " has entered the airspace");
            Thread thread = new Thread(plane); // Create a new thread for the plane
            thread.start();
        }
    }
    
    // used to keep track of "simultaneous" processes and time
    public static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ss:SSS"); // seconds and milliseconds
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    
    // used to shuffle order of planes
    private static void shuffleArray(Object[] array) {
        Random random = new Random();
        for(int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Object temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
