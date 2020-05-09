package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import org.junit.Test;

/**
 * Created by ZhangLei on 17/7/27.
 */
public class RandomFilterTest {


    @Test
    public void testNumeric() {
        RandomFilter rf = new RandomFilter();
        rf.type = RandomFilter.Type.integer;
        rf.target = "ok";
        rf.init();
        TEvent e = new DefaultEvent("");
        for (int i = 0; i < 100; i++) {
            rf.filter(e);
            System.out.println(e.get("ok"));
        }
    }

}
