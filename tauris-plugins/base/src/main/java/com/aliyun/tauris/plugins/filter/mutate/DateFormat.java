package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("dateformat")
public class DateFormat extends BaseMutate {

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
        Object val    = event.get(source);
        Long   millis = null;
        if (val instanceof Date) {
            millis = ((Date) val).getTime();
        } else if (val instanceof DateTime) {
            millis = ((DateTime) val).getMillis();
        } else if (val instanceof Long) {
            millis = (Long) val;
        } else if (val instanceof Integer) {
            millis = ((Integer) val) * 1000L;
        }
        if (millis == null) {
            return;
        }
        if (alignTo != null) {
            millis = millis - millis % alignTo;
        }
        event.set(target, new DateTime(millis).toString(_formatter));

    }

}
