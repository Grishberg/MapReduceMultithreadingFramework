package com.grishberg.tcp.client;

import com.grishberg.tcp.ReadWriteHandler;
import com.grishberg.tcp.TldContainer;
import com.grishberg.tcp.Utils;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by g on 11.11.15.
 */
public class TcpClient implements ITcpClient, ClientConnectionHandler.IConnectionListener
        , ReadWriteHandler.IOnReceivedPacketListener {
    private volatile boolean isConnected;
    private ITcpClientListener mListener;
    private AsynchronousSocketChannel mClient;
    private ReadWriteHandler mRwHandler;
    private int mPort;
    private int mIp;
    private String mHost;

    @Override
    public boolean connect(String host, int port, ITcpClientListener listener) {
        mListener = listener;
        mPort = port;
        mIp = 0;
        mHost = host;
        try {
            mClient = AsynchronousSocketChannel.open(/*group*/);
            // Listen for a new request
            InetSocketAddress sAddr = new InetSocketAddress(host, port);
            mClient.connect(sAddr, this, new ClientConnectionHandler());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onConnected() {
        isConnected = true;
        mRwHandler = new ReadWriteHandler(this, mClient, 0, mIp);
        mRwHandler.start();
        if (mListener != null) {
            mListener.onConnected(mIp);
        }
    }

    @Override
    public void onFail(String msg) {
        //TODO: reconnect
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connect(mHost, mPort, mListener);
    }

    @Override
    public void onReceived(Integer id, int ip, TldContainer data) {
        System.out.println("on received tld");
        if (mListener != null) {
            mListener.onReceived(ip, data);
        }
    }

    @Override
    public void onDisconnect(Integer id, int ip) {
        if (mListener != null) {
            mListener.onDisconnected(ip);
        }
        onFail("disconnected by server");
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public boolean sendMessage(TldContainer message) {
        if (!isConnected || mRwHandler == null) return false;
        mRwHandler.sendMessage(message);
        return false;
    }

    @Override
    public void disconnect() {
        mRwHandler.disconnect();
    }
}
