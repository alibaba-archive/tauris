package com.aliyun.tauris.plugins.filter.mutate;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 2018/1/2.
 */
public class FloatConverterTest {

    FloatConverter converter = new FloatConverter();

    @Test
    public void test() {
        Assert.assertEquals(100.1f, converter.convert("100.1"));
        Assert.assertEquals(-100.2f, converter.convert("-100.2"));
        Assert.assertEquals(1000000000.100f, converter.convert("1000000000.100"));
        Assert.assertEquals(null, converter.convert("-"));
        Assert.assertEquals(null, converter.convert("abc.100"));
        Assert.assertEquals(null, converter.convert("100a"));

        Assert.assertEquals(10.0f, converter.convert(10));

    }
}
