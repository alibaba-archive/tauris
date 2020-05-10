package com.aliyun.tauris.plugins.converter;

import com.aliyun.tauris.expression.TExpression;
import org.apache.commons.beanutils.ConversionException;

import java.nio.charset.Charset;

/**
 * Created by ZhangLei on 2018/5/24.
 */
public class ExpressionConverter extends AbstractConverter {

    public ExpressionConverter() {
        super();
    }

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
            return (T)TExpression.build((String) value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }

}
