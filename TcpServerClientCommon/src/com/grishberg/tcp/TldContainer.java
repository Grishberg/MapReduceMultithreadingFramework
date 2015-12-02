package com.grishberg.tcp;

import com.sun.istack.internal.NotNull;

/**
 * Created by g on 10.11.15.
 * container for sending packets with type and length
 */
public class TldContainer {
    private int type;
    private int length;
    private byte[] data;

    public TldContainer(int type) {
        this.type = type;
        length = 0;
        data = null;
    }

    public TldContainer(int type, int state) {
        this.type = type;
        length = 4;
        data = new byte[4];
        data[0] = (byte) (state & 0xFF);
        data[1] = (byte) ((state & 0xFF00) >> 8);
        data[2] = (byte) ((state & 0xFF0000) >> 16);
        data[3] = (byte) ((state & 0xFF000000) >> 24);
    }

    public TldContainer(int type, @NotNull byte[] data) {
        this.type = type;
        this.data = data;
        this.length = data.length;
    }

    public TldContainer(int type, String str) {
        this.type = type;
        data = str.getBytes();
        this.length = data.length;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[length + 8];
        result[0] = (byte) (type & 0xFF);
        result[1] = (byte) ((type & 0xFF00) >> 8);
        result[2] = (byte) ((type & 0xFF0000) >> 16);
        result[3] = (byte) ((type & 0xFF000000) >> 24);

        result[4] = (byte) (length & 0xFF);
        result[5] = (byte) ((length & 0xFF00) >> 8);
        result[6] = (byte) ((length & 0xFF0000) >> 16);
        result[7] = (byte) ((length & 0xFF000000) >> 24);
        if (length > 0) {
            System.arraycopy(data, 0, result, 8, length);
        }
        return result;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(@NotNull byte[] data) {
        this.data = data;
        this.length = data.length;
    }
}
