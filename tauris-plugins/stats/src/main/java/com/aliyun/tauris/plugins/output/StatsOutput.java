package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.plugins.output.stats.EventPoint;
import com.aliyun.tauris.plugins.output.stats.LabelField;
import com.aliyun.tauris.plugins.output.stats.Labels;
import com.aliyun.tauris.plugins.output.stats.ValueField;
import com.google.common.util.concurrent.AtomicDouble;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.JobBuilder.*;

@Name("stats")
public class StatsOutput extends BaseTOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.stats");

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_stats_total").labelNames("id").help("stats output total").create().register();

    @Required
    File directory;

    @Required
    String filename;

    String datePattern;

    private DateTimeFormatter dateFormatter;

    @Required
    String flushCronExpr;

    /**
     * 聚合的label, 值可以是:分隔的字符串, :前是label名, :后是event的field名
     */
    @Required
    LabelField[] labelFields;

    /**
     * 输出的value, 值可以是:分隔的字符串, :前是value名, :后是event的field名
     */
    @Required
    ValueField[] valueFields;

    private volatile Map<Labels, AggregatedPoint> data = new ConcurrentHashMap<>();

    private Scheduler scheduler;

    private Lock lock = new ReentrantLock();

    public void init() throws TPluginInitException {
        if (datePattern != null) {
            dateFormatter = DateTimeFormat.forPattern(datePattern);
        }
    }

    @Override
    public void start() throws Exception {
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        scheduler = schedFact.getScheduler();
        JobDataMap data = new JobDataMap();
        data.put("instance", this);
        JobDetail job     = newJob(FlushJob.class).usingJobData(data).build();
        Trigger   trigger = newTrigger().withSchedule(cronSchedule(flushCronExpr)).build();
        scheduler.getContext().put("instance", this);
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("cannot create directory '" + directory + "'");
            }
        }
    }

    @Override
    public void doWrite(TEvent event) {
        EventPoint ep = createEventPoint(event);
        if (ep == null) {
            return;
        }
        lock.lock();
        AggregatedPoint ap = data.get(ep.getLabels());
        if (ap == null) {
            ap = new AggregatedPoint(ep.getLabels());
            data.put(ep.getLabels(), ap);
        }
        ap.incr(ep);
        lock.unlock();
    }

    private File newFile() {
        String filename = this.filename;
        if (dateFormatter != null) {
            String date = new DateTime().toString(dateFormatter);
            filename = filename.replaceAll("\\$\\{date\\}", date);
        }
        return new File(directory, filename);
    }

    public void flush() {
        if (this.data.isEmpty()) {
            return;
        }
        Map<Labels, AggregatedPoint> snapdata;
        lock.lock();
        try {
            snapdata = this.data;
            this.data = new ConcurrentHashMap<>();
        } finally {
            lock.unlock();
        }
        File file = newFile();
        try (FileOutputStream writer = new FileOutputStream(file)) {
            snapdata.forEach((tag, ap) -> {
                try {
                    TEvent e = ap.toEvent();
                    if (e == null) {
                        return;
                    }
                    codec.encode(e, writer);
                } catch (IOException e) {
                    logger.error("write stats result failed", e);
                } catch (EncodeException e) {
                    logger.error("encode stats result failed", e);
                }
            });
            OUTPUT_COUNTER.labels(id()).inc(snapdata.size());
            snapdata.clear();
        } catch (IOException e){
            logger.error("write stats result failed", e);
        }
    }

    @Override
    public void stop() {
        try {
            super.stop();
            scheduler.shutdown();
            flush();
        } catch (SchedulerException e) {
            logger.error("shutdown failed", e);
        }
    }

    private EventPoint createEventPoint(TEvent event) {
        String[] ks = new String[labelFields.length];
        for (int i = 0; i < labelFields.length; i++) {
            ks[i] = labelFields[i].labelOf(event);
        }
        Labels   tags   = new Labels(ks);
        Double[] values = new Double[valueFields.length];
        for (int i = 0; i < valueFields.length; i++) {
            Double value = valueFields[i].valueOf(event);
            if (value == null) {
                values[i] = 0.0;
            } else {
                values[i] = value;
            }
        }
        return new EventPoint(tags, values);
    }


    public static class FlushJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                ((StatsOutput) context.getJobDetail().getJobDataMap().get("instance")).flush();
            } catch (Exception e) {
                logger.error("flush exception", e);
            }
        }
    }

    private class AggregatedPoint {

        private Labels labels;

        private ConcurrentHashMap<String, AtomicDouble> values;

        public AggregatedPoint(Labels labels) {
            this.labels = labels;
            this.values = new ConcurrentHashMap<>();
            for (ValueField value : valueFields) {
                values.put(value.getValueName(), new AtomicDouble(0L));
            }
        }

        public void incr(EventPoint ep) {
            for (int i = 0; i < valueFields.length; i++) {
                String valueName = valueFields[i].getValueName();
                Double value = ep.getValues()[i];
                if (value != null) {
                    incr(valueName, value);
                }
            }
        }

        public void incr(String valueName, double value) {
            values.get(valueName).addAndGet(value);
        }

        public TEvent toEvent() {
            TEvent e = new DefaultEvent();
            for (int i = 0; i < labelFields.length; i++) {
                String key = labelFields[i].getLabel();
                Object value = labels.getKey(i);
                if (value == null) {
                    logger.warn(String.format("key %s is null", key));
                    return null;
                }
                e.setField(labelFields[i].getField(), labels.getKey(i));
            }
            for (Map.Entry<String, AtomicDouble> ex : values.entrySet()) {
                e.setField(ex.getKey(), ex.getValue().doubleValue());
            }
            return e;
        }
    }
}
