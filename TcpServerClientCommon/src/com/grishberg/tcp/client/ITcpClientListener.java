package com.grishberg.tcp.client;

import com.grishberg.tcp.TldContainer;

/**
 * Created by g on 11.11.15.
 */
public interface ITcpClientListener {
    void onConnected(int ip);
    void onReceived(int ip, TldContainer data);
    void onDisconnected(int ip);
    void onConnectError(int ip);
}
