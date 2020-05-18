package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.utils.EventFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 将一个表达式的值push到一个数组中，如果数组为空则新创建一个
 *
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("array_push")
public class ArrayPush implements TMutate {

    @Required
    EventFormatter value;

    @Required
    String target;

    @Override
    public void mutate(TEvent event) {
        Object v = event.get(target);
        if (v == null) {
            v = new ArrayList<>();
        }
        if (v.getClass().isArray()) {
            Object[] ss = (Object[]) v;
            v = new ArrayList<>(Arrays.asList(ss));
        }
        if (!(v instanceof List)) {
            String clz = v.getClass().getName();
            throw new IllegalArgumentException("`" + target + "` of event is " + clz + ", not a list or array ");
        }
        String val = value.format(event);
        ((List)v).add(val);
        event.set(target, v);
    }

}
