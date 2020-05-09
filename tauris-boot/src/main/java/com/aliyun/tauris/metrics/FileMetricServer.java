package com.aliyun.tauris.metrics;

import com.aliyun.tauris.TLogger;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 将指标数据输出到文件
 * Created by ZhangLei on 16/12/8.
 */
public class FileMetricServer extends MetricServer {

    private File path;

    private int interval;

    private ScheduledExecutorService executorService;

    public FileMetricServer(File path, int interval) {
        this.path = path;
        this.interval = interval;
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public void init() {

        executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("file-metric-scheduler-pool").daemon(true).build());

    }

    @Override
    public void start()  {
        int now = (int)(System.currentTimeMillis() / 1000);
        int delay = interval - now % interval;
        executorService.scheduleAtFixedRate(() -> {
            try (FileWriter fw = new FileWriter(path)){
                write(fw, CollectorRegistry.defaultRegistry.metricFamilySamples());
            } catch (IOException e) {
                logger.ERROR("write metric failed", e);
            }
        }, delay, interval, TimeUnit.SECONDS);
    }

    private static void write(Writer writer,
                              Enumeration<Collector.MetricFamilySamples> mfs) throws IOException {
        for (Collector.MetricFamilySamples samples : Collections.list(mfs)) {
            writer.write("# HELP " + samples.name + " " + escapeHelp(samples.help) + "\n");
            writer.write("# TYPE " + samples.name + " " + typeString(samples.type) + "\n");
            for (Collector.MetricFamilySamples.Sample sample : samples.samples) {
                writer.write(sample.name);
                if (sample.labelNames.size() > 0) {
                    writer.write("{");
                    for (int i = 0; i < sample.labelNames.size(); ++i) {
                        String labelName = sample.labelNames.get(i);
                        String labelValue = sample.labelValues.get(i);
                        writer.write(String.format("%s=\"%s\",", labelName, escapeLabelValue(labelValue)));
                    }
                    writer.write("}");
                }
                writer.write(" " + Collector.doubleToGoString(sample.value) + "\n");
            }
        }
        writer.write("# END");
    }

    public void shutdown() {
        executorService.shutdownNow();
        path.deleteOnExit();
    }

    static String escapeHelp(String s) {
        return s.replace("\\", "\\\\").replace("\n", "\\n");
    }

    static String escapeLabelValue(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    static String typeString(Collector.Type t) {
        switch (t) {
            case GAUGE:
                return "gauge";
            case COUNTER:
                return "counter";
            case SUMMARY:
                return "summary";
            case HISTOGRAM:
                return "histogram";
            default:
                return "untyped";
        }
    }
}

