package com.grishberg.common.io;

/**
 * Created by g on 12.11.15.
 */
public class TlValue {
    public static final int TYPE_INT = 1;
    public static final int TYPE_STRING = 2;
    public static final int TYPE_DOUBLE = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_BYTE_ARRAY = 5;

    private int type;
    private int mIntValue;
    private double mDoubleValue;
    private long mLongValue;
    private String mStringValue;

    public static int readInt(byte[] data, int offset) {
        int result = 0;
        result = (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8)
                + ((data[offset + 2] & 0xFF) << 16) + ((data[offset + 3] & 0xFF) << 24);
        return result;
    }

    public static long readLong(byte[] data, int offset) {
        long result = 0;
        result = (data[offset] & 0xFF) + ((data[offset + 1] & 0xFF) << 8)
                + ((data[offset + 2] & 0xFF) << 16) + ((data[offset + 3] & 0xFF) << 24)
                + ((data[offset + 4] & 0xFF) << 32) + ((data[offset + 5] & 0xFF) << 40)
                + ((data[offset + 6] & 0xFF) << 48) + ((data[offset + 7] & 0xFF) << 56);
        return result;
    }

    public static String readString(byte[] data, int offset) {
        String result = null;
        int length = readInt(data, offset);
        offset += 4;
        byte[] buffer = null;
        if(length > 0) {
            buffer = new byte[length];
            System.arraycopy(data, offset, buffer, 0, length);
            result = new String(buffer);
        }
        return result;
    }

    public static byte[] readByteArray(byte[] data, int offset) {
        int length = readInt(data, offset);
        offset += 4;
        byte[] result = new byte[length];
        System.arraycopy(data, offset, result, 0, length);
        return result;
    }

}
