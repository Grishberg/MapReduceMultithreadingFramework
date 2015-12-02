package com.grishberg.tcp.client;

import java.nio.channels.CompletionHandler;

/**
 * Created by g on 12.11.15.
 */
public class ClientConnectionHandler implements
        CompletionHandler<Void, ClientConnectionHandler.IConnectionListener> {
    @Override
    public void completed(Void server, IConnectionListener attach) {
        if (attach != null) {
            attach.onConnected();
        }
    }

    @Override
    public void failed(Throwable exc, IConnectionListener attachment) {
        System.out.println("fail connect to server");
        if(attachment != null){
            attachment.onFail(exc.getLocalizedMessage());
        }
    }

    public interface IConnectionListener {
        void onConnected();
        void onFail(String error);
    }
}
