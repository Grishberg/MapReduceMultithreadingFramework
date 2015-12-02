package com.grishberg.common.job;

/**
 * Created by g on 12.11.15.
 */
public class JobClientConst {
    public static final String JOB_CACHE_NAME = "job.jar";

    public static final int STATE_NONE = 1;
    public static final int STATE_WAITING = 2;
    public static final int STATE_RUNNING = 3;

    // CMD

    public static final int CMD_GET_STATE = 1;
    public static final int CMD_SEND_STATE = 2;
    public static final int CMD_SEND_JOB = 3;
    public static final int CMD_RECEIVED_JOB = 4;

    public static final int CMD_SEND_PARAMETER = 5;
    public static final int CMD_RECEIVED_PARAMETER = 6;

    public static final int CMD_SEND_RESULT = 7;
    public static final int CMD_RECEIVED_RESULT = 8;
    public static final int CMD_START_JOB = 9;
    public static final int CMD_JOB_STARTED = 10;
    public static final int CMD_FAIL_START_JOB = 11;

    public static final int CMD_SEND_JOB_CLASS_NAME = 12;
    public static final int CMD_RECEIVED_JOB_CLASS_NAME = 13;
    public static final int CMD_BAD_PARAMETERS = 14;
    public static final int CMD_JOB_ENDED = 15;
    public static final int CMD_JOB_FAILED = 16;

}
