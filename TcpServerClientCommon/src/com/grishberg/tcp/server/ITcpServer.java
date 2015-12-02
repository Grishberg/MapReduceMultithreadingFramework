package com.grishberg.tcp.server;

import com.grishberg.tcp.TldContainer;

/**
 * Created by g on 10.11.15.
 */
public interface ITcpServer {
    boolean start(int port,ITcpServerListener listener);
    boolean sendMessage(Integer id, TldContainer message);
    void stop();
    boolean isConnected();
}
