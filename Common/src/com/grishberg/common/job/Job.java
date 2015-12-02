package com.grishberg.common.job;

import com.grishberg.common.io.DataInContainer;
import com.grishberg.common.io.Writable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * Created by g on 13.11.15.
 */
public class Job<INPUT_PARAMETER extends Writable, OUTPUT_PARAMETER extends Writable> {
    protected IJobClient mClientListener;
    protected IJobTracker mTrackerListener;

    private INPUT_PARAMETER getMapParameterInstance() {

        try {
            return (INPUT_PARAMETER) ((Class) ((ParameterizedType) this.getClass().
                    getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OUTPUT_PARAMETER getReduceParameterInstance() {

        try {
            return (OUTPUT_PARAMETER) ((Class) ((ParameterizedType) this.getClass().
                    getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setupMap(IJobClient listener) {
        mClientListener = listener;
    }

    public void initMap(DataInContainer in) {
        INPUT_PARAMETER instance = getMapParameterInstance();
        try {
            instance.readFields(in);
            map(instance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // need override this method to start
    protected void map(INPUT_PARAMETER param) {
        System.out.println("run parent map");
    }

    protected void sendResult(OUTPUT_PARAMETER result) {
        if (mClientListener != null) {
            mClientListener.sendResult(result);
        }
    }

    public void setupReduce(IJobTracker listener) {
        mTrackerListener = listener;
    }

    public void initReduce(DataInContainer in) {
        OUTPUT_PARAMETER instance = getReduceParameterInstance();
        try {
            instance.readFields(in);
            reduce(instance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reduce(OUTPUT_PARAMETER param) {
        System.out.println("run parent reduce");
    }

    public void onReduceDone() {
    }

    public void stop() {
        mTrackerListener = null;
        mClientListener = null;
        System.out.println("job stopped");
    }
}
