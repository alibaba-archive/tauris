package com.aliyun.tauris.plugins.codec;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * Influxdb line Decoder
 * Created by ZhangLei on 16/12/7.
 */
public class InfluxdbDecoder extends AbstractDecoder {

    final static DateTimeFormatter RFC3339_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    public enum Precision {
        /**
         * RFC 3339 Date and Time on the Internet
         */
        rfc3339(RFC3339_FORMATTER::parseDateTime),
        /**
         * Hour
         */
        hour((t) -> new DateTime(TimeUnit.HOURS.toMillis(Long.parseLong(t)))),
        /**
         * minute
         */
        minute((t) -> new DateTime(TimeUnit.MINUTES.toMillis(Long.parseLong(t)))),
        /**
         * second
         */
        second((t) -> new DateTime(TimeUnit.SECONDS.toMillis(Long.parseLong(t)))),
        /**
         * millisecond
         */
        millisecond((t) -> new DateTime(Long.parseLong(t))),
        /**
         * microsecond
         */
        microsecond((t) -> new DateTime(TimeUnit.MICROSECONDS.toMillis(Long.parseLong(t)))),
        /**
         * nanosecond
         */
        nanosecond((t) -> new DateTime(TimeUnit.NANOSECONDS.toMillis(Long.parseLong(t))));

        Function<String, DateTime> parser;

        Precision(Function<String, DateTime> parser) {
            this.parser = parser;
        }

        DateTime parse(String time) {
            return this.parser.apply(time);
        }
    }

    Precision precision = Precision.nanosecond;

    @Override
    public TEvent decode(String source) throws DecodeException {
        //        warehouse,warehouse=et1 http=423.0,http_ssl=55.0,s2xx=445.0,s3xx=10.0,s4xx=19.0 1494903614000000000
        if (source == null || source.trim().isEmpty()) {
            throw new DecodeException("source is null");
        }
        TEvent event = new TEvent(source);
        decode(source, event, null);
        return event;
    }

    @Override
    public void decode(String source, TEvent event, String target) throws DecodeException {
        if (target != null) {
            event.set(target, source);
        }
        String[] segs = source.split(" ");
        if (segs.length != 3) {
            throw new DecodeException("invalid influxdb format",  source);
        }
        try {
            String[] fs = segs[0].split(",");
            String measurement = fs[0];
            event.addMeta("measurement", measurement);

            Map<String, String> tags = new HashMap<>();
            for (int i = 1; i < fs.length; i++) {
                String[] tv = fs[i].split("=");
                tags.put(tv[0], tv[1]);
            }
            event.addMeta("tags", tags);

            String[] vs = segs[1].split(",");
            for (String v : vs) {
                String[] tv = v.split("=");
                event.setField(tv[0], Double.parseDouble(tv[1]));
            }

            DateTime timestamp = precision.parse(segs[2]);
            event.setTimestamp(timestamp);
        } catch (Exception e) {
            throw new DecodeException("invalid influxdb format", source);
        }
    }

}
