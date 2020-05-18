package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Required;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class DateConverter implements TFieldConverter {

    private static Logger LOG = LoggerFactory.getLogger(DateConverter.class);

    @Required
    String[] fields;

    String format;

    DateType type = DateType.joda;

    private DateTimeFormatter _formatter;

    public void init() {
        if (format != null) {
            _formatter = DateTimeFormat.forPattern(format);
        }
    }

    public void convert(TEvent event) {
        for (String field : fields) {
            Object value = event.get(field);
            if (value != null) {
                try {
                    value = convert(value);
                    event.set(field, value);
                } catch (Exception ex) {
                    event.remove(field);
                }
            }
        }
    }

    protected Object convert(Object value) {
        if (value == null) return null;
        DateTime date;
        if (value instanceof DateTime) {
            date = (DateTime)value;
        } else if (value instanceof Long) {
            date = new DateTime(value);
        } else if (value instanceof Integer) {
            date = new DateTime(((Integer)value) * 1000l);
        } else if (value instanceof String && _formatter != null) {
            date = _formatter.parseDateTime((String)value);
        } else {
            LOG.error(String.format("cannot convert %s to date", value));
            return null;
        }
        if (type == DateType.standard) {
            return date.toDate();
        } else {
            return date;
        }
    }

}
