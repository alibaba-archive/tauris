package com.aliyun.tauris.plugins.codec;

import java.nio.charset.Charset;

/**
 * Class KVFlowPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-12-06
 */
public class AbstractKVFlow {

    public static final byte[] MAGIC_NUMBER = new byte[]{9, 5};
    public static final byte   VERSION      = 1;


    public static final byte TYPE_NULL   = 0;
    public static final byte TYPE_STRING = 1;
    public static final byte TYPE_INT    = 2;
    public static final byte TYPE_LONG   = 3;
    public static final byte TYPE_FLOAT  = 4;
    public static final byte TYPE_DOUBLE = 5;
    public static final byte TYPE_BOOL   = 6;
    public static final byte TYPE_OTHER  = 7;

    public static final int HEADER_LEN = 3 + 32;

    public static final int INITIAL_BODY_SIZE = 4 * 1024 * 1024;
    public static final int MAX_ELEMENT_SIZE  = 1 * 1024 * 1024;

    public static final int MAX_KEY_LEN   = 256;
    public static final int MAX_KEY_COUNT = 256;
    public static final int MAX_VALUE_LEN = 65535;

    protected Charset charset = Charset.defaultCharset();
    protected int maxElementSize = MAX_ELEMENT_SIZE;

    protected static byte typeOf(Object value) {
        if (value == null) {
            return TYPE_NULL;
        }
        if (value instanceof String) {
            return TYPE_STRING;
        } else if (value instanceof Integer) {
            return TYPE_INT;
        } else if (value instanceof Long) {
            return TYPE_LONG;
        } else if (value instanceof Boolean) {
            return TYPE_BOOL;
        } else if (value instanceof Float) {
            return TYPE_FLOAT;
        } else if (value instanceof Double) {
            return TYPE_DOUBLE;
        } else {
            return TYPE_OTHER;
        }
    }

}
