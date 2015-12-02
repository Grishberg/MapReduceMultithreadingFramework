package com.grishberg.tcp.server;

import com.grishberg.tcp.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by g on 11.11.15.
 */
class ServerConnectionHandler implements
        CompletionHandler<AsynchronousSocketChannel, ServerConnectionHandler.IConnectionListener> {
    @Override
    public void completed(AsynchronousSocketChannel client, IConnectionListener attach) {
        try {
            SocketAddress clientAddr = client.getRemoteAddress();
            System.out.format("Accepted a  connection from  %s%n", clientAddr);

            int clientIp = 0;
            if (clientAddr instanceof InetSocketAddress) {
                clientIp = Utils.ip2int(((InetSocketAddress) clientAddr).getAddress().getAddress());
            }
            if (attach != null) {
                attach.onAccepted(clientIp, client);
                AsynchronousServerSocketChannel socketChannel = attach.getServerSocket();
                // Listen for a new request
                String host = "localhost";
                InetSocketAddress sAddr = new InetSocketAddress(host, attach.getPort());
                System.out.format("Server is listening at %s%n", sAddr);

                socketChannel.accept(attach, new ServerConnectionHandler());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable e, IConnectionListener attach) {
        System.out.println("Failed to accept a  connection.");
        e.printStackTrace();
    }

    public interface IConnectionListener {
        void onAccepted(int ip, AsynchronousSocketChannel clientSocket);

        AsynchronousServerSocketChannel getServerSocket();

        int getPort();
    }
}