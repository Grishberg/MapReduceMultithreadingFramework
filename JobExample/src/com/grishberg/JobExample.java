package com.grishberg;

import com.grishberg.common.job.IJobTracker;
import com.grishberg.common.job.Job;

/**
 * Created by g on 13.11.15.
 */
public class JobExample extends Job<ExampleWritable, ExampleWritable> {
    // main execution
    @Override
    public void map(ExampleWritable param) {
        System.out.println("on map called");
        System.out.printf("map: initial parameter = %s", param.str);
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ExampleWritable e = new ExampleWritable(String.format("test_%d", i));
            sendResult(e);
        }
        System.out.println("map done");
    }

    // initiate reduce values
    @Override
    public void setupReduce(IJobTracker listener) {
        super.setupReduce(listener);
    }

    // add result
    @Override
    public void reduce(ExampleWritable param) {
        super.reduce(param);
        System.out.printf("reduce received param = %s\n", param.str);
    }

    // out results
    @Override
    public void onReduceDone() {
        super.onReduceDone();
    }

    public static void main(String[] args) {
        System.out.println("Main stub.");
    }
}
