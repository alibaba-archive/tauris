package com.aliyun.tauris.utils;

import com.aliyun.tauris.utils.MultiLineIterator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ZhangLei on 17/1/16.
 */
public class MultiLineIteratorTest {


    @Test
    public void test() {
        String text = "hello\nworl\n";
        MultiLineIterator iter = new MultiLineIterator(text.getBytes(), text.getBytes().length);

        int c = 0;
        while(iter.hasNext()) {
            System.out.print(iter.next());
            c++;
            if (c > 2) {
                Assert.assertTrue(false);
            }
        }
        Assert.assertEquals(2, c);
    }
}
