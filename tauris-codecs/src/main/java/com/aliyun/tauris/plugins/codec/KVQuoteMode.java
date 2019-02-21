package com.aliyun.tauris.plugins.codec;


import org.apache.commons.lang3.StringEscapeUtils;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by ZhangLei on 16/12/7.
 */
public enum KVQuoteMode {
    none('\0'), escape('!'), base64('?');

    final char markChar;

    KVQuoteMode(char markChar) {
        this.markChar = markChar;
    }

    String quote(String src, Charset charset) {
        switch (this) {
            case base64:
                return Base64.getEncoder().encodeToString(src.getBytes(charset));
            case escape:
                return StringEscapeUtils.ESCAPE_JAVA.translate(src);
            default:
                return src;
        }
    }

    String unquote(String src, Charset charset) {
        switch (this) {
            case base64:
                return new String(Base64.getDecoder().decode(src), charset);
            case escape:
                return StringEscapeUtils.UNESCAPE_JAVA.translate(src);
            default:
                return src;
        }
    }

}
