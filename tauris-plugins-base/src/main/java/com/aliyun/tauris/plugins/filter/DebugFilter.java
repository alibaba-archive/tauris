package com.aliyun.tauris.plugins.filter;

import com.alibaba.fastjson.JSON;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;

import java.io.*;

/**
 * Created by ZhangLei on 16/12/11.
 */
public class DebugFilter extends BaseTFilter {

    boolean print = true;

    File output;

    private OutputStream writer;

    public void init() throws TPluginInitException {
        if (output != null) {
            try {
                writer = new BufferedOutputStream(new FileOutputStream(output), 81920);
            } catch (IOException e) {
                throw new TPluginInitException("io error", e);
            }
        }
    }

    @Override
    public boolean doFilter(TEvent event) {
        if (print) {
            System.out.println(JSON.toJSONString(event));
        }
        if (output != null) {
            try {
                writer.write(event.getSource().getBytes());
            } catch (IOException e) {
            }
        }
        return true;
    }
}
