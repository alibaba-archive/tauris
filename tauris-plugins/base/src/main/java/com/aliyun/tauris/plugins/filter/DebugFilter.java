package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.*;
import com.aliyun.tauris.plugins.codec.DefaultPrinter;
import com.aliyun.tauris.plugins.codec.PlainEncoder;

import java.io.*;

/**
 * Created by ZhangLei on 16/12/11.
 */
public class DebugFilter extends BaseTFilter {

    boolean print = true;

    File output;

    TPrinter printer = new DefaultPrinter();
    TEncoder codec   = new PlainEncoder();

    public void init() throws TPluginInitException {
        if (output != null) {
            try {
                printer = printer.withCodec(codec).wrap(new FileOutputStream(output, true));
            } catch (IOException e) {
                throw new TPluginInitException("io error", e);
            }
        }
    }

    @Override
    public boolean doFilter(TEvent event) {
        try {
            if (print) {
                System.out.println(codec.encode(event));
            }
            if (output != null) {
                try {
                    printer.write(event);
                } catch (IOException e) {
                }
            }
        } catch (EncodeException e) {
            e.printStackTrace();
        }
        return true;
    }
}
