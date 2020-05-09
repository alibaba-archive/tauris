package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

import java.io.*;


/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("csv")
public class CSVEncoder extends AbstractEncoder {

    public static final char NULL_CHARACTER = '\0';

    @Required
    String[] fields;

    @Required
    char separator;

    char quotechar = '\0';

    char escape = '\\';

    public void init() {
    }

    @Override
    public void encode(TEvent event, String target) throws EncodeException {
        event.set(target, encode(event));
    }

    @Override
    public void encode(TEvent event, OutputStream output) throws EncodeException, IOException {
        writeEvent(event, output);
    }

    protected void writeEvent(TEvent event, OutputStream output) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            if (i != 0) {
                output.write(separator);
            }
            Object val = event.get(fields[i]);
            String strval;
            if (val == null) {
                strval = "";
            } else {
                strval = val.toString();
            }
            writeTo(strval, output);
        }
    }

    private void writeTo(String element, OutputStream output) throws IOException {
        if (element == null) {
            return;
        }
        if (quotechar != '\0') {
            output.write(quotechar);
        }
        if (stringContainsSpecialCharacters(element)) {
            processLine(element, output);
        } else {
            output.write(element.getBytes(charset));
        }
        if (quotechar != '\0') {
            output.write(quotechar);
        }
    }

    private boolean stringContainsSpecialCharacters(String line) {
        return line.indexOf(quotechar) != -1 || line.indexOf(escape) != -1;
    }

    protected void processLine(String nextElement, OutputStream output) throws IOException {
        for (int j = 0; j < nextElement.length(); j++) {
            char nextChar = nextElement.charAt(j);
            if (escape != NULL_CHARACTER && nextChar == quotechar) {
                output.write(escape);
                output.write(nextChar);
            } else if (escape != NULL_CHARACTER && nextChar == escape) {
                output.write(escape);
                output.write(nextChar);
            } else {
                output.write(nextChar);
            }
        }
    }
}
