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
public class CSVPrinterBuilder implements TPrinterBuilder {

    @Required
    String[] fields;

    @Required
    char separator;

    char quotechar;

    char escape = '\\';

    String emptyValue = "";

    String delimiter = "\n";

    public CSVPrinterBuilder() {
    }

    public CSVPrinterBuilder(String[] fields, char separator, char quotechar, char escape, String delimiter, String emptyValue) {
        this.fields = fields;
        this.separator = separator;
        this.quotechar = quotechar;
        this.escape = escape;
        this.delimiter = delimiter;
        this.emptyValue = emptyValue;
    }

    @Override
    public TPrinter create(OutputStream out) {
        return new CSVPrinter(out);
    }

    public class CSVPrinter implements TPrinter {

        private CSVWriter writer;

        private String[] buffer;

        public CSVPrinter(OutputStream out) {
            writer = new CSVWriter(new OutputStreamWriter(out), separator, quotechar, escape, delimiter);
            buffer = new String[fields.length];
        }

        @Override
        public synchronized void write(TEvent event) {
            int i = 0;
            for (String field : fields) {
                Object val = event.get(field);
                if (val != null) {
                    String sval = val.toString();
                    ;
                    buffer[i] = sval.isEmpty() ? emptyValue : sval;
                } else {
                    buffer[i] = emptyValue;
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
}
