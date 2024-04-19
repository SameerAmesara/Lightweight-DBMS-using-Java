package org.LightweightDBMS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

public class Logs {
    static User user;
    static BufferedWriter logWriter;
    static LocalTime getLogTime;
    static String logTime;
    public static String fileName;


    public Logs(User user)
    {
        Logs.user = user;
        Logs.getLogTime = LocalTime.now();
        Logs.logTime = String.valueOf(Logs.getLogTime).replace(':','_');
        Logs.logTime = Logs.logTime.split(".")[0];
    }

    public Logs()
    {

    }

    public static void addLogs(String log, String extra, boolean printOutput)
    {
        try {
            Logs.logWriter = new BufferedWriter(new FileWriter(Logs.fileName,true));
            Logs.logWriter.write(log+" "+extra);
            Logs.logWriter.write("\n");
            Logs.logWriter.close();
            if(printOutput)
            {
                System.out.println(log);
            }

        }
        catch(IOException e)
        {
            System.out.println("Not able to write logs");
        }

    }

    public static void startLogs(boolean isNotTest){
        //System.out.println("In Log start.");
        Logs.getLogTime = LocalTime.now();
        Logs.logTime = String.valueOf(Logs.getLogTime).replace(':','_');
        //System.out.println(Logs.logTime);
        String[] logTimeList = Logs.logTime.split("\\.");
        Logs.logTime = logTimeList[0];

        Logs.fileName = "src/main/java/org/LightweightDBMS/files/logs/"+Logs.logTime+".txt";
        try
        {
            Logs.getLogTime = LocalTime.now();
            File logFile = new File(Logs.fileName);

            if (logFile.createNewFile()) {
                if(isNotTest) {
                    System.out.println("File created: " + logFile.getName());
                }
            } else {
                if(isNotTest){
                    System.out.println("File already exists.");
                }

            }

            Logs.logWriter = new BufferedWriter(new FileWriter(Logs.fileName));
            Logs.logWriter.write("---Started---\n");
            Logs.logWriter.close();
        }
        catch (IOException e)
        {
            System.out.println("Not able to open log file.");
        }

    }

    public static void deleteLog() {
        File logFile = new File(Logs.fileName);
        if (logFile.delete()) {
            System.out.println("Log file deleted successfully.");
        } else {
            System.out.println("Failed to delete log file.");
        }
    }

    public static void stopLogs() {
        try {
            if (Logs.logWriter != null) {
                Logs.logWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
