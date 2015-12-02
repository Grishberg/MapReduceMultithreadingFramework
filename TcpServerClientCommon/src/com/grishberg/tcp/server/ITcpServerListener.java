package com.grishberg.tcp.server;

import com.grishberg.tcp.TldContainer;

/**
 * Created by g on 10.11.15.
 */
public interface ITcpServerListener {
    void onConnected(Integer id, int ip);
    void onReceived(Integer id, int ip, TldContainer data);
    void onDisconnected(Integer id, int ip);
    void onAcceptError();
}
