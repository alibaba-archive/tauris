package com.aliyun.tauris.utils;

import java.io.*;
import java.util.*;

/**
 * Created by ZhangLei on 16/11/3.
 */
public class StreamLogFormatter implements LogFormatter {

    private List<Parser> parsers;

    private List<String> columns = new ArrayList<>();

    public StreamLogFormatter(File patternFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(patternFile));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        this.parsers = buildParsers(sb.toString().trim());
        for (Parser p : parsers) {
            if (p instanceof VariableParser) {
                columns.add(((VariableParser)p).name);
            }
        }
    }

    public StreamLogFormatter(String format) {
        this.parsers = buildParsers(format);
        for (Parser p : parsers) {
            if (p instanceof VariableParser) {
                columns.add(((VariableParser)p).name);
            }
        }
    }

    public List<String> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public Map<String, String> format(String log) {
        Map<String, String> fields = new HashMap<>();
        int offset = 0;
        int i = 0;
        int length;
        while (offset < log.length() && i < parsers.size()) {
            length = parsers.get(i++).parse(log, offset, fields);
            if (offset == -1) {
                return null;
            }
            offset += length;
        }
        return fields;
    }

    private List<Parser> buildParsers(String format) {
        List<Parser> parsers = new ArrayList<>();
        int i = 0;
        while (i < format.length()) {
            char c = format.charAt(i);
            if (c == '$') {
                i = buildVariableParser(format, i + 1, parsers);
            } else {
                parsers.add(new CharParser(c));
                i++;
            }
        }
        return parsers;
    }

    private int buildVariableParser(String fmt, int offset, List<Parser> parsers) {
        StringBuilder sb = new StringBuilder();
        while (offset < fmt.length()) {
            char c = fmt.charAt(offset);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <='9') || c == '_') {
                sb.append(c);
                offset++;
            } else {
                break;
            }
        }
        parsers.add(new VariableParser(sb.toString(), fmt.charAt(offset)));
        return offset;
    }

    private interface Parser {
        int parse(String line, int offset, Map<String, String> columns);
    }

    private class CharParser implements Parser{
        private char c;

        public CharParser(char c) {
            this.c = c;
        }

        @Override
        public int parse(String text, int offset, Map<String, String> columns) {
            char c = text.charAt(offset);
            if (this.c == c) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private class VariableParser implements Parser{
        private String name;
        private char tc;

        public VariableParser(String name, char tc) {
            this.name = name;
            this.tc = tc;
        }

        @Override
        public int parse(String text, int offset, Map<String, String> columns) {
            StringBuilder sb = new StringBuilder();
            while(offset < text.length()) {
                char c = text.charAt(offset);
                if (c == this.tc) {
                    break;
                } else {
                    sb.append(c);
                }
                offset++;
            }
            columns.put(name, sb.toString());
            return sb.length();
        }
    }

}
