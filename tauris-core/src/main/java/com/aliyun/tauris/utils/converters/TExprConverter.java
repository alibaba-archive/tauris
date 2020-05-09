package com.aliyun.tauris.utils.converters;

import com.alibaba.texpr.TExpression;
import com.aliyun.tauris.utils.TConverter;
import org.apache.commons.beanutils.ConversionException;

/**
 * Class TExprConverter
 *
 * @author yundun-waf-dev
 * @date 2019-12-20
 */
public class TExprConverter extends TConverter {

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
            return (T) TExpression.compile((String) value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
    }
}
