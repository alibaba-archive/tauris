package com.aliyun.tauris.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by ZhangLei on 17/1/16.
 */
public class MultiLineReaderTest {


    @Test
    public void testString() throws IOException {
        String text = "hello\nworld\n";
        ByteArrayInputStream bis = new ByteArrayInputStream(text.getBytes());
        MultiLineReader reader = new MultiLineReader(bis);

        String line;
        while(true) {
            line = reader.read();
            if (line == null) {
                break;
            }
            System.out.println(line);
        }
    }

    @Test
    public void testInputStream() throws IOException {
        MultiLineReader reader = new MultiLineReader(getClass().getClassLoader().getResourceAsStream("buffered_read_long.txt"));

        int lineCount = 0;
        String line;
        while(true) {
            line = reader.read();
            if (line == null) {
                break;
            }
            lineCount++;
        }
        Assert.assertEquals(5, lineCount);
    }
}
