package com.aliyun.tauris.config.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangLei on 16/12/13.
 */
public class Helper {

    private int          indent  = 0;
    private List<String> message = new ArrayList<>();
    private StringBuffer buffer  = new StringBuffer();

    public static Helper m = new Helper().init();

    public Helper init() {
        indent = 0;
        return this;
    }

    public Helper expand() {
        indent++;
        return this;
    }

    public Helper expand(String text) {
        indent++;
        text(" " + text);
        return this;
    }

    public Helper collapse() {
        indent--;
        return this;
    }

    public Helper collapse(String text) {
        indent--;
        message(text);
        return this;
    }

    public Helper next() {
        message.add(trimTail(buffer.toString()));
        buffer = new StringBuffer();
        return this;
    }

    public Helper trim() {
        String line = buffer.toString();
        buffer = new StringBuffer(trimTail(line));
        return this;
    }

    public Helper message(String text) {
        return this.padding().text(text);
    }

    public Helper text(String text) {
        this.buffer.append(text);
        return this;
    }

    public Helper append(String text) {
        this.buffer.append(' ').append(text);
        return this;
    }

    private Helper padding() {
        for (int i = 0; i < indent; i++) {
            this.buffer.append('\t');
        }
        return this;
    }

    public void print() {
        for (String line: message) {
            System.out.println(line);
        }
    }

    public int getLineCount() {
        return message.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String line: message) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static String trimTail(String text) {
        StringBuilder buffer = new StringBuilder();
        boolean append = false;
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            boolean blank = (c == ' ' || c == '\n' || c == '\t');
            if (!append && blank) {
                continue;
            }
            if (!blank) {
                append = true;
            }
            buffer.insert(0, c);
        }
        return buffer.toString();
    }
}
