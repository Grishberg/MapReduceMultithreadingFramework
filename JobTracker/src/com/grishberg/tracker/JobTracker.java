package com.grishberg.tracker;

import com.grishberg.common.io.DataInContainer;
import com.grishberg.common.io.DataOutContainer;
import com.grishberg.common.io.TlValue;
import com.grishberg.common.job.JarRunner;
import com.grishberg.common.job.IJobTracker;
import com.grishberg.common.job.JarContainerWritable;
import com.grishberg.common.job.JobClientConst;
import com.grishberg.httpserver.HttpSocketServer;
import com.grishberg.tcp.TldContainer;
import com.grishberg.tcp.Utils;
import com.grishberg.tcp.server.ITcpServerListener;
import com.grishberg.tcp.server.TcpServer;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by g on 14.11.15.
 */
public class JobTracker extends TcpServer implements IJobTracker, ITcpServerListener {
    private Map<Integer, JobClientContainer> mClients;
    private HttpSocketServer mHttpServer;
    private int mStartedJobCount;
    private JarRunner mJarRunner;
    private String mClassPath;
    private int mHttpPort;

    public JobTracker() {
        mClients = new HashMap<>();
    }

    public void startServer(int port, int httpPort) {
        start(port, this);
        mHttpPort = httpPort;
        mHttpServer = new HttpSocketServer(this, httpPort, new BodyGenerator(this));
        mHttpServer.startServer();
    }

    @Override
    public int getHttpPort() {
        return mHttpPort;
    }

    public void stopServer() {
    }

    @Override
    public boolean runReducer() {
        return false;
    }

    @Override
    public boolean sendJob(String classPath, byte[] body) {
        TldContainer tldContainer = new TldContainer(JobClientConst.CMD_SEND_JOB);
        JarContainerWritable writable = new JarContainerWritable(classPath, body);
        mClassPath = classPath;
        DataOutContainer dataOutContainer = new DataOutContainer();
        writable.write(dataOutContainer);
        tldContainer.setData(dataOutContainer.getBytes());
        // send data to clients
        for (Map.Entry<Integer, JobClientContainer> client : mClients.entrySet()) {
            System.out.printf("send job to client id = %d data t=%d l=%d\n"
                    , client.getKey(), tldContainer.getType(), tldContainer.getLength());
            sendMessage(client.getKey(), tldContainer);
        }
        //save job
        if (!JarRunner.saveJar(writable.getJarBody())) {
            return false;
        }
        try {
            mJarRunner = JarRunner.newInstance();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (mJarRunner != null) {
            mJarRunner.runReduce(mClassPath, null, this);
        }
        return mJarRunner != null;
    }

    @Override
    public boolean sendGetStatus() {
        TldContainer tldContainer = new TldContainer(JobClientConst.CMD_GET_STATE);
        // send data to clients
        for (Map.Entry<Integer, JobClientContainer> client : mClients.entrySet()) {
            sendMessage(client.getKey(), tldContainer);
        }
        return false;
    }

    @Override
    public boolean sendStartJobCmd() {
        TldContainer tldContainer = new TldContainer(JobClientConst.CMD_START_JOB);
        if (mJarRunner != null) {
            mJarRunner.runReduce(mClassPath, null, this);
        }
        // send data to clients
        for (Map.Entry<Integer, JobClientContainer> client : mClients.entrySet()) {
            sendMessage(client.getKey(), tldContainer);
        }
        return false;
    }

    //Tcp server

    @Override
    public void onReceived(Integer id, int ip, TldContainer data) {
        System.out.printf("Received from id = %d  ip = %s data: t=%d, l=%d\n"
                , id, Utils.int2ip(ip), data.getType(), data.getLength());
        //recognize
        switch (data.getType()) {
            case JobClientConst.CMD_RECEIVED_PARAMETER:
                break;
            case JobClientConst.CMD_RECEIVED_JOB:
                onJobReceived(id, ip);
                break;
            case JobClientConst.CMD_JOB_STARTED:
                onJobStarted(id, ip);
                break;
            case JobClientConst.CMD_FAIL_START_JOB:
                break;
            case JobClientConst.CMD_JOB_FAILED:
                break;
            case JobClientConst.CMD_JOB_ENDED:
                onJobEnded(id, ip);
                break;
            case JobClientConst.CMD_SEND_RESULT:
                onSendResult(id, ip, data);
                break;
            case JobClientConst.CMD_SEND_STATE:
                onReceivedStatus(id, ip, data);
                break;
        }
    }

    @Override
    public void onConnected(Integer id, int ip) {
        mClients.put(id, new JobClientContainer(ip));
        System.out.printf("Client %d connected ip = %s\n", id, Utils.int2ip(ip));
    }

    @Override
    public void onDisconnected(Integer id, int ip) {
        mClients.remove(id);
        System.out.printf("Client %d disconnected ip = %s\n", id, Utils.int2ip(ip));
    }

    @Override
    public void onAcceptError() {

    }

    // resp
    private void onReceivedStatus(Integer id, int ip, TldContainer data) {
        int state = TlValue.readInt(data.getData(), 0);
        System.out.printf(">> received status client id=%d ip=%s state=%d\n"
                , id, Utils.int2ip(ip), state);
        JobClientContainer client = mClients.get(id);
        if (client != null) {
            client.setStatus(state);
        }
        switch (state) {
            case JobClientConst.STATE_NONE:
                System.out.println("state none");
                break;
            case JobClientConst.STATE_RUNNING:
                System.out.println("state running");
                break;
            case JobClientConst.STATE_WAITING:
                System.out.println("state waiting");
                break;
        }
    }

    private void onJobStarted(Integer id, int ip) {
        System.out.printf(">> job started client id=%d ip=%s\n", id, Utils.int2ip(ip));
        JobClientContainer client = mClients.get(id);
        if (client != null) {
            client.setStatus(JobClientConst.STATE_RUNNING);
        }
    }

    private void onJobReceived(Integer id, int ip) {
        System.out.printf(">> job received client id=%d ip=%s\n", id, Utils.int2ip(ip));
        JobClientContainer client = mClients.get(id);
        if (client != null) {
            client.setStatus(JobClientConst.STATE_WAITING);
        }
    }

    private void onJobEnded(Integer id, int ip) {
        System.out.printf(">> job ended client id=%d ip=%s\n", id, Utils.int2ip(ip));
        JobClientContainer client = mClients.get(id);
        if (client != null) {
            client.setStatus(JobClientConst.STATE_WAITING);
        }
    }

    private void onSendResult(Integer id, int ip, TldContainer data) {
        System.out.printf(">> received result: client id=%d ip=%s\n", id, Utils.int2ip(ip));
        JobClientContainer client = mClients.get(id);
        System.out.printf(">> result body = %s", new String(data.getData()));
/*
        if(client != null){
            client.setStatus(JobClientConst.STATE_WAITING);
        }
*/
        DataInContainer in = new DataInContainer(data.getData());
        // send result to job host
        if (mJarRunner != null) {
            mJarRunner.sendResult(in);
        }
    }

    @Override
    public void onJobEnded() {
        System.out.println(">> on job ended");
    }
}
