package com.grishberg.tcp;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by g on 10.11.15.
 */
public class Utils {
    public static int ip2int(byte[] addr) {
        if (addr == null || addr.length < 4) return -1;
        int result = addr[0] + (addr[1] << 8) + (addr[2] << 16) + (addr[3] << 24);
        return result;
    }
    public static String int2ip(int ip) {
        String addr =
                String.format("%d.%d.%d.%d",
                        (ip & 0xff),
                        (ip >> 8 & 0xff),
                        (ip >> 16 & 0xff),
                        (ip >> 24 & 0xff));
        return addr;
    }
}
