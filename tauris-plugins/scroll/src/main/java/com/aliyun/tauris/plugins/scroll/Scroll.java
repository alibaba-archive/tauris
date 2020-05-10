package com.aliyun.tauris.plugins.scroll;

/**
 * +--------+-------+----------+-----------------+      +------+----------------+
 * | HDR1   |PAD    | HDR Len  | GAP  |Timestamp |
 * | 0x0102 |0x0000 | 0x0000   | 0x13 |8 bits    |
 * +------+--------+------+----------------+      +------+----------------+
 * Created by ZhangLei on 16/12/8.
 */
public interface Scroll {

    byte[] MAGIC_NUMBER = new byte[]{1, 2};

    int MAX_HEADER_LEN = 1024;

    int HEADER_PADDING_BYTES = 2;

    int MAGIC_PADDING_BYTES = MAGIC_NUMBER.length + HEADER_PADDING_BYTES;

    int HEADER_LEN_BYTES = 2;

    /**
     * 最小header长度, 2字节magic, 2字节padding, 2字节 header len
     */
    int MIN_HEADER_LEN   = MAGIC_PADDING_BYTES + HEADER_LEN_BYTES;

    int BODY_LEN_BYTES = 4;

    /**
     * 10M
     */
    int MAX_BODY_LEN = 10 * 1024 * 1024;

    byte SCROLL_GAP       = 0x00;
    /**
     * 16
     */
    byte SCROLL_VERSION   = 0x10;
    /**
     * 17
     */
    byte SCROLL_HOSTNAME  = 0x11;
    /**
     * 18
     */
    byte SCROLL_APPNAME   = 0x12;
    byte SCROLL_TIMESTAMP = 0x13;
    byte SCROLL_TOKEN     = 0x14;
    byte SCROLL_COMPRESS  = 0x20;

}
