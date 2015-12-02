package com.grishberg.common.job;

/**
 * Created by g on 13.11.15.
 */
public interface IJobTracker {
    boolean runReducer();
    boolean sendJob(String classPath, byte[] body);
    boolean sendStartJobCmd();
    boolean sendGetStatus();
    int getHttpPort();
    void onJobEnded();
}
