package com.aliyun.tauris.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 17/1/16.
 */
public class MatchLineReaderTest {

    @Test
    public void testInputStream() throws IOException {
        Pattern head = null;
        Pattern tail = Pattern.compile(".*;$");
        MultiLineReader reader = new MatchLineReader(getClass().getClassLoader().getResourceAsStream("buffered_read_long.txt"), null, tail);

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
