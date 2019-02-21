package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 *
 * Created by ZhangLei on 16/12/14.
 */
@Name("substring")
public class SubString implements TMutate {

    String source = "@source";
    String target = "@source";

    Integer beginIndex;
    Integer endIndex;

    String beginChars;
    String endChars;
    Integer maxStrlen;
    boolean matchEnd = false;

    private Set<Character> _beginChars;
    private Set<Character> _endChars;

    public void init() throws TPluginInitException {
        if (beginChars != null) {
            _beginChars = Sets.newHashSet();
            for (char b : beginChars.toCharArray()) {
                _beginChars.add(b);
            }
        }
        if (endChars != null) {
            _endChars = Sets.newHashSet();
            for (char b : endChars.toCharArray()) {
                _endChars.add(b);
            }
        }
        if (beginIndex == null && beginChars == null) {
            throw new TPluginInitException("missing property begin_index or begin_chars");
        }
        if (beginChars != null && maxStrlen == null) {
            throw new TPluginInitException("property max_strlen is required");
        }
    }
    @Override
    public void mutate(TEvent event) {
        Object v = event.get(source);
        if (v == null) {
            return;
        }
        if (!(v instanceof String)) {
            throw new IllegalStateException("this `" + source + "` of event is not string type ");
        }
        String s = (String)v;
        String ss = null;
        if (beginIndex != null) {
            ss = getSubStringByIndex(s);
        } else {
            ss = getSubStringByChar(s);
        }
        if (ss != null) {
            event.set(target, ss);
        }
    }

    private String getSubStringByIndex(String s) {
        if (beginIndex >= s.length()) {
            return null;
        }
        if (endIndex == null) {
            return s.substring(beginIndex);
        } else {
            int ei = endIndex;
            if (endIndex > s.length()) {
                ei = s.length();
            }
            return s.substring(beginIndex, ei);
        }
    }

    private String getSubStringByChar(String s) {
        char[] buff = new char[maxStrlen];
        int cursor = 0;
        int end = 0;
        if (matchEnd) {
            if (_endChars == null) {
                for (char b : s.toCharArray()) {
                    if (_beginChars.contains(b)) {
                        cursor = 0;
                        continue;
                    }
                    buff[cursor++] = b;
                }
                return new String(buff, 0, cursor);
            } else {
                for (char b : s.toCharArray()) {
                    if (_beginChars.contains(b)) {
                        cursor = 0;
                    } else if (_endChars.contains(b)) {
                        end = cursor;
                    } else {
                        buff[cursor++] = b;
                    }
                }
                return new String(buff, 0, end == 0 ? cursor : end);
            }
        } else {
            boolean start = false;
            for (char b : s.toCharArray()) {
                if (!start) {
                    if (_beginChars.contains(b)) {
                        start = true;
                    }
                } else {
                    if (_endChars == null) {
                        buff[cursor] = b;
                    } else {
                        if (_endChars.contains(b)) {
                            return new String(buff, 0, cursor);
                        } else {
                            buff[cursor++] = b;
                        }
                    }
                }
            }
            return new String(buff, 0, cursor);
        }
    }

}
