package org.example;

import java.awt.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;

public class Main {
    private static String getTime()
    {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss a");
        String formattedTime = now.format(formatter);
        return formattedTime;

    }

    public static void main(String[] args) throws InterruptedException, AWTException, FileNotFoundException {
        long startTime=System.currentTimeMillis();
        PrintWriter logWriter=new PrintWriter(new File("mmover.log"));
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("mmoverConfig.ini"));
        } catch (IOException e) {
            // If config.ini does not exist, create one with default values
            prop.setProperty("waiting_time", "20000");
            prop.setProperty("MAX_X", "200");
            prop.setProperty("MAX_Y", "200");
            prop.setProperty("MAX_INTERFERENCE_ALLOWED", "3");
            try {
                prop.store(new FileOutputStream("mmoverConfig.ini"), null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        int waiting_time = Integer.parseInt(prop.getProperty("waiting_time"));
        int MAX_X = Integer.parseInt(prop.getProperty("MAX_X"));
        int MAX_Y = Integer.parseInt(prop.getProperty("MAX_Y"));
        int MAX_INTERFERENCE_ALLOWED = Integer.parseInt(prop.getProperty("MAX_INTERFERENCE_ALLOWED"));

        Robot robot=new Robot();
        Random random=new Random();
        int interference_count=0;
        String formattedTime=getTime();
        System.out.println("Started  on: " + formattedTime+"\nwill move every "+waiting_time/1000+" seconds ");

        System.out.println("config values: waiting_time: " + waiting_time + " MAX_X: " + MAX_X + " MAX_Y: " + MAX_Y + " MAX_INTERFERENCE_ALLOWED: " + MAX_INTERFERENCE_ALLOWED);

        logWriter.println("Started  on: " + formattedTime+"\nwill move every "+waiting_time/1000+" seconds ");

        logWriter.println("config values: waiting_time: " + waiting_time + " MAX_X: " + MAX_X + " MAX_Y: " + MAX_Y + " MAX_INTERFERENCE_ALLOWED: " + MAX_INTERFERENCE_ALLOWED);


        Point prev_location=MouseInfo.getPointerInfo().getLocation();
        long i=1;
        while (true)
        {
            Point currentMouseLocation = MouseInfo.getPointerInfo().getLocation();

//            check whether prev location is same ,if not user has interfered
            if (prev_location.x!=currentMouseLocation.x||prev_location.y!=currentMouseLocation.y)
            {
                interference_count++;
                System.out.println("Warning !! User interfered "+interference_count +" max:"+MAX_INTERFERENCE_ALLOWED);
               logWriter.println("Warning !! User interfered "+interference_count +" max:"+MAX_INTERFERENCE_ALLOWED);

                if (interference_count>=MAX_INTERFERENCE_ALLOWED)
                {

                  logWriter.println("User has interfered " + MAX_INTERFERENCE_ALLOWED + " times exiting ");

                    System.out.println("User has interfered " + MAX_INTERFERENCE_ALLOWED + " times exiting ");
                    Thread.sleep(3000);
                   logWriter.println("Closing the program @ "+getTime());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss a");

                    long endTime = System.currentTimeMillis();
                    long totalTimeInMillis = endTime - startTime;
                    Duration duration = Duration.ofMillis(totalTimeInMillis);
                    long hours = duration.toHours();
                    long minutes = duration.toMinutes() % 60;
                    long seconds = duration.getSeconds() % 60;
                    logWriter.println("Total time program ran for: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));

                    logWriter.close();

                    System.exit(-1);
                }

            }
            int move_x=random.nextInt(MAX_X);
            int move_y=random.nextInt(MAX_Y);
            robot.mouseMove(move_x,move_y);
            logWriter.println("moved  "+i++ +" times, current  location: (" + currentMouseLocation.x + "," + currentMouseLocation.y + ")  "+getTime());

            System.out.println("moved  "+i++ +" times, current  location: (" + currentMouseLocation.x + "," + currentMouseLocation.y + ")  "+getTime());
            if (currentMouseLocation.x < 50 || currentMouseLocation.x > Toolkit.getDefaultToolkit().getScreenSize().width - 50 || currentMouseLocation.y < 50 || currentMouseLocation.y > Toolkit.getDefaultToolkit().getScreenSize().height - 50) {
                logWriter.println("Corner, moving it to the middle of screen...");

                System.out.println("Corner, moving it to the middle of screen...");
                robot.mouseMove(Toolkit.getDefaultToolkit().getScreenSize().width/2, Toolkit.getDefaultToolkit().getScreenSize().height/2);
            }
            prev_location=MouseInfo.getPointerInfo().getLocation();
            Thread.sleep(waiting_time);
        }
    }
}
