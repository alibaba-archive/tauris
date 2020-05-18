package com.aliyun.tauris.plugins.filter.mutate;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class LongConverterTest {

    LongConverter converter = new LongConverter();

    @Test
    public void test() {
        Assert.assertEquals(100l, converter.convert("100").get());
        Assert.assertEquals(-100l, converter.convert("-100").get());
        Assert.assertEquals(10000000000000l, converter.convert("10000000000000").get());
        Assert.assertFalse(converter.convert("-").isPresent());
        Assert.assertFalse(converter.convert("abc").isPresent());
        Assert.assertFalse(converter.convert("100a").isPresent());

        Assert.assertEquals(5l, converter.convert(5.5).get());

    }
}
