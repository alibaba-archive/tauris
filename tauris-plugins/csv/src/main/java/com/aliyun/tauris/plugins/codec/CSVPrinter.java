package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.opencsv.CSVWriter;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;

/**
 * Class DefaultPrinter
 *
 * @author yundun-waf-dev
 * @date 2018-11-16
 */
@NotThreadSafe
@Name("csv")
public class CSVPrinter implements TPrinter {

    @Required
    String[] fields;

    @Required
    char separator;

    char quotechar;

    char escape = '\\';

    String delimiter = "\n";

    private CSVWriter writer;

    private String[] buffer;

    public CSVPrinter() {
    }

    public CSVPrinter(String[] fields, char separator, char quotechar, char escape, String delimiter) {
        this.fields = fields;
        this.separator = separator;
        this.quotechar = quotechar;
        this.escape = escape;
        this.delimiter = delimiter;
    }

    @Override
    public TPrinter wrap(OutputStream out) {
        CSVPrinter printer = new CSVPrinter(fields, separator, quotechar, escape, delimiter);
        printer.writer = new CSVWriter(new OutputStreamWriter(out), separator, quotechar, escape, delimiter);
        printer.buffer = new String[fields.length];
        return printer;
    }

    @Override
    public TPrinter withCodec(TEncoder codec) {
        return this;
    }

    @Override
    public void write(TEvent event) throws IOException, EncodeException {
        int i = 0;
        for (String field: fields) {
            Object val = event.get(field);
            if (val != null) {
                buffer[i] = val.toString();
            } else {
                buffer[i] = "";
            }
            i++;
        }
        writer.writeNext(buffer);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
