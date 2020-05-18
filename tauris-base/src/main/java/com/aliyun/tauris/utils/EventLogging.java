package com.aliyun.tauris.utils;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class EventLogging implements TPlugin {

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
