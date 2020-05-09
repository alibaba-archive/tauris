package com.aliyun.tauris.utils;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TWidget;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.formatter.EventFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by ZhangLei on 17/5/26.
 */
public class EventLogging implements TWidget {

    private Logger logger = LoggerFactory.getLogger(EventLogging.class);

    private EventFormatter formatter;

    private Method method;

    private Random random = new Random();

    /**
     * 日志输出比率: 0 - 99
     * 为0时每条日志都会输出，1-99时，会以1%到99%的几率输出日志
     */
    int writingRate;

    public void setName(String name) {
        logger = LoggerFactory.getLogger(name);
    }

    @Required
    public void setFormat(String format) {
        this.formatter = EventFormatter.build(format);
    }

    @Required
    public void setLevel(String level) {
        try {
            method = Logger.class.getMethod(level, String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("unsupported logging level:" + level);
        }
    }

    public void log(TEvent event) {
        if (!canWrite()) {
            return;
        }
        try {
            method.invoke(logger, formatter.format(event));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean canWrite() {
        if (writingRate == 0) return true;
        int mode = Math.abs(random.nextInt()) % 100;
        return (mode <= writingRate);
    }
}
