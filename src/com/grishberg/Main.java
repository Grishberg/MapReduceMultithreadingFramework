package com.grishberg;

import com.grishberg.multithreading.TaskRunnable;
import com.grishberg.parser.SettingsReader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by g on 07.11.15.
 */
public class Main {
    private Aggregator aggregator;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private ExecutorService executor = Executors.newFixedThreadPool(CPU_COUNT * 2);

    public Main(String inPath, String outPath, String configPath) {
        aggregator = new Aggregator();
        SettingsReader settingsReader = new SettingsReader(configPath);
        List<String> filter = settingsReader.getUrls();
        System.out.println("LogChecker v1.0");
        System.out.println(String.format("cores count = %d", CPU_COUNT));
        long startTime = System.currentTimeMillis();

        File f = new File(inPath);
        File[] matchingFiles = f.listFiles();
        if (filter.size() == 0) {
            System.out.println("settings file is empty");
            return;
        }
        System.out.println(String.format("mappers count = %d", matchingFiles.length));
        aggregator.setMappersCount(matchingFiles.length);
        for (File file : matchingFiles) {
            TaskRunnable taskRunnable = new TaskRunnable(file.getAbsolutePath(), filter, aggregator);
            executor.submit(taskRunnable);
        }
        System.out.println("wait while run workers...");

        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int resultCount = aggregator.printResults(outPath);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        String msg = String.format("Done. result count = %d \n running time = %d ms\n"
                , resultCount
                , totalTime);
        System.out.println(msg);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("usage <in> <out> <config>");
            return;
        }
        new Main(args[0], args[1], args[2]);
    }
}
