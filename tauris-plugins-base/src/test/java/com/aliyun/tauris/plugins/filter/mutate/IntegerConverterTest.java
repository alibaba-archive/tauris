package com.aliyun.tauris.plugins.filter.mutate;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 2018/1/2.
 */
public class IntegerConverterTest {

    IntegerConverter converter = new IntegerConverter();

    @Test
    public void test() {
        Assert.assertEquals(100, converter.convert("100"));
        Assert.assertEquals(-100, converter.convert("-100"));
        Assert.assertEquals(1000000000, converter.convert("1000000000"));
        Assert.assertEquals(null, converter.convert("-"));
        Assert.assertEquals(null, converter.convert("abc"));
        Assert.assertEquals(null, converter.convert("100a"));

        Assert.assertEquals(5, converter.convert(5.5));

    }
}
