package com.grishberg.common.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g on 12.11.15.
 */
public class DataInContainer {
    private int length;
    private Object[] values;
    private int index;

    public DataInContainer(byte[] data) {
        int offset = 0;
        length = (data[offset]) + (data[offset + 1] << 8)
                + (data[offset + 2] << 16) + (data[offset + 3] << 24);
        offset += 4;
        values = new Object[length];
        index = 0;

        int type = 0;
        for (int i = 0; i < length; i++) {
            type = (data[offset]) + (data[offset + 1] << 8)
                    + (data[offset + 2] << 16) + (data[offset + 3] << 24);
            offset += 4;
            switch (type) {
                case TlValue.TYPE_INT:
                    values[i] = TlValue.readInt(data, offset);
                    offset += 4;
                    break;
                case TlValue.TYPE_LONG:
                    values[i] = TlValue.readLong(data, offset);
                    offset += 8;
                    break;
                case TlValue.TYPE_STRING:
                    values[i] = TlValue.readString(data, offset);
                    offset += TlValue.readInt(data, offset);
                    offset += 4;
                    break;
                case TlValue.TYPE_BYTE_ARRAY:
                    values[i] = TlValue.readByteArray(data, offset);
                    offset += TlValue.readInt(data, offset);
                    break;
            }
        }
    }

    public int getLength() {
        return length;
    }

    private Object getValue(int i) {
        return values[i];
    }

    public String getString() {
        return (String) values[index++];
    }

    public int getInt() {
        return (int) values[index++];
    }

    public long getLong() {
        return (long) values[index++];
    }

    public byte[] getByteArray() {
        return (byte[]) values[index++];
    }
}
