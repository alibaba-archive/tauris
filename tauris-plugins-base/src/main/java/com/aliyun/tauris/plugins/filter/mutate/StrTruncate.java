package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

/**
 * 截断event的一个field的值的字符串长度
 *
 * Created by ZhangLei on 16/12/14.
 */
@Name("truncate")
public class StrTruncate implements TMutate {

    @Required
    String field;

    @Required
    int length;

    @Override
    public void mutate(TEvent event) {
        Object v = event.get(field);
        if (v == null) {
            return;
        }
        if (!(v instanceof String)) {
            throw new IllegalStateException("this field `" + field + "` of event is " + v.getClass().getSimpleName() + ", cannot be truncate");
        }
        String s = (String)v;
        if (s.length() > length) {
            event.set(field, s.substring(0, length));
        }
    }

}
