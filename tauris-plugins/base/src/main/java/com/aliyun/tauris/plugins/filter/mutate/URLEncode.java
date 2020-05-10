package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

/**
 *
 * Created by ZhangLei on 16/12/14.
 */
@Name("urlencode")
public class URLEncode implements TMutate {

    @Required
    String[] fields;

    private URLCodec codec = new URLCodec();

    @Override
    public void mutate(TEvent event) {
        for (String field : fields) {
            Object val = event.get(field);
            if (val == null || !(val instanceof String)) {
                return;
            }
            String s = (String) val;
            try {
                event.set(field, codec.encode(s));
            } catch (EncoderException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
