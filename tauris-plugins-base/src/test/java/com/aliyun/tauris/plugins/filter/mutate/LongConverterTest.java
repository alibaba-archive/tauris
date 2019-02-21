package com.aliyun.tauris.plugins.filter.mutate;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 2018/1/2.
 */
public class LongConverterTest {

    LongConverter converter = new LongConverter();

    @Test
    public void test() {
        Assert.assertEquals(100l, converter.convert("100"));
        Assert.assertEquals(-100l, converter.convert("-100"));
        Assert.assertEquals(10000000000000l, converter.convert("10000000000000"));
        Assert.assertEquals(null, converter.convert("-"));
        Assert.assertEquals(null, converter.convert("abc"));
        Assert.assertEquals(null, converter.convert("100a"));

        Assert.assertEquals(5l, converter.convert(5.5));

    }
}
