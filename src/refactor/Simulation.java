/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package refactor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author admin
 * Program is Initiated from this class
 * Airport setup and world events (plane spawns) are triggered here
 * 
 */
public class Simulation {
    private static LocalDateTime start_time; // to store current time on program start to keep track of duration
    private static final int PLANE_COUNT = 6;
     
    public static void main(String[] args) {
        System.out.println("Simulation Initialized");
        start_time = LocalDateTime.now(); // get starting time (now)
        
        // create gates
        List<Gate> gates = new ArrayList<>();
        gates.add(new Gate(1));
        gates.add(new Gate(2));
        gates.add(new Gate(3)); // reserve gate
        
        // create atc and fuel truck objects
        ATC atc = new ATC(gates);
        FuelTruck truck = new FuelTruck();
        
        // generate planes
        Plane[] planes = new Plane[PLANE_COUNT];
        Thread[] plane_threads = new Thread[PLANE_COUNT];
        
        for(int i = 0; i < planes.length; i++) {
            planes[i] = new Plane(i + 1, atc, truck, false);
            plane_threads[i] = new Thread(planes[i]);
        }
        
        // setup an emergency plane
        planes[planes.length - 1].setEmergency(true);
        
        // shuffle
        shuffleArray(plane_threads);
        
        Random rdm = new Random();
        for(Thread thread : plane_threads) {
            try {
                    int delay = rdm.nextInt(2000) + 1000; // 1-3 second delay
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            
            thread.start();
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
        String time_formatted = String.format("%02d:%03d", seconds, millis);
        return time_formatted;

        //String color_code = getColorByTime(duration.toMillis());
        //return color_code + time_formatted;
    }
    
    public static String getColorByTime(long elapsed_time) {
        int color_code = 16 + (int) ((elapsed_time / 10) % 240); // 256-color mode, update every 10ms
        return "\u001B[38;5;" + color_code + "m";
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
