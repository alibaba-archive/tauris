package com.aliyun.tauris.utils;

import com.aliyun.tauris.metrics.LabelsKey;
import com.aliyun.tauris.metrics.LabelsKeyFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangLei on 16/12/2.
 */
public class LabelsKeyTest {


    @Test
    public void test() {
        LabelsKeyFactory factory = LabelsKeyFactory.getInstance();
        LabelsKey key1 = factory.makeKey("123", "456");
        LabelsKey key2 = factory.makeKey("469", "910");

        Map<LabelsKey, Long> map = new HashMap<>();
        map.put(key1, 1l);
        map.put(key2, 2l);

        System.out.println(map.get(key1));
    }
}
