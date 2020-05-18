package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEncoder;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Required;

import java.io.*;

/**
 * Created by ZhangLei on 16/12/11.
 */
public class DebugFilter extends BaseTFilter {

    @Required
    TEncoder encoder;

    boolean print = true;

    File output;

    private Writer writer;

    public void init() throws TPluginInitException {
        if (output != null) {
            try {
                writer = new BufferedWriter(new FileWriter(output), 81920);
            } catch (IOException e) {
                throw new TPluginInitException("io error", e);
            }
        }
    }

    @Override
    public boolean doFilter(TEvent event) {
        try {
            if (print) {
                System.out.println(encoder.encode(event));
            }
            if (output != null) {
                try {
                    writer.write(encoder.encode(event));
                } catch (IOException e) {
                }
            }
        } catch (EncodeException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }
}
