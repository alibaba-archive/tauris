package com.aliyun.tauris.utils;

import com.aliyun.tauris.TEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 17/1/7.
 */
public class EventFormatter {

    private static Map<String, String>                TERM_PATTERN = new LinkedHashMap<>();
    private static Map<String, Class<? extends Term>> TERM_CLASSES = new HashMap<>();
    private static Pattern p;

    static {
        TERM_CLASSES.put("f", FieldTerm.class);
        TERM_CLASSES.put("t", DateTimeTerm.class);
        TERM_CLASSES.put("m", MetadataTerm.class);
        TERM_CLASSES.put("e", EnvironTerm.class);
    }

    static {
        TERM_PATTERN.put("f", "[a-z][a-zA-Z\\._\\-\\d]+(\\?[^}]+)?"); // %{fieldname}
        TERM_PATTERN.put("t", "\\+[^\\}]+");        //%{+yyyy-MM-dd}
        TERM_PATTERN.put("m", "@[a-zA-Z_\\d\\.]+(\\?[^}]+)?");    // %{@meta}
        TERM_PATTERN.put("e", "![a-zA-Z_\\d\\.]+(\\?[^}]+)?"); // %{!nodename}
        List<String> ts = new ArrayList<>();
        TERM_PATTERN.forEach((k, v) -> {
            ts.add(String.format("(?<%s>\\s*%s\\s*)", k, v));
        });
        p = Pattern.compile(String.format("%%\\{%s\\}", String.join("|", ts)));
    }

    private String expression;

    private List<Term> terms = new ArrayList<>();

    public EventFormatter() {
    }

    public EventFormatter(String expression) {
        this.expression = expression;
    }

    public void init() {
        this.terms.clear();
        Matcher matcher = p.matcher(expression);
        int     cursor  = 0;
        while (matcher.find()) {
            for (Map.Entry<String, Class<? extends Term>> entry : TERM_CLASSES.entrySet()) {
                String t = entry.getKey();
                String v = matcher.group(t);
                if (v != null) {
                    Class<? extends Term> c = entry.getValue();
                    try {
                        this.terms.add(new TextTerm(expression.substring(cursor, matcher.start(t) - 2)));
                        this.terms.add(c.getConstructor(String.class).newInstance(v));
                    } catch (Exception e) {
                        throw new IllegalStateException("invalid expression: '" + expression + "'");
                    }
                    cursor = matcher.end(t) + 1;
                }
            }
            ;
        }
        if (cursor < expression.length()) {
            this.terms.add(new TextTerm(expression.substring(cursor)));
        }
    }

    public static EventFormatter build(String expr) {
        try {
            EventFormatter formatter = new EventFormatter(expr);
            formatter.init();
            return formatter;
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getExpression() {
        return expression;
    }

    public String format() {
        return format(null);
    }

    public String format(TEvent e) {
        StringBuilder sb = new StringBuilder();
        for (Term t : terms) {
            String v = t.parse(e);
            if (v != null) {
                sb.append(v);
            }
        }
        return StringEscapeUtils.unescapeJava(sb.toString());
    }

    interface Term {
        String parse(TEvent e);
    }

    static class TextTerm implements Term {

        private String text;

        public TextTerm(String text) {
            this.text = text;
        }

        @Override
        public String parse(TEvent e) {
            return text;
        }
    }

    static class DateTimeTerm implements Term {

        private DateTimeFormatter sdf;

        public DateTimeTerm(String format) {
            sdf = DateTimeFormat.forPattern(format.trim().substring(1));
        }

        @Override
        public String parse(TEvent e) {
            if (e != null) {
                return new DateTime(e.getTimestamp()).toString(sdf);
            } else {
                return new DateTime().toString(sdf);
            }
        }
    }

    static class MetadataTerm implements Term {

        private String metaname;

        private String defaultValue;

        public MetadataTerm(String metaname) {
            if (metaname.contains("?")) {
                int q = metaname.lastIndexOf("?");
                defaultValue = metaname.substring(q + 1);
                metaname = metaname.substring(0, q);
            }
            this.metaname = metaname.trim().substring(1);
        }

        @Override
        public String parse(TEvent e) {
            Object meta = e.getMeta(metaname);
            return meta == null ? defaultValue : meta.toString();
        }
    }

    static class EnvironTerm implements Term {

        private String value;

        public EnvironTerm(String env) {
            String defaultValue = null;
            if (env.contains("?")) {
                int q = env.lastIndexOf("?");
                defaultValue = env.substring(q + 1);
                env = env.substring(0, q);
            }
            env = env.substring(1);
            value = System.getenv().get(env);
            if (value == null) {
                value = System.getProperty(env);
            }
            if (value == null) {
                value = defaultValue;
            }
        }

        @Override
        public String parse(TEvent e) {
            return value;
        }
    }

    static class FieldTerm implements Term {

        private String[] fieldNameChain;

        private String defaultValue;

        public FieldTerm(String fieldNameChain) {
            if (fieldNameChain.contains("?")) {
                int q = fieldNameChain.lastIndexOf("?");
                defaultValue = fieldNameChain.substring(q + 1);
                fieldNameChain = fieldNameChain.substring(0, q);
            }
            this.fieldNameChain = fieldNameChain.trim().split("\\.");
        }

        @Override
        public String parse(TEvent e) {
            Map<String, Object> valMap = e.getFields();
            Object              val    = null;
            for (int i = 0; i < fieldNameChain.length; i++) {
                String fn = fieldNameChain[i];
                boolean isLast = i == fieldNameChain.length - 1;
                val = valMap.get(fn);
                if (val == null) {
                    break;
                }
                if (val instanceof Map) {
                    valMap = (Map<String, Object>) val;
                } else {
                    if (!isLast) {
                        val = null;
                    }
                    break;
                }
            }
            return val == null ? defaultValue : val.toString();
        }
    }

    public static boolean isExpr(Object value) {
        if (!(value instanceof String)) return false;
        String str = (String) value;
        return str.contains("%{");
    }
}
