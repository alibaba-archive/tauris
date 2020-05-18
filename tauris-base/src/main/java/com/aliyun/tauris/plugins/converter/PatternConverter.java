package com.aliyun.tauris.plugins.converter;

import org.apache.commons.beanutils.ConversionException;

import java.util.regex.Pattern;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class PatternConverter extends AbstractConverter {

    @Override
    public Class<?> getType() {
        return Pattern.class;
    }

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new ConversionException(String.format("cannot convert %s to %s", value.getClass(), getType()));
        }
        try {
            return (T) Pattern.compile((String) value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
    }

}
