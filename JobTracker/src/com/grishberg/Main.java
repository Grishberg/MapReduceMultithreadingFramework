package com.grishberg;

import com.grishberg.common.PropertiesReader;
import com.grishberg.tracker.JobTracker;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("http.port", "9999");
        defaults.put("server.port", "7002");
        PropertiesReader propertiesReader = new PropertiesReader(defaults);
        int httpPort = propertiesReader.readInt("http.port", 9999);
        int port = propertiesReader.readInt("server.port", 7002);

        JobTracker tracker = new JobTracker();
        tracker.startServer(port, httpPort);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tracker.stop();
        System.out.println("done.");
    }
}
