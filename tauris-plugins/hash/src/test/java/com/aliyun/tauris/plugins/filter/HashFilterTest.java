package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.plugins.filter.HashFilter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 2018/1/22.
 */
public class HashFilterTest {

    @Test
    public void testMd5() {
        HashFilter filter = new HashFilter();
        filter.algorithm = HashFilter.Algorithm.md5;
        filter.source = "k1";
        filter.target = "h1";
        TEvent event = new DefaultEvent("");
        event.setField("k1", "127.0.0.1");
        filter.filter(event);
        Assert.assertEquals(DigestUtils.md5Hex("127.0.0.1"), event.get("h1"));
    }

    @Test
    public void testSha1() {
        HashFilter filter = new HashFilter();
        filter.algorithm = HashFilter.Algorithm.sha1;
        filter.source = "k1";
        filter.target = "h1";
        TEvent event = new DefaultEvent("");
        event.setField("k1", "127.0.0.1");
        filter.filter(event);
        Assert.assertEquals(DigestUtils.sha1Hex("127.0.0.1"), event.get("h1"));
    }

    @Test
    public void testCode() {
        HashFilter filter = new HashFilter();
        filter.algorithm = HashFilter.Algorithm.code;
        filter.source = "k1";
        filter.target = "h1";
        TEvent event = new DefaultEvent("");
        event.setField("k1", "192.0.0.3");
        filter.filter(event);
        Assert.assertEquals(new HashCodeBuilder().append("192.0.0.3").toHashCode(), event.get("h1"));
        System.out.println(new HashCodeBuilder().append("192.0.0.3").toHashCode());
    }
}
