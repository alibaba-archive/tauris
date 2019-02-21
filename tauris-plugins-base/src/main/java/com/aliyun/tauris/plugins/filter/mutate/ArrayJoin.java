package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 将一个字符串数组合并成一个字符串
 *
 * Created by ZhangLei on 16/12/14.
 */
@Name("array_join")
public class ArrayJoin implements TMutate {

    @Required
    String source;

    @Required
    String target;

    @Required
    String separator;

    @Override
    public void mutate(TEvent event) {
        Object v = event.get(source);
        if (v == null) {
            return;
        }
        if (!(v.getClass().isArray()) && !(v instanceof List)) {
            throw new IllegalStateException("`" + source + "` of event is not string or list type ");
        }
        if (v.getClass().isArray()) {
            String[] ss = (String[]) v;
            event.set(target, String.join(separator, ss));
        } else {
            List ss = (List)v;
            event.set(target, String.join(separator, ss));
        }
    }

}
