package com.aliyun.tauris.expression;

import com.aliyun.tauris.TObject;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by ZhangLei on 17/5/26.
 */
public class VariableExpression extends TExpression implements ContainerExpression {

    private String name;

    public VariableExpression(String name) {
        this.name = name;
    }

    @Override
    public boolean contains(TObject c, Object o) {
        Object val = c.get(name);
        if (val == null) {
            return false;
        }
        if (val.getClass().isArray()) {
            for (Object item : ((Object[])val)) {
                if (item.equals(o)) {
                    return true;
                }
            }
            return false;
        }
        if (val instanceof String && o instanceof String) {
            return ((String)val).contains(((String)o));
        }
        return val instanceof Collection && ((Collection) val).contains(o);
    }

    @Override
    public Object eval(TObject e) {
        if (e == null) {
            return null;
        }
        return e.get(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
