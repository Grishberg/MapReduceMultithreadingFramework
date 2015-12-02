package com.grishberg;

import com.grishberg.tcp.TldContainer;
import com.grishberg.tcp.Utils;
import com.grishberg.tcp.client.ITcpClientListener;
import com.grishberg.tcp.client.TcpClient;

public class Main implements ITcpClientListener {
    TcpClient mClient;
    private int mClientId;

    public Main() {
        mClient = new TcpClient();
        byte[] addr = {127,0,0,1};
        mClient.connect("127.0.1.1", 7000, this);
    }

    @Override
    public void onConnected(int ip) {
        System.out.println("on connected ip=" + ip + " " + Utils.int2ip(ip));
        TldContainer data = new TldContainer(1,"test");
        mClient.sendMessage(data);
    }

    @Override
    public void onDisconnected(int ip) {
        System.out.println("on disconnected");
    }

    @Override
    public void onReceived(int ip, TldContainer data) {
        System.out.println("on received");

        mClient.sendMessage(data);

    }

    @Override
    public void onConnectError(int ip) {
        System.out.println("on error");
    }

    public void stop() {
        if (mClient != null) {
            mClient.disconnect();
        }
    }

    public static void main(String[] args) {
        // write your code here
        Main main = new Main();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        main.stop();
        System.out.println("done.");
    }
}
