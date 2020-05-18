package com.aliyun.tauris.plugins.filter.mutate;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class IntegerConverterTest {

    IntegerConverter converter = new IntegerConverter();

    @Test
    public void test() {
        Assert.assertEquals(100, converter.convert("100").get());
        Assert.assertEquals(-100, converter.convert("-100").get());
        Assert.assertEquals(1000000000, converter.convert("1000000000").get());
        Assert.assertFalse(converter.convert("-").isPresent());
        Assert.assertFalse(converter.convert("abc").isPresent());
        Assert.assertFalse(converter.convert("100a").isPresent());

        Assert.assertEquals(5, converter.convert(5.5).get());

    }
}
