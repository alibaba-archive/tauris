package com.aliyun.tauris.plugins.converter;

import io.tauris.expression.TExpression;
import org.apache.commons.beanutils.ConversionException;

import java.nio.charset.Charset;

/**
 * @author Ray Chaung<rockis@gmail.com>
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
            return (T)TExpression.compile((String) value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }

}
