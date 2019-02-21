package com.aliyun.tauris;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 2018/4/13.
 */
public class TResourceURI {

    private URI uri;

    private Map<String, String> params = new LinkedHashMap<>();

    private TResourceURI(URI uri) {
        this.uri = uri;
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            try {
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        params.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                }
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public String getScheme() {
        return uri.getScheme();
    }

    public Integer getIntegerParam(String name) {
        String value = getParam(name);
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean getBoolParam(String name) {
        String value = getParam(name);
        return "true".equals(value);
    }

    public String getHost() {
        return uri.getHost();
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getParam(String name) {
        return params.get(name);
    }

    public URI toURI() {
        return URI.create(toString());
    }

    public String toString() {
        String s = uri.toString();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("__") && key.endsWith("__")) {
                try {
                    String pair = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
                    s = s.replace(pair, "");
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        if (s.endsWith("?")) {
            s = s.substring(0, s.length() - 1);
        }
        return URI.create(s).toString();
    }

    public static TResourceURI valueOf(String u) {
        Pattern p = Pattern.compile("^[a-z]+://.*");
        Matcher m = p.matcher(u);
        if (!m.matches()) {
            u = "file://" + u;
        }
        URI uri = URI.create(u);
        return new TResourceURI(uri);
    }
}
