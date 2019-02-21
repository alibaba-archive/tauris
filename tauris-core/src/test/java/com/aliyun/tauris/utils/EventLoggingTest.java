package com.aliyun.tauris.utils;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.utils.EventLogging;
import com.aliyun.tauris.utils.TProperty;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by ZhangLei on 17/5/26.
 */
public class EventLoggingTest {

    Logger logger = LoggerFactory.getLogger(EventLoggingTest.class);

    @Test
    public void test() throws Exception {
        EventLogging           logging = new EventLogging();
        Map<String, TProperty> props   = TProperty.getProperties(logging);
        Assert.assertFalse(props.get("name").isRequired());
        Assert.assertTrue(props.get("format").isRequired());
        Assert.assertTrue(props.get("level").isRequired());

        props.get("name").set("hello");
        props.get("format").set("hello %{name}");
        props.get("level").set("warn");

        TEvent e = new TEvent("test");
        e.setField("name", "world");
        logging.log(e);
    }

    @Test
    public void testRate() {
        EventLogging logging = new EventLogging();

        logging.writingRate = 50;

        int t = 0;
        for (int i = 0; i < 100 * 10000; i++) {
            if (logging.canWrite()) {
                t += 1;
            }
        }
        t = (t / 10000 );
        Assert.assertTrue(t >= 40 && t <= 60);


        logging.writingRate = 20;

        t = 0;
        for (int i = 0; i < 100 * 10000; i++) {
            if (logging.canWrite()) {
                t += 1;
            }
        }
        t = (t / 10000 );
        Assert.assertTrue(t >= 10 && t <= 30);

        logging.writingRate = 80;

        t = 0;
        for (int i = 0; i < 100 * 10000; i++) {
            if (logging.canWrite()) {
                t += 1;
            }
        }
        t = (t / 10000 );
        Assert.assertTrue(t >= 70 && t <= 90);


    }
}
