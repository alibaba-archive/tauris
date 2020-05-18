package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("hmac")
public class THmacSigner implements TSigner {

    public static final String AUTH_METHOD = "THmac";

    private static DateTimeFormatter RFC822_DATE_FORMAT = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z").withZone(DateTimeZone.forID("GMT")).withLocale(Locale.US);

    @Required
    String accessKey;

    @Required
    String accessSecret;

    public THmacSigner() {
    }

    public THmacSigner(String accessKey, String accessSecret) {
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
    }

    public void init() {

    }

    @Override
    public void sign(Map<String, String> headers, HttpUriRequest request, @Nullable byte[] content) {
        URI uri = request.getURI();
        if (content != null && content.length > 0) {
            headers.put("Content-MD5", Md5Crypt.md5Crypt(content));
        }
        headers.put("Date", new DateTime().toString(RFC822_DATE_FORMAT));
        String sign = makeSignature(accessSecret, request.getMethod(), headers, uri.getPath(), queryStringToParams(uri.getQuery()));
        headers.put(AUTHORIZATION_HEADER, AUTH_METHOD + " " + Base64.getEncoder().encodeToString((accessKey + ":" + sign).getBytes()));
    }

    /**
     * for client
     * @param accessSecret
     * @param method
     * @param headers
     * @param path
     * @param query
     * @return
     */
    public static String makeSignature(String accessSecret, String method, Map<String, String> headers, String path, String query) {
        return makeSignature(accessSecret, method, headers, path, queryStringToParams(query));
    }

    public static String makeSignature(String accessSecret, String method, Map<String, String> headers, String path, Map<String, String> params) {
        return caleSign(accessSecret, method, headers, path, paramsToQueryString(params));
    }

    private static String caleSign(String accessSecret, String method, Map<String, String> headers, String path, String query) {
        path = StringUtils.isEmpty(path) ? "/" : path;
        StringBuilder builder = new StringBuilder();
        builder.append(method.toLowerCase()).append("\n");
        builder.append(getMapValue(headers, "Content-MD5")).append("\n");
        builder.append(getMapValue(headers, "Date")).append("\n");
        builder.append(getCanonicalizedHeaders(headers)).append("\n");
        builder.append(query == null ? path : path + "?" + query);
        return makeSignature(accessSecret, builder.toString());
    }

    public static String makeSignature(String accessSecret, String data) {
        try {
            byte[] e = accessSecret.getBytes("UTF-8");
            byte[] dataBytes = data.getBytes("UTF-8");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(e, "HmacSHA1"));
            return new String(encodeBase64(mac.doFinal(dataBytes)));
        } catch (UnsupportedEncodingException var6) {
            throw new RuntimeException("Not Supported encoding method UTF-8", var6);
        } catch (NoSuchAlgorithmException var7) {
            throw new RuntimeException("Not Supported signature method hmac-sha1", var7);
        } catch (InvalidKeyException var8) {
            throw new RuntimeException("Failed to calcuate the signature", var8);
        }
    }

    private static String getCanonicalizedHeaders(Map<String, String> headers) {
        TreeMap<String, String> treeMap = new TreeMap<>(headers);
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        Iterator iter = treeMap.entrySet().iterator();

        while(true) {
            Map.Entry entry;
            do {
                if(!iter.hasNext()) {
                    return builder.toString();
                }
                entry = (Map.Entry)iter.next();
            } while(!((String)entry.getKey()).startsWith("x-"));

            if(isFirst) {
                isFirst = false;
            } else {
                builder.append("\n");
            }

            builder.append((String)entry.getKey()).append(":").append((String)entry.getValue());
        }
    }

    private static String getMapValue(Map<String, String> map, String key) {
        return map.containsKey(key)?(String)map.get(key):"";
    }

    private static String paramsToQueryString(Map<String, String> p) {
        TreeMap<String, String> params = new TreeMap<>(p);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e: params.entrySet()) {
            if (!first) {
                sb.append('&');
            }
            try {
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                //ignore
            }
            first = false;
        }
        return sb.toString();
    }

    private static Map<String, String> queryStringToParams(String queryString) {
        TreeMap<String, String> params = new TreeMap<>();
        if (queryString == null) {
            return params;
        }
        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            try {
                final int idx = pair.indexOf("=");
                final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                params.put(key, value);
            } catch (UnsupportedEncodingException e) {
                //ignore
            }
        }
        return params;
    }

    private static String buildUrlParameter(Map<String, String> paras) {
        TreeMap<String, String> treeMap = new TreeMap<>(paras);
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;

        Map.Entry entry;
        for(Iterator var5 = treeMap.entrySet().iterator(); var5.hasNext(); builder.append((String)entry.getKey()).append("=").append((String)entry.getValue())) {
            entry = (Map.Entry)var5.next();
            if(isFirst) {
                isFirst = false;
            } else {
                builder.append("&");
            }
        }

        return builder.toString();
    }
}
