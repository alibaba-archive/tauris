package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TLogger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;


/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("dateparse")
public class DateParse implements TMutate {

    private TLogger logger;

    @Required
    String source;

    @Required
    String target;

    String format;

    DateType type = DateType.standard;

    private DateTimeFormatter formatter;

    public void init() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        if (format != null) {
            formatter = DateTimeFormat.forPattern(format);
        }
    }

    @Override
    public void mutate(TEvent event) {
        try {
            Object value = event.get(source);
            if (value == null) return;
            DateTime date;
            if (value instanceof Date) {
                date = new DateTime(value);
            } else if (value instanceof DateTime) {
                date = (DateTime) value;
            } else if (value instanceof Long) {
                date = new DateTime(value);
            } else if (value instanceof Integer) {
                date = new DateTime(((Integer)value) * 1000L);
            } else if (value instanceof String && formatter != null) {
                date = formatter.parseDateTime((String)value);
            } else {
                logger.ERROR("cannot convert %s to date", value);
                return;
            }
            if (type == DateType.standard) {
                event.set(target, date.toDate());
            } else {
                event.set(target, date);
            }
        } catch (Exception e) {
            logger.ERROR("date parse failed", e);
        }
    }
}

