package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by ZhangLei on 16/12/14.
 */
@Name("dateformat")
public class DateFormat extends BaseMutate  {

    @Required
    String source;

    @Required
    String target;

    @Required
    String format;

    Long alignTo;

    private DateTimeFormatter _formatter;

    public void init() throws TPluginInitException {
        _formatter = DateTimeFormat.forPattern(format);
        if (alignTo != null) {
            if (alignTo % 60 != 0 && 60 % alignTo != 0) {
                throw new TPluginInitException("Invalid aligned_to");
            }
            alignTo = alignTo * 1000;
        }
    }

    @Override
    public void mutate(TEvent event) {
        Object val = event.get(source);
        DateTime dt = null;
        if (val instanceof DateTime) {
            dt = (DateTime)val;
        } else if (val instanceof Long) {
            dt = new DateTime(val);
        } else if (val instanceof Integer) {
            dt = new DateTime(((Integer)val) * 1000);
        }
        if (dt == null) {
            return;
        }
        if (alignTo != null) {
            long ts = dt.getMillis();
            dt = new DateTime(ts - ts % alignTo);
        }
        event.set(target, dt.toString(_formatter));

    }

}
