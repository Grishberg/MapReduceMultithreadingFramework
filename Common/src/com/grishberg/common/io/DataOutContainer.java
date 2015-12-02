package com.grishberg.common.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 12.11.15.
 */
public class DataOutContainer {
    private List<Byte> buffer;
    private int count;

    public DataOutContainer() {
        this.buffer = new ArrayList<>();
        count = 0;
    }

    private void writeIntValue(int value) {
        buffer.add((byte) (value & 0xFF));
        buffer.add((byte) ((value & 0xFF00) >> 8));
        buffer.add((byte) ((value & 0xFF0000) >> 16));
        buffer.add((byte) ((value & 0xFF000000) >> 24));
    }

    public void writeInt(int value) {
        writeIntValue(TlValue.TYPE_INT);
        writeIntValue(value);
        count++;
    }

    public void writeLong(long value) {
        writeIntValue(TlValue.TYPE_LONG);
        buffer.add((byte) (value & 0xFF));
        buffer.add((byte) ((value & 0xFF00) >> 8));
        buffer.add((byte) ((value & 0xFF0000) >> 16));
        buffer.add((byte) ((value & 0xFF000000) >> 24));

        buffer.add((byte) ((value >> 32) & 0xFF));
        buffer.add((byte) ((value >> 40) & 0xFF));
        buffer.add((byte) ((value >> 48) & 0xFF));
        buffer.add((byte) ((value >> 56) & 0xFF));
        count++;
    }

    public void writeString(String value) {
        byte[] bytes = null;
        writeIntValue(TlValue.TYPE_STRING);
        if(value != null) {
            bytes = value.getBytes();
            writeIntValue(bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                buffer.add(bytes[i]);
            }
        } else {
            writeIntValue(0);
        }
        count++;
    }

    public void writeByteArray(byte[] bytes) {
        writeIntValue(TlValue.TYPE_BYTE_ARRAY);
        writeIntValue(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            buffer.add(bytes[i]);
        }
        count++;
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[buffer.size() + 4];
        bytes[0] = (byte) (count & 0xFF);
        bytes[1] = (byte) ((count & 0xFF00) >> 8);
        bytes[2] = (byte) ((count & 0xFF0000) >> 16);
        bytes[2] = (byte) ((count & 0xFF000000) >> 24);
        for (int i = 0; i < buffer.size(); i++) {
            bytes[i + 4] = buffer.get(i);
        }
        return bytes;
    }
}
