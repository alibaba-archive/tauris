package com.aliyun.tauris.utils;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class MapperUtil {


    /**
     * big endian
     * @param bytes
     * @param offset
     * @return
     */
    public static short bytes2short(byte[] bytes, int offset) {
        int mask = 0x00FF;
        short val = 0;
        for (int i = 1 ;i >= 0; i--) {
            val |= (bytes[offset + 1 - i] & mask) << ( i * 8 );
        }
        return val;
    }

    public static void short2bytes(short val, byte[] dest, int offset) {
        for (int i = 1; i >= 0; i--) {
            dest[offset + i] = (byte)(val & 0xFF);
            val = (short)(val >>> 8);
        }
    }

    /**
     * big endian
     * @param bytes
     * @param offset
     * @return
     */
    public static int bytes2int(byte[] bytes, int offset) {
        int mask = 0x000000FF;
        int val = 0;
        for (int i = 3 ;i >= 0; i--) {
            val |= (bytes[offset + 3 - i] & mask) << ( i * 8 );
        }
        return val;
    }

    /**
     * little endian
     * @param bytes
     * @param offset
     * @return
     */
    public static int lbytes2int(byte[] bytes, int offset) {
        int mask = 0x000000FF;
        int val = 0;
        for (int i = 3 ;i >= 0; i--) {
            val |= (bytes[offset + i] & mask) << ( i * 8 );
        }
        return val;
    }

    /**
     * big endian
     * @param val
     * @param dest
     * @param offset
     */
    public static void int2bytes(int val, byte[] dest, int offset) {
        for (int i = 3; i >= 0; i--) {
            dest[offset + i] = (byte)(val & 0xFF);
            val = val >>> 8;
        }
    }

    /**
     * little endian
     * @param val
     * @param dest
     * @param offset
     */
    public static void int2lbytes(int val, byte[] dest, int offset) {
        for (int i = 0; i <= 3; i++) {
            dest[offset + i] = (byte)(val & 0xFF);
            val = val >>> 8;
        }
    }

    public static long bytes2long(byte[] bytes, int offset) {
        long mask = 0x00000000000000FF;
        long val = 0;
        for (int i = 7 ;i >= 0; i--) {
            val |= (bytes[offset + 7 - i] & mask) << ( i * 8 );
        }
        return val;
    }

    /**
     * big endian
     * @param val
     * @param dest
     * @param offset
     */
    public static void long2bytes(long val, byte[] dest, int offset) {
        for (int i = 7; i >= 0; i--) {
            dest[offset + i] = (byte)(val & 0xFF);
            val = val >>> 8;
        }
    }

    /**
     * little endian
     * @param bytes
     * @param offset
     * @return
     */
    public static long lbytes2long(byte[] bytes, int offset) {
        long mask = 0x00000000000000FF;
        long val = 0;
        for (int i = 7 ;i >= 0; i--) {
            val |= (bytes[offset + i] & mask) << ( i * 8 );
        }
        return val;
    }

    public static void long2lbytes(long val, byte[] dest, int offset) {
        for (int i = 7; i >= 0; i--) {
            dest[offset + i] = (byte) (val >> i * 8);
        }
    }

    public static byte[] int2lbytes(int val) {
        byte[] buf = new byte[4];
        int2lbytes(val, buf, 0);
        return buf;
    }

    public static byte[] int2bytes(int val) {
        byte[] buf = new byte[4];
        int2bytes(val, buf, 0);
        return buf;
    }
}
