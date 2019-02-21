package com.aliyun.tauris;

import com.aliyun.tauris.expression.TExpression;
import com.aliyun.tauris.metric.Counter;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Class TOutputGroup
 *
 * @author ZhangLei
 * @date 2018-09-11
 */
public class TOutputGroup extends TPluginGroup {

    private static final Counter OUTPUT_PROFILER_COUNTER = Counter.build().name("tauris_output_profiler").labelNames("id").help("output plugin used time").create().register();
    public static final String SYSPROP_OUTPUT_PROFILER = "tauris.output.profiler";

    private static Logger logger = LoggerFactory.getLogger(TOutputGroup.class);

    private List<TOutput> outputs;

    private boolean profiler;

    private String id;

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

    public List<TOutput> getOutputs() {
        return Lists.transform(outputs, OutputAdapter::new);
    }

    @Override
    public void release() {
        outputs.forEach(TPlugin::release);
    }

    private class OutputAdapter implements TOutput {

        private TOutput output;

        private boolean started;

        public OutputAdapter(TOutput output) {
            this.output = output;
        }

        @Override
        public void start() throws Exception {
            if (started) {
                throw new IllegalStateException("output " + output.id() + " is started");
            }
            output.start();
            this.started = true;
        }

        @Override
        public boolean check(TEvent event) {
            return output.check(event);
        }

        @Override
        public boolean write(TEvent event) {
            if (on == null || on.check(event)) {
                long now = System.currentTimeMillis();
                boolean b = output.write(event);
                if (profiler) {
                    OUTPUT_PROFILER_COUNTER.labels(output.id()).inc(System.currentTimeMillis() - now);
                }
                return b;
            }
            return false;
        }

        @Override
        public void stop() {
            if (!started) {
                throw new IllegalStateException("output " + output.id() + " is stopped");
            }
            output.stop();
            this.started = false;
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
