package com.aliyun.tauris.config.parser;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class SimpleValue extends Value {

    abstract Object value();


    static class IntValue extends SimpleValue {

        final int value;

        public IntValue(String expr) {
            this.value = Integer.valueOf(expr);
        }

        @Override
        void _assignTo(PluginProperty property) throws Exception {
            property.set(value);
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public String toString() {
            return value+ "";
        }
    }

    static class FloatValue extends SimpleValue {

        final float value;

        public FloatValue(String expr) {
            this.value = Float.valueOf(expr);
        }

        @Override
        void _assignTo(PluginProperty property) throws Exception {
            property.set(value);
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public String toString() {
            return value + "";
        }
    }

    static class StringValue extends SimpleValue {

        final String value;

        private boolean doubleQuote;

        public StringValue(String expr) {
            if (expr.charAt(0) == '"') {
                value = StringEscapeUtils.unescapeJava(expr.substring(1, expr.length() - 1));
                doubleQuote = true;
            } else {
                value = expr.substring(1, expr.length() - 1);
            }
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        void _assignTo(PluginProperty property) throws Exception {
            property.set(value);
        }

        @Override
        public String toString() {
            if (doubleQuote) {
                return String.format("\"%s\"", StringEscapeUtils.escapeJava(value));
            } else {
                return String.format("'%s'", value);
            }
        }
    }

    static class BooleanValue extends SimpleValue {

        final boolean value;

        public BooleanValue(String expr) {
            value = "true".equals(expr);
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        void _assignTo(PluginProperty property) throws Exception {
            property.set(value);
        }

        @Override
        public String toString() {
            return value ? "true" : "false";
        }
    }

    static class NullValue extends SimpleValue {

        public NullValue() {
        }

        @Override
        public Object value() {
            return null;
        }

        @Override
        void _assignTo(PluginProperty property) throws Exception {
            property.set(null);
        }

        @Override
        public String toString() {
            return "null";
        }
    }


}
