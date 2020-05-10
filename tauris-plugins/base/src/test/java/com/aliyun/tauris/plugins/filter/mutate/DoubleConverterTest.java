package com.aliyun.tauris.plugins.filter.mutate;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 2018/1/2.
 */
public class DoubleConverterTest {

    DoubleConverter converter = new DoubleConverter();

    @Test
    public void test() {
        Assert.assertEquals(100.1, converter.convert("100.1").get());
        Assert.assertEquals(-100.2, converter.convert("-100.2").get());
        Assert.assertEquals((double) 1000000000, converter.convert("1000000000").get());
        Assert.assertFalse(converter.convert("-").isPresent());
        Assert.assertFalse(converter.convert("abc.100").isPresent());
        Assert.assertFalse(converter.convert("100a").isPresent());

        Assert.assertEquals(10.0, converter.convert(10).get());

    }
}
