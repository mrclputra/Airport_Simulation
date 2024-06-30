/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.time.Duration;
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
    private static LocalDateTime start_time; // start time to keep track of program duration
    
    public static void main(String[] args) {
        System.out.println("Hello World");
        start_time = LocalDateTime.now(); // initialize the start time
        
        Runway runway = new Runway();
        
        List<Gate> gates = new ArrayList<>();
        gates.add(new Gate(1));
        gates.add(new Gate(2));
        gates.add(new Gate(3)); // emergency gate
        
        ATC atc = new ATC(runway, gates);
        
        // generate planes
        Plane[] planes = new Plane[6]; // Array to store Plane instances
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread();
            planes[i] = new Plane(i + 1, atc, false, thread); // Create normal planes
            thread = new Thread(planes[i]);
        }
        
        // emergency plane
        Thread emergencyThread = new Thread();
        planes[5] = new Plane(6, atc, true, emergencyThread); // Create emergency plane
        emergencyThread = new Thread(planes[5]);
        
        // shuffle the planes array to randomize the order
        shuffleArray(planes);
        
        
        // start planes with random delays of 0-2 seconds
        Random random = new Random();
        for (Plane plane : planes) {
            int delay = random.nextInt(2000); // delay generation here
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread thread = new Thread(plane); // create a new thread for the plane
            System.out.println(getCurrentTime() + " Plane " + plane.getID() + "(" + plane.getPassengerCount() + ")" + " has entered the airspace");
            thread.start();
        }
    }
    
    // used to keep track of "simultaneous" processes and time
    public static String getCurrentTime() {
        Duration duration = Duration.between(start_time, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long millis = duration.toMillis() % 1000;
        return String.format("%02d:%03d", seconds, millis);
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
