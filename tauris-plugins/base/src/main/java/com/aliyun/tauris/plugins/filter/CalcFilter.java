package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.expression.TExpression;

/**
 * Created by ZhangLei on 16/12/8.
 */
public class CalcFilter extends BaseTFilter {

    @Required
    TExpression expression;

    @Required
    String target;

    NumberType valueType = NumberType.LONG;

    @Override
    public boolean doFilter(TEvent event) {
        if (valueType == NumberType.BOOL) {
            event.set(target, expression.check(event));
        } else {
            Number val = expression.calc(event);
            if (val != null) {
                Object v = null;
                switch (valueType) {
                    case INT:
                        v = val.intValue();
                        break;
                    case LONG:
                        v = val.longValue();
                        break;
                    case FLOAT:
                        v = val.floatValue();
                        break;
                    case DOUBLE:
                        v = val.doubleValue();
                        break;
                    case BYTE:
                        v = val.byteValue();
                        break;
                    case SHORT:
                        v = val.shortValue();
                        break;
                }
                event.set(target, v);
            }
        }
        return true;
    }

    public enum NumberType {
        INT, LONG, DOUBLE, FLOAT, BYTE, SHORT, BOOL
    }

}
