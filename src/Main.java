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
    private static int PLANE_COUNT = 6;
    
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
        
        // generate planes
        Plane[] planes = new Plane[PLANE_COUNT];
        Thread[] plane_threads = new Thread[PLANE_COUNT];
        
        for(int i = 0; i < planes.length; i++) {
            planes[i] = new Plane(i + 1, atc, truck, false);
        }
        
        // set an emergency plane
        planes[planes.length - 1].setEmergency(true);
        
        // random object
        // uses 'i' to iterate through threads list
        Random rdm = new Random();
        for(int i = 0; i < planes.length; i++) {
            Plane plane = planes[i];
            plane_threads[i] = new Thread(plane);
            
            try {
                    int delay = rdm.nextInt(2000) + 1; // 1-3 second delay
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            
            System.out.println(getTime() + " World: Flight: " + plane.getID() + " has entered the airspace");
            plane_threads[i].start();
        }
        
        // ensure all threads complete execution
        for(Thread thread : plane_threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // run sanity checks
        atc.sanityCheck(planes);
        
    }
    
    // used to keep track of "simultaneous" processes and time
    public static String getTime() {
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
        int color_code = 30 + (id % PLANE_COUNT);
        return "\u001B[" + color_code + "m";
    }
    
    private static String resetColor() {
        return "\u001B[0m";
    }
}
