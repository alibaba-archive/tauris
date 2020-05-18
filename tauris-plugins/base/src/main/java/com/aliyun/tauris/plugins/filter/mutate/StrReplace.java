package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

/**
 * 将一个字符串类型的field/meta 分割为多个field/meta
 *
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("replace")
public class StrReplace implements TMutate {

    @Required
    String source;

    @Required
    String target;

    @Required
    String regex;

    @Required
    String replacement;

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
        event.set(target, s.replaceAll(regex, replacement));
    }

}
