package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.formatter.SimpleFormatter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
                key = SimpleFormatter.build(rawKey);
            }
            if (isExprValue(rawVal)) {
                try {
                    val = SimpleFormatter.build((String) rawVal);
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
                if (key instanceof SimpleFormatter) {
                    key = ((SimpleFormatter)key).format(event);
                }
                if (val instanceof SimpleFormatter) {
                    val = ((SimpleFormatter)val).format(event);
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
