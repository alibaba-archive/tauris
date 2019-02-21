package com.aliyun.tauris.utils;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class MapperUtilTest
 *
 * @author yundun-waf-dev
 * @date 2018-12-06
 */
public class MapperUtilTest {

    @Test
    public void testInt() {
        int val = 123456789;
        byte[] bytes = new byte[4];

        MapperUtil.int2bytes(val, bytes, 0);
        Assert.assertEquals(val, MapperUtil.bytes2int(bytes, 0));


        MapperUtil.int2lbytes(val, bytes, 0);
        Assert.assertEquals(val, MapperUtil.lbytes2int(bytes, 0));
    }

    @Test
    public void testLong() {
        long val = 12345678910234l;
        byte[] bytes = new byte[8];

        MapperUtil.long2bytes(val, bytes, 0);
        Assert.assertEquals(val, MapperUtil.bytes2long(bytes, 0));


        MapperUtil.long2lbytes(val, bytes, 0);
        Assert.assertEquals(val, MapperUtil.lbytes2long(bytes, 0));
    }
}
