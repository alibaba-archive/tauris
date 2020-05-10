package com.aliyun.tauris.plugins.converter;

import org.apache.commons.beanutils.ConversionException;

import java.nio.charset.Charset;

/**
 * Created by ZhangLei on 2018/5/24.
 */
public class CharsetConverter extends AbstractConverter {

    @Override
    public Class<?> getType() {
        return Charset.class;
    }

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new ConversionException(String.format("cannot convert %s to %s", value.getClass(), getType()));
        }
        try {
            return (T)Charset.forName((String) value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
    }

}
