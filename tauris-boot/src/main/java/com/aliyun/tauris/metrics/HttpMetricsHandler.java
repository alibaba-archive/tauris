package com.aliyun.tauris.metrics;

import com.aliyun.tauris.metrics.Collector;
import com.aliyun.tauris.metrics.CollectorRegistry;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Class HttpMetricsHandler
 *
 * @author yundun-waf-dev
 * @date 2018-09-19
 */
public class HttpMetricsHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private CollectorRegistry registry;

    private String path;
    private int startTime;

    public HttpMetricsHandler(CollectorRegistry registry, String path) {
        this.registry = registry;
        this.path = path;
        this.startTime = (int) (System.currentTimeMillis() / 1000);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (!req.method().name().equalsIgnoreCase("get") || !req.uri().equals(this.path)) {
            DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            ctx.writeAndFlush(response).channel().close();
        } else {
            String metric = build();
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(metric.getBytes())); // 2

            HttpHeaders heads = response.headers();
            heads.add(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
            heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            heads.addInt("Start-Time", startTime);
            ctx.writeAndFlush(response).channel().close();
        }
    }

    private String build() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# VERSION " + System.getProperty("tauris.version", "dev")).append("\n");
        write(sb, this.registry.metricFamilySamples());
        return sb.toString();
    }

    private static void write(StringBuilder writer,
                              Enumeration<Collector.MetricFamilySamples> mfs) throws IOException {
    /* See http://prometheus.io/docs/instrumenting/exposition_formats/
     * for the output format specification. */
        for (Collector.MetricFamilySamples samples : Collections.list(mfs)) {
            writer.append("# HELP " + samples.name + " " + escapeHelp(samples.help) + "\n");
            writer.append("# TYPE " + samples.name + " " + typeString(samples.type) + "\n");
            for (Collector.MetricFamilySamples.Sample sample : samples.samples) {
                writer.append(sample.name);
                if (sample.labelNames.size() > 0) {
                    writer.append("{");
                    for (int i = 0; i < sample.labelNames.size(); ++i) {
                        String labelName = sample.labelNames.get(i);
                        String labelValue = sample.labelValues.get(i);
                        writer.append(String.format("%s=\"%s\",", labelName, escapeLabelValue(labelValue)));
                    }
                    writer.append("}");
                }
                writer.append(" " + Collector.doubleToGoString(sample.value) + "\n");
            }
        }
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush(); // 4
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
}
