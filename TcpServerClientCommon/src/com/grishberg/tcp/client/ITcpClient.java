package com.grishberg.tcp.client;

import com.grishberg.tcp.TldContainer;

/**
 * Created by g on 11.11.15.
 */
public interface ITcpClient {
    boolean connect(String host, int port, ITcpClientListener listener);
    boolean sendMessage(TldContainer message);
    void disconnect();
    boolean isConnected();
}
