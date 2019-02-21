package com.aliyun.tauris.utils;

import com.aliyun.tauris.utils.ReusableByteIO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ZhangLei on 16/12/2.
 */
public class ReusableByteIOTest {

    @Test
    public void test() throws Exception {
        ReusableByteIO bio = new ReusableByteIO(50, 10);

        Runnable writer = () -> {
            for (int i = 0 ;i < 50; i++) {
                try {
                    bio.write(RandomStringUtils.randomAlphabetic(10).getBytes(), 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        AtomicInteger c = new AtomicInteger();
        Runnable reader = () -> {
            byte[] buffer = new byte[10];
            for (int i =0 ;i < 50; i++){
                try {
                    int len = bio.read(buffer);
                    if (len > 0) {
                        Assert.assertEquals(10, len);
                        c.incrementAndGet();
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        writer.run();
        reader.run();

        Assert.assertEquals(50, c.get());
    }
}
