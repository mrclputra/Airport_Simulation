/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author admin
 * main function program runs from here
 * if you want to introduce processing delays, try to stay inside synchronized functions
 * 
 */
public class Main {
    private static LocalDateTime start_time; // start time to keep track of program duration
    
    public static void main(String[] args) {
        System.out.println("Simulation Initialized");
        start_time = LocalDateTime.now(); // get starting time (current time)

        // create gates
        List<Gate> gates = new ArrayList<>();
        gates.add(new Gate(1));
        gates.add(new Gate(2));
        gates.add(new Gate(3)); // emergency gate

        // create atc and fuel truck objects
        ATC atc = new ATC(gates);
        FuelTruck truck = new FuelTruck();

        // Generate planes
        Thread[] planes = new Thread[6];

        // Create and start each plane thread with random delays
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            boolean isEmergency = (i == 5); // Assuming the 6th plane (index 5) is emergency
            Plane plane = new Plane(i + 1, atc, truck, isEmergency);
            planes[i] = new Thread(plane);
            
            // Generate random delay between 0 to 2 seconds (0 to 2000 milliseconds)
            int delay = random.nextInt(2000);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Output message indicating plane has entered airspace and start its thread
            System.out.println(getCurrentTime() + " World: Flight " + plane.getID() + " has entered the airspace");
            planes[i].start();
        }
        
        // Wait for all planes to finish
        for (Thread planeThread : planes) {
            try {
                planeThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        
        // Simulation completed
        System.out.println(getCurrentTime() + " Simulation completed successfully.");
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
    
    // used for color coding text, to be implemented
    public static String getColorCode(int id) {
        int color_code = 31 + (id % 6); // 31 to 36 are standard color codes for rgbmyc
        return "\u001B[" + color_code + "m";
    }
    
    private static String resetColor() {
        return "\u001B[0m";
    }
}
