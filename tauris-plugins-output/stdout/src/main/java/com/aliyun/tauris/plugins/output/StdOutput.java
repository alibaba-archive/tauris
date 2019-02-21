package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.plugins.codec.DefaultPrinter;
import com.aliyun.tauris.TPrinter;

import java.io.*;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("stdout")
public class StdOutput extends BaseTOutput {

    private TPrinter printer = new DefaultPrinter();

    public StdOutput() {
    }

    public void init() {
        printer = printer.wrap(System.out).withCodec(codec);
    }

    public void doWrite(TEvent event) {
        try {
            printer.write(event);
            printer.flush();
        } catch (IOException | EncodeException e) {
            System.err.println(e.getMessage());
        }
    }
}
