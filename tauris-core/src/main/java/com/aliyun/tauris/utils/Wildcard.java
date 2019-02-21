package com.aliyun.tauris.utils;

import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 16/4/27.
 */
public class Wildcard {

    private Pattern pattern;

    private String wildcard;

    public Wildcard(String wildcard) {
        this.wildcard = wildcard;
        this.pattern = wildcardToRegex(wildcard);
    }

    public boolean match(CharSequence cs) {
        return pattern.matcher(cs).matches();
    }

    @Override
    public int hashCode() {
        return wildcard.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Wildcard) {
            return this.wildcard.equals(((Wildcard)obj).wildcard);
        }
        return false;
    }

    @Override
    public String toString() {
        return wildcard;
    }

    public static boolean isWildcard(String cs) {
        return cs.contains("*") || cs.contains("?");
    }

    public static Pattern wildcardToRegex(String wildcard){
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch(c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return Pattern.compile(s.toString());
    }
}
