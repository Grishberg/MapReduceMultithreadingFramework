package com.grishberg.job;

import com.grishberg.common.io.DataInContainer;
import com.grishberg.common.io.DataOutContainer;
import com.grishberg.common.io.Writable;
import com.grishberg.common.job.IJobClient;
import com.grishberg.common.job.JarContainerWritable;
import com.grishberg.common.job.JarRunner;
import com.grishberg.common.job.JobClientConst;
import com.grishberg.tcp.TldContainer;
import com.grishberg.tcp.client.ITcpClientListener;
import com.grishberg.tcp.client.TcpClient;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by g on 12.11.15.
 */
public class JobClient extends TcpClient implements IJobClient, ITcpClientListener {
    private int mState;
    private byte[] mParamaters;
    private String mJobClassName;
    private JarRunner mRunner;

    public void init(String host, int port) {
        mState = JobClientConst.STATE_NONE;
        connect(host, port, this);
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public boolean downloadModule(DataInContainer in) {
        if (mRunner != null) {
            mRunner.stop();
            mRunner = null;
        }
        JarContainerWritable writable = new JarContainerWritable();
        writable.readFields(in);
        mJobClassName = writable.getJarClassName();
        System.out.printf("on download module class name=%s size=%d\n"
                , mJobClassName
                , writable.getJarBody().length);
        if (JarRunner.saveJar(writable.getJarBody())) {
            mState = JobClientConst.STATE_WAITING;
            return true;
        }
        return false;
    }

    @Override
    public boolean runJob() {
        System.out.println("run job");
        boolean state = false;
        if (mState == JobClientConst.STATE_WAITING) {
            if (mRunner != null) {
                mRunner.stop();
            }
            try {
                mRunner = JarRunner.newInstance();
                mRunner.runMap(mJobClassName, null, this);
                state = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return state;
    }

    @Override
    public void onJobEnded() {
        sendCmd(JobClientConst.CMD_JOB_ENDED);
    }

    @Override
    public void onJobFailed() {
        sendCmd(JobClientConst.CMD_JOB_FAILED);
    }

    @Override
    public boolean sendResult(Writable result) {
        DataOutContainer out = new DataOutContainer();
        try {
            result.write(out);
            TldContainer data = new TldContainer(JobClientConst.CMD_SEND_RESULT);
            data.setData(out.getBytes());
            sendMessage(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setParameters(Writable parameters) {
        return false;
    }

    // TCP client listener

    @Override
    public void onConnected(int ip) {
        System.out.println("job client connected");
        sendMessage(new TldContainer(JobClientConst.CMD_SEND_STATE, mState));
    }

    @Override
    public void onDisconnected(int ip) {

    }

    @Override
    public void onConnectError(int ip) {

    }

    @Override
    public void onReceived(int ip, TldContainer data) {
        if (data == null) return;
        System.out.println("on received");
        DataInContainer dataInContainer = null;
        switch (data.getType()) {
            case JobClientConst.CMD_GET_STATE:
                sendState();
                break;
            case JobClientConst.CMD_SEND_JOB:
                dataInContainer = new DataInContainer(data.getData());
                if (dataInContainer.getLength() != 2) {
                    sendCmd(JobClientConst.CMD_BAD_PARAMETERS);
                    break;
                }
                if (downloadModule(dataInContainer)) {
                    sendCmd(JobClientConst.CMD_RECEIVED_JOB);
                }
                break;
            case JobClientConst.CMD_SEND_PARAMETER:
                mParamaters = data.getData();
                sendCmd(JobClientConst.CMD_RECEIVED_PARAMETER);
                break;

            case JobClientConst.CMD_START_JOB:
                mParamaters = data.getData();
                if (runJob()) {
                    sendCmd(JobClientConst.CMD_JOB_STARTED);
                } else {
                    sendCmd(JobClientConst.CMD_FAIL_START_JOB);
                }
                break;
        }
    }

    private void sendCmd(int cmd) {
        System.out.println("send cmd=" + cmd);
        TldContainer data = new TldContainer(cmd);
        sendMessage(data);
    }

    private void sendState() {
        System.out.println("send state");
        TldContainer data = new TldContainer(JobClientConst.CMD_SEND_STATE
                , getState());
        sendMessage(data);
    }
}
