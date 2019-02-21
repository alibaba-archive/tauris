package com.aliyun.tauris.expression;

import com.aliyun.tauris.utils.TConverter;
import org.apache.commons.beanutils.ConversionException;

/**
 * Created by ZhangLei on 2018/5/11.
 */
public class Converter extends TConverter {

    @Override
    public Class<?> getType() {
        return TExpression.class;
    }

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new ConversionException(String.format("cannot convert %s to %s", value.getClass(), getType()));
        }
        try {
            return (T) TExpression.build((String) value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
    }
}
