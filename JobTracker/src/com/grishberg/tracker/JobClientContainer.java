package com.grishberg.tracker;

import com.grishberg.common.job.JobClientConst;

/**
 * Created by g on 29.11.15.
 */
public class JobClientContainer {
    private int ip;
    private int status;

    public JobClientContainer(int ip) {
        this.ip = ip;
        status = JobClientConst.STATE_NONE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
