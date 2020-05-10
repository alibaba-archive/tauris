package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

/**
 * 将一个字符串类型的field/meta 分割为多个field/meta
 *
 * Created by ZhangLei on 16/12/14.
 */
@Name("split")
public class StrSplit implements TMutate {

    @Required
    private String source;

    @Required
    private String target;

    @Required
    private String separator;

    @Override
    public void mutate(TEvent event) {
        Object v = event.get(source);
        if (v == null) {
            return;
        }
        if (!(v instanceof String)) {
            throw new IllegalStateException("`" + source + "` of event is not string type ");
        }
        String s = (String)v;
        String[] vs = s.split(separator);
        event.set(target, vs);
    }

}
