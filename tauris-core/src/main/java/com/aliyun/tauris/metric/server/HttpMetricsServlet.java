package com.aliyun.tauris.metric.server;

import com.aliyun.tauris.metric.Collector;
import com.aliyun.tauris.metric.CollectorRegistry;
import com.aliyun.tauris.utils.Wildcard;
import com.google.common.net.InetAddresses;
import org.apache.commons.net.util.SubnetUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.*;

/**
 * Class MetricsServlet
 *
 * @author yundun-waf-dev
 * @date 2018-09-19
 */
public class HttpMetricsServlet extends DefaultServlet {

    private CollectorRegistry registry;

    private List<SubnetUtils.SubnetInfo> subnets = Collections.emptyList();

    private int startTime;

    public HttpMetricsServlet(CollectorRegistry registry) {
        this.registry = registry;
    }

    public void doInit() {
        startTime = (int) (System.currentTimeMillis() / 1000);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InetAddress remote = InetAddress.getByName(req.getRemoteAddr());
        if (!test(remote)) {
            resp.setStatus(403);
            return;
        }
        resp.setStatus(200);
        resp.setContentType(CONTENT_TYPE_004);

        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setIntHeader("Start-Time", startTime); //客户端用这个头部判断是否有重启
        PrintWriter                 writer = response.getWriter();
        HttpMetricsServlet.SampleFilter filter = new HttpMetricsServlet.SampleFilter(req);
        writer.write("# VERSION " + System.getProperty("tauris.version", "dev") + "\n");
        write(writer, this.registry.metricFamilySamples(), filter);
    }

    private boolean test(InetAddress remote) {
        if (remote.isLoopbackAddress()) {
            return true;
        }
        if (subnets.isEmpty()) {
            return true;
        }
        int ip = InetAddresses.coerceToInteger(remote);
        for (SubnetUtils.SubnetInfo si : subnets) {
            if (si.isInRange(ip)) {
                return true;
            }
        }
        return false;
    }

    private static void write(PrintWriter writer,
                              Enumeration<Collector.MetricFamilySamples> mfs,
                              HttpMetricsServlet.SampleFilter filter) throws IOException {
    /* See http://prometheus.io/docs/instrumenting/exposition_formats/
     * for the output format specification. */
        for (Collector.MetricFamilySamples samples : Collections.list(mfs)) {
            if (!filter.accept(samples)) {
                continue;
            }
            writer.write("# HELP " + samples.name + " " + escapeHelp(samples.help) + "\n");
            writer.write("# TYPE " + samples.name + " " + typeString(samples.type) + "\n");
            for (Collector.MetricFamilySamples.Sample sample : samples.samples) {
                if (!filter.accept(sample)) {
                    continue;
                }
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
    }

    public final static String CONTENT_TYPE_004 = "text/plain; version=0.0.4; charset=utf-8";

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

    private static class SampleFilter {

        private Set<String>                 names         = new HashSet<>();
        private Map<String, List<Wildcard>> labelPatterns = new HashMap<>();

        public SampleFilter(HttpServletRequest req) {
            for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
                if (entry.getKey().equals("filter.name")) {
                    names.addAll(Arrays.asList(entry.getValue()));
                }
                if (entry.getKey().startsWith("filter.label.")) {
                    String label = suffix(entry.getKey());
                    if (label != null) {
                        for (String labelValue : entry.getValue()) {
                            List<Wildcard> ws = labelPatterns.get(label);
                            if (ws == null) {
                                ws = new ArrayList<>();
                                labelPatterns.put(label, ws);
                            }
                            ws.add(new Wildcard(labelValue));
                        }
                    }
                }
            }

        }

        private String suffix(String str) {
            int dot = str.lastIndexOf('.');
            if (dot == str.length() - 1) {
                return null;
            }
            return str.substring(dot + 1);
        }

        public boolean accept(Collector.MetricFamilySamples samples) {
            return names.isEmpty() || names.contains(samples.name);
        }

        public boolean accept(Collector.MetricFamilySamples.Sample sample) {
            if (labelPatterns.isEmpty()) {
                return true;
            }
            for (int i = 0; i < sample.labelNames.size(); i++) {
                String labelName = sample.labelNames.get(i);
                List<Wildcard> ws = labelPatterns.get(labelName);
                if (ws != null) {
                    String labelValue = sample.labelValues.get(i);
                    boolean matched = false;
                    for (Wildcard w : ws) {
                        if (w.match(labelValue)) {
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        return false;
                    }
                }
            }
            return true;
        }

    }
}
