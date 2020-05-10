package com.aliyun.tauris.plugins.filter.iploc.dna;

/**
 * Created by ZhangLei on 2017/9/9.
 */
public class LineReader {

    private String line;
    int            start;
    int            end;
    private String sep = "\u0001";

    public LineReader(String line, String sep){
        if (line == null) {
            line = "";
        }
        this.line = line;
        if (sep == null) {
            sep = "\u0001";
        }
        this.sep = sep;
    }

    public String nextValue() {
        if (start > line.length()) {
            return "";
        }

        end = line.indexOf(sep, start);

        if (end == -1) {
            end = line.length();
        }

        String str = line.substring(start, end);
        start = end + 1;
        return str;
    }
}