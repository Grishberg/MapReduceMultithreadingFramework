package com.grishberg.common.job;

import com.grishberg.common.io.DataInContainer;
import com.grishberg.common.io.Writable;
/**
 * Created by g on 12.11.15.
 */
public interface IJobClient {
    int getState();
    boolean downloadModule(DataInContainer in);
    boolean runJob();
    boolean sendResult(Writable result);
    boolean setParameters(Writable parameters);
    void onJobEnded();
    void onJobFailed();
}
