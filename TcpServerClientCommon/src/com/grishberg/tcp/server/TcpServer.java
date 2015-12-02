package com.grishberg.tcp.server;

import com.grishberg.tcp.ReadWriteHandler;
import com.grishberg.tcp.TldContainer;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by g on 10.11.15.
 */
public class TcpServer implements ITcpServer, ServerConnectionHandler.IConnectionListener
        , ReadWriteHandler.IOnReceivedPacketListener {
    public static final int TRIES_COUNT = 3;
    private int port = 3310;
    private volatile boolean isConnected;
    private AsynchronousServerSocketChannel mServer;
    private AsynchronousSocketChannel mClient;
    private ITcpServerListener mListener;
    private BlockingDeque<TldContainer> mMsgQueue;
    // clients handler array
    private HashMap<Integer, ReadWriteHandler> mRwHandlers;
    private int mThreadId;

    public TcpServer() {
        mMsgQueue = new LinkedBlockingDeque<>();
        mRwHandlers = new HashMap<>();
    }

    @Override
    public void onAccepted(int ip, AsynchronousSocketChannel clientSocket) {
        mClient = clientSocket;
        // start listen incoming messages
        //attach.server.accept(attach, this);
        ReadWriteHandler rwHandler = new ReadWriteHandler(this, mClient, mThreadId, ip);
        mRwHandlers.put(mThreadId, rwHandler);
        rwHandler.start();
        if (mListener != null) {
            mListener.onConnected(mThreadId, ip);
        }
        mThreadId++;
    }

    @Override
    public AsynchronousServerSocketChannel getServerSocket() {
        return mServer;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void onReceived(Integer id, int ip, TldContainer data) {
        System.out.println("on received tld");
        if (mListener != null) {
            mListener.onReceived(id, ip, data);
        }
    }

    @Override
    public void onDisconnect(Integer id, int ip) {
        if (mListener != null) {
            mListener.onDisconnected(id, ip);
        }
    }

    @Override
    public boolean start(int port, ITcpServerListener listener) {
        this.port = port;
        mListener = listener;
        for (int tries = 0; tries < TRIES_COUNT; tries++) {
            try {
                mServer = AsynchronousServerSocketChannel.open(/*group*/);
                // Listen for a new request
                mServer.bind(new InetSocketAddress(port));
                System.out.format("Server is listening at %d%n", port);

                mServer.accept(this, new ServerConnectionHandler());
                break;
            } catch (BindException e) {
                System.out.println("Адрес уже используется.");
                try {
                    mServer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    @Override
    public void stop() {
        if (mRwHandlers != null) {
            Iterator it = mRwHandlers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ((ReadWriteHandler) pair.getValue()).disconnect();
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
        mRwHandlers = null;
        if (mServer != null) {
            try {
                mServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public boolean sendMessage(Integer id, TldContainer message) {
        ReadWriteHandler readWriteHandler = mRwHandlers.get(id);
        if (readWriteHandler == null) {
            return false;
        }
        readWriteHandler.sendMessage(message);
        return true;
    }
}
