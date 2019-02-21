package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangLei on 16/12/14.
 */
@Name("timestamp")
public class Timestamp implements TMutate {

    @Required
    String[] fields;

    @Required
    String from;

    @Required
    String to;

    private TimeUnit _fromUnit;

    private TimeUnit _toUnit;

    public void init() throws TPluginInitException {
        _fromUnit = TimeUnit.valueOf(from);
        _toUnit = TimeUnit.valueOf(to);
        if (_fromUnit == null || _toUnit == null) {
            throw new TPluginInitException("invalid 'from' or 'to' timeunit, options is " + StringUtils.join(TimeUnit.values(), ","));
        }
    }

    @Override
    public void mutate(TEvent event) {
        for (String field : fields ) {
            Long fv = (Long)event.get(field);
            if (fv == null) {
                continue;
            }
            event.set(field, _toUnit.convert(fv, _fromUnit));
        }
    }
}
