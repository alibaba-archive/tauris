package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 将一个字符串类型的field/meta 分割为多个field/meta
 *
 * Created by ZhangLei on 16/12/14.
 */
@Name("reverse")
public class Reverse implements TMutate {

    @Required
    String source;

    String target;

    @Override
    public void mutate(TEvent event) {
        String tgt = target == null ? source : target;

        Object v = event.get(source);
        if (v == null) {
            return;
        }
        if (v instanceof String) {
            event.set(tgt, StringUtils.reverse((String) v));
            return;
        }

        if (v.getClass().isArray()) {
            Object[] vs = (Object[])v;
            ArrayUtils.reverse(vs);
            event.set(tgt, vs);
            return;
        }
        if (v instanceof List) {
            List vs = (List)v;
            Collections.reverse(vs);
            event.set(tgt, vs);
            return;
        }
        throw new IllegalStateException("`" + source + "` of event is " + v.getClass() + " not support reverse ");
    }

}
