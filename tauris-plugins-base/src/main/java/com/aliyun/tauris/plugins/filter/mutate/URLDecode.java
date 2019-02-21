package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.google.common.base.CaseFormat;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *
 * Created by ZhangLei on 16/12/14.
 */
@Name("urldecode")
public class URLDecode implements TMutate {

    @Required
    String[] fields;

    String charset = "UTF-8";

    @Override
    public void mutate(TEvent event) {
        for (String field: fields) {
            Object val = event.get(field);
            if (val == null || !(val instanceof String)) {
                return;
            }
            String s = (String)val;
            try {
                event.set(field, new String(URLCodec.decodeUrl(s.getBytes()), charset));
            } catch (DecoderException e) {
                //ignore
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
