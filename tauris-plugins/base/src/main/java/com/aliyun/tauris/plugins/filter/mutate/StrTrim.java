package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;

/**
 * trim event的一个string field
 *
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("trim")
public class StrTrim implements TMutate {

    @Required
    String field;

    @Override
    public void mutate(TEvent event) {
        Object v = event.get(field);
        if (v == null) {
            return;
        }
        if (!(v instanceof String)) {
            throw new IllegalStateException("this field `" + field + "` of event is not string type ");
        }
        String s = (String)v;
        event.set(field, s.trim());
    }

}
