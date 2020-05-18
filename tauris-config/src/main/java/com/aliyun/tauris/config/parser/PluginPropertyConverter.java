package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.TConverter;
import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.TPluginScanner;
import org.apache.commons.beanutils.ConvertUtils;

/**
 * Class PropertyConverter
 *
 * @author yundun-waf-dev
 * @date 2020-05-17
 */
public class PluginPropertyConverter {

    public static void configure(TPluginScanner scanner) {
        for (Class<? extends TPlugin> converterClass : scanner.scanPluginClasses(TConverter.class)) {
            try {
                TConverter c = (TConverter)converterClass.newInstance();
                ConvertUtils.register(c, c.getType());
            } catch (Exception e) {
                throw new IllegalStateException("create instance of " + converterClass.getName() + "  failed, cause by " + e.getMessage());
            }
        }
    }

    public static Object convert(Object value, Class<?> targetType) {
        return ConvertUtils.convert(value, targetType);
    }
}
