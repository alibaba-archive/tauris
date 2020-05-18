package com.aliyun.tauris.plugins.filter.iploc;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class IPUtils {
    private static String  regex   = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "((1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.){2}"
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    private static Pattern pattern = Pattern.compile(regex);

    /**
     * 判断是否为有效IP 不区分局域网 保留地址等
     *
     * @param ip ip
     * @return true 有效 false 无效
     */
    public static boolean validate(long ip) {
        return ip >= 0 && ip <= 0xFFFFFFFFL;
    }

    /**
     * 判断是否是合法ip地址 不区分局域网 保留地址等
     *
     * @param ip ip地址
     * @return true 有效 false无效
     */
    public static boolean validate(String ip) {
        return !StringUtils.isEmpty(ip) && pattern.matcher(ip).matches();
    }

    /**
     * ip字符串转long 转换之前请判断是否合法
     *
     * @param ip ip地址
     * @return 对应的long值
     */
    public static long toLong(String ip) {
        long result = 0;
        StringTokenizer token = new StringTokenizer(ip, ".");
        for (int i = 0; i < 4; i++) {
            result += Long.parseLong(token.nextToken()) << (3 - i) * 8;
        }
        return result;
    }

    /**
     * long 转 ip字符串 转换之前请判断是否合法
     *
     * @param ip ip地址
     * @return 对应的点分字符串
     */
    public static String toDot(long ip) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            long v = 0xFF & (ip >> (24 - i * 8));
            sb.append(v).append(".");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 判断是否是公网ip
     *
     * <pre>
     * 内网地址
     * A类:10.0.0.0-10.255.255.255
     * B类:172.16.0.0-172.31.255.255
     * C类:192.168.0.0-192.168.255.255
     *
     * 保留地址
     * 0.0.0.0/8
     * A类:127.0.0.0-127.255.255.255
     * B类:169.254.0.0-169.254.255.255
     * D类:224.0.0.0-239.255.255.255
     * E类:240.0.0.0-255.255.255.255
     * </pre>
     *
     * @param ip ip值
     * @return 如果ip不在内网地址以及保留地址内 则是公网ip true 否则不是
     */
    public static boolean isPublic(long ip) {
        // 判断是否保留地址
        if (ip <= 0x00FFFFFFL || (ip >= 0x7F000000L && ip <= 0x7FFFFFFFL) || (ip >= 0xA9FE0000L && ip <= 0xA9FEFFFFL)
                || (ip >= 0xE0000000L && ip <= 0xEFFFFFFFL) || ip >= 0xF0000000L) {
            return false;
        }

        // 判断是否在内网ip段
        if ((ip >= 0x0A000000L && ip <= 0x0AFFFFFFL) || (ip >= 0xAC100000L && ip <= 0xAC1FFFFFL)
                || (ip >= 0xC0A80000L && ip <= 0xC0A8FFFFL)) {
            return false;
        }
        return true;
    }

    /**
     * @return 判断是否是公网ip 请在判断之前校验参数是否合法
     */
    public static boolean isPublic(String ip) {
        long ipLong = toLong(ip);
        return isPublic(ipLong);
    }

    /**
     * 四字节的int转为long
     *
     * @param i 数值
     * @return 转long
     */
    public static long toLong(int i) {
        return ((long) i) & 0xffffffffL;
    }

    /**
     * 将数组合并成Long
     *
     * @param arr 数组
     * @return 对应的long值
     */
    public static Long toLong(byte[] arr) {
        if (arr == null) {
            return null;
        }
        return toLong(arr, 0, arr.length);
    }

    /**
     * 将数组指定子串组合并成Long
     *
     * @param arr 数组
     * @param fromIdx 起始pos
     * @param len 长度
     * @return 对应的long值
     */
    public static Long toLong(byte[] arr, int fromIdx, int len) {
        if (arr == null || fromIdx < 0 || len < 0 || arr.length < fromIdx + len) {
            return null;
        }
        long result = 0;
        for (int i = 0; i < len; i++) {
            long v = arr[fromIdx + i] & 0x0FF;
            result += v << (len - 1 - i) * 8;
        }
        return result;
    }

    /**
     * ip转为四个字符数组
     *
     * @param ip ip地址
     * @return 定长数组
     */
    public static byte[] toArr(String ip) {
        if (!validate(ip)) {
            return null;
        }
        StringTokenizer token = new StringTokenizer(ip, ".");

        byte[] arr = new byte[4];
        for (int i = 0; i < 4; i++) {
            arr[i] = (byte) (Integer.parseInt(token.nextToken()) & 0xFF);
        }
        return arr;
    }

    /**
     * 将 long 转为 len 字节数组
     *
     * @param value 值
     * @param len 数组长度
     * @return 定长数组
     */
    public static byte[] toArr(long value, int len) {
        byte[] arr = new byte[len];
        for (int i = len - 1; i >= 0; i--) {
            arr[i] = (byte) (0xFF & (value >> ((len - 1 - i) * 8)));
        }
        return arr;
    }

    /**
     * 获取ip的前两字节对应的数值
     *
     * @param ip ip
     * @return 前缀
     */
    public static int slot(long ip) {
        return (int) (0xFFFF & (ip >> 16));
    }
}
