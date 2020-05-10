package com.aliyun.tauris;

import com.aliyun.tauris.expression.TExpression;
import com.aliyun.tauris.metrics.Counter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class TOutputGroup
 *
 * @author ZhangLei
 * @date 2018-09-11
 */
public final class TOutputGroup extends TPluginGroup implements TOutput {

    private static final Counter OUTPUT_PROFILER_COUNTER = Counter.build().name("tauris_output_profiler").labelNames("id").help("output plugin used time").create().register();
    private static final String  SYSPROP_OUTPUT_PROFILER = "tauris.output.profiler";

    private List<TOutput> outputs;

    boolean profiler;

    String id;

    TExpression on;

    public TOutputGroup(List<TOutput> outputs) {
        this.outputs = outputs == null ? Collections.<TOutput>emptyList() : outputs;
        this.profiler = System.getProperty(SYSPROP_OUTPUT_PROFILER, "false").equals("true");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void start() throws Exception {
        for (TOutput o : outputs) {
            o.start();
        }
    }

    private boolean check(TEvent event) {
        if (on != null) {
            return on.check(event);
        }
        return true;
    }

    @Override
    public boolean write(TEvent event) {
        if (!check(event)) {
            return false;
        }
        for (TOutput o : outputs) {
            long now = System.currentTimeMillis();
            o.write(event);
            if (profiler) {
                OUTPUT_PROFILER_COUNTER.labels(o.id()).inc(System.currentTimeMillis() - now);
            }
        }
        return true;
    }

    @Override
    public void stop() {
        for (TOutput o : outputs) {
            o.stop();
        }
    }

    @Override
    public void release() {
        outputs.forEach(TPlugin::release);
    }

    public List<TOutput> getOutputs() {
        return outputs.stream().map(OutputDelegate::new).collect(Collectors.toList());
    }

    private class OutputDelegate implements TOutput {

        private TOutput output;

        public OutputDelegate(TOutput output) {
            this.output = output;
        }

        @Override
        public void start() throws Exception {
            output.start();
        }

        @Override
        public boolean write(TEvent event) {
            if (on == null || on.check(event)) {
                return output.write(event);
            }
            return false;
        }

        @Override
        public void stop() {
            output.stop();
        }

        @Override
        public String id() {
            return output.id();
        }

        @Override
        public void setId(String id) {
            output.setId(id);
        }

        @Override
        public void release() {
            output.release();
        }
    }
}
