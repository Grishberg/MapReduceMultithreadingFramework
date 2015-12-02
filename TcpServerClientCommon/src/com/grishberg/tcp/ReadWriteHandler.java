package com.grishberg.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by g on 11.11.15.
 */
public class ReadWriteHandler extends Thread {
    private IOnReceivedPacketListener mListener;
    private AsynchronousSocketChannel mClientSocket;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
    private int mIp;
    private Integer mId;

    public ReadWriteHandler(IOnReceivedPacketListener listener, AsynchronousSocketChannel client
            , Integer id
            , int ip) {
        this.mListener = listener;
        mClientSocket = client;
        mId = id;
        mIp = ip;
    }

    public void sendMessage(TldContainer message) {
        if (mClientSocket != null && mClientSocket.isOpen()) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(message.toByteArray());
            mClientSocket.write(byteBuffer);
        }
    }

    @Override
    public void run() {

        try {
            int totalReaded = 0;
            int messageSize = 0;
            byte type = 0;

            // Read the first line
            int bytesRead = mClientSocket.read(byteBuffer).get();

            boolean running = true;
            boolean isReadingPacket = false;
            byte[] data = null;
            int offset = 8;
            while (bytesRead != -1 && running) {
                // Make sure that we have data to read
                if (byteBuffer.position() > 2) {
                    // Make the buffer ready to read
                    byteBuffer.flip();

                    // Convert the buffer into a line
                    byte[] b = new byte[bytesRead];
                    byteBuffer.get(b, 0, bytesRead);
                    if (!isReadingPacket) {
                        isReadingPacket = true;
                        type = b[0];
                        offset = 8;
                        messageSize = (b[4] & 0xFF) + ((b[5] & 0xFF) << 8)
                                + ((b[6] & 0xFF) << 16) + ((b[7] & 0xFF) << 24);
                        if (messageSize > 0xFFFFFF) {
                            System.out.println("bad length");
                            isReadingPacket = false;
                            continue;
                        }
                        data = new byte[messageSize];
                    }
                    System.out.printf("arrayCopy b size =%d, b offset=%d, dst size=%d, totalReaded=%d, l=%d\n",
                            b.length, offset, data.length, totalReaded, bytesRead - offset);
                    System.arraycopy(b, offset, data, totalReaded, bytesRead - offset);
                    totalReaded += bytesRead - offset;
                    offset = 0;

                    if (totalReaded == messageSize) {
                        isReadingPacket = false;
                        TldContainer tld = new TldContainer(type, data);
                        System.out.printf("[ Message len: %d ]\n", totalReaded);
                        String line = new String(b);
                        totalReaded = 0;
                        bytesRead = 0;
                        if (mListener != null) {
                            mListener.onReceived(mId, mIp, tld);
                        }
                    }

                    // Make the buffer ready to write
                    byteBuffer.clear();

                    // Read the next line
                    bytesRead = mClientSocket.read(byteBuffer).get();
                } else {
                    // An empty line signifies the end of the conversation in our protocol
                    running = false;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("interrupted");
        } catch (ExecutionException e) {
            System.out.println("connection aborted");
        }
        if (mListener != null) {
            mListener.onDisconnect(mId, mIp);
        }
        if (mClientSocket != null) {
            try {
                mClientSocket.close();
                mClientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (mClientSocket != null) {
            try {
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mClientSocket = null;
        }
    }

    public int getIp() {
        return mIp;
    }

    public int getThreadId() {
        return mId;
    }

    private static int arrayToInt(byte[] array, int offset) {
        ByteBuffer wrapper = ByteBuffer.wrap(array, offset, 4);
        return wrapper.getInt();
    }

    private static long arrayToLong(byte[] array, int offset) {
        ByteBuffer wrapper = ByteBuffer.wrap(array, offset, 8);
        return wrapper.getLong();
    }

    public interface IOnReceivedPacketListener {
        void onReceived(Integer id, int ip, TldContainer data);

        void onDisconnect(Integer id, int ip);
    }
}