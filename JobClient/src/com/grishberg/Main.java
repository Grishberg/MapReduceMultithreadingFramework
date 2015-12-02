package com.grishberg;

import com.grishberg.common.PropertiesReader;
import com.grishberg.job.JobClient;
import com.grishberg.tcp.Utils;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Map<String, String> defaults = new HashMap<>();
        defaults.put("server.host", "127.0.0.1");
        defaults.put("server.port","7002");
        PropertiesReader propertiesReader = new PropertiesReader(defaults);
        String clientIp = propertiesReader.readString("server.host");
        int port = propertiesReader.readInt("server.port", 7002);
        JobClient jobClient = new JobClient();
        while (true) {
            try {
                jobClient.init(clientIp, port);
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                System.out.println("stopped.");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (jobClient != null) {
                    jobClient.disconnect();
                }
            }
        }
        jobClient.disconnect();
        System.out.println("done.");
    }
}
