package com.aliyun.tauris.plugins.converter;

import com.aliyun.tauris.utils.TConverter;
import org.apache.commons.beanutils.ConversionException;

import javax.mail.internet.InternetAddress;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 2018/5/11.
 */
public class InternetAddressConverter extends TConverter {

    @Override
    public Class<?> getType() {
        return InternetAddress.class;
    }

    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (!(value instanceof String)) {
            throw new ConversionException(String.format("cannot convert %s to %s", value.getClass(), getType()));
        }
        try {
            return (T) new InternetAddress((String)value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
    }

}
