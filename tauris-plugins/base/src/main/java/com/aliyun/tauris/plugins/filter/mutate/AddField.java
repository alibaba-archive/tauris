package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.utils.EventFormatter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ZhangLei on 16/12/14.
 */
public class AddField extends BaseMutate {

    @Required
    Map<String, Object> newFields;

    boolean formatKey;

    private Map<Object, Object> fields = new HashMap<>();

    public void init() {
        Set<String> keySet = new HashSet<>(newFields.keySet());
        for (String rawKey : keySet) {
            Object key = rawKey;
            Object rawVal = newFields.get(rawKey);
            Object val = rawVal;

            if (formatKey && isExprValue(key)) {
                key = EventFormatter.build(rawKey);
            }
            if (isExprValue(rawVal)) {
                try {
                    val = EventFormatter.build((String) rawVal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.fields.put(key, val);
        }
    }

    @Override
    public void mutate(TEvent event) {
        if (test(event)) {
            for (Map.Entry<Object, Object> entry : fields.entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();
                if (key instanceof EventFormatter) {
                    key = ((EventFormatter)key).format(event);
                }
                if (val instanceof EventFormatter) {
                    val = ((EventFormatter)val).format(event);
                }
                try {
                    event.set((String)key, val);
                } catch (NullPointerException e) {
                }
            }
        }
    }

    private boolean isExprValue(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        String str = (String) value;
        return str.contains("%{");
    }

}
