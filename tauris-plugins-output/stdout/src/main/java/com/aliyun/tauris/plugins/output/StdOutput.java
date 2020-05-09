package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPrinterBuilder;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.plugins.codec.DefaultPrinterBuilder;
import com.aliyun.tauris.TPrinter;

import java.io.*;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("stdout")
public class StdOutput extends BaseTOutput {

    TPrinterBuilder printer = new DefaultPrinterBuilder();

    private TPrinter p;

    public StdOutput() {
    }

    public void init() {
        this.p = printer.create(System.out);
    }

    public void doWrite(TEvent event) {
        try {
            p.write(event);
            p.flush();
        } catch (IOException | EncodeException e) {
            System.err.println(e.getMessage());
        }
    }
}
