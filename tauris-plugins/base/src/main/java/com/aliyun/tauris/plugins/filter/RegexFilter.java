package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.google.common.base.CaseFormat;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class RegexFilter extends BaseTFilter {

    public enum Mode {
        find, match
    }

    String source = "@source";

    File patternFile;

    String separator = "\\s";

    Pattern pattern;

    Mode mode = Mode.match;

    /**
     * 将正则中的groupname转换成underscore命名格式
     */
    boolean underscore = false;

    private Map<String, String> groupNames;

    public void init() throws TPluginInitException {
        if (patternFile == null && pattern == null) {
            throw new TPluginInitException("property pattern_file or regex is required");
        }
        try {
            if (pattern == null) {
                if (!patternFile.exists()) {
                    throw new TPluginInitException("pattern file `" + patternFile + "` not exists");
                }
                List<String> lines = IOUtils.readLines(new FileInputStream(patternFile));
                lines = lines.stream().filter(s -> !s.trim().isEmpty()).collect(Collectors.toList());
                String pattern = "^" + StringUtils.join(lines, separator) + ".*";
                this.pattern = Pattern.compile(pattern);
            }
            this.groupNames = new HashMap<>();
            Pattern px = Pattern.compile("\\?\\<([^\\>]+)>");
            Matcher m2 = px.matcher(this.pattern.toString());

            while (m2.find()) {
                String n = m2.group(1);
                groupNames.put(n, underscore ? CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, n) : n);
            }
            if (groupNames.isEmpty()) {
                throw new TPluginInitException("no group defined in regex expression");
            }
        } catch (IOException e) {
            throw new TPluginInitException("regex filter config error", e);
        }
    }

    protected boolean doFilter(TEvent e) {
        String text = (String) e.get(source);
        if (text == null) {
            return false;
        }
        Matcher m = pattern.matcher(text);
        if (mode == Mode.match) {
            if (!m.matches()) {
                return false;
            }
        } else {
            if (!m.find()) {
                return false;
            }
        }
        for (Map.Entry<String, String> c : groupNames.entrySet()) {
            String v = m.group(c.getKey());
            e.set(c.getValue(), v == null ? null : v.trim());
        }
        return true;
    }
}
