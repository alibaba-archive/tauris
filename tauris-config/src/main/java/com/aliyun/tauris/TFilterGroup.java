package com.aliyun.tauris;

import com.aliyun.tauris.expression.TExpression;
import com.aliyun.tauris.metrics.Counter;

import java.util.List;

/**
 * Class TFilterChain
 *
 * @author ZhangLei
 * @date 2018-09-11
 */
public class TFilterGroup extends TPluginGroup {

    private static final Counter FILTER_PROFILER_COUNTER = Counter.build().name("tauris_filter_profiler").labelNames("id").help("filter used time").create().register();
    public static final String SYSPROP_FILTER_PROFILER = "tauris.filter.profiler";

    private TLogger logger;

    private List<TFilter> filters;

    private String id;

    private boolean profiler;

    TExpression on;

    public TFilterGroup(List<TFilter> filters) {
        this.filters = filters;
        this.profiler = System.getProperty(SYSPROP_FILTER_PROFILER, "false").equals("true");
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public TEvent filter(TEvent event) {
        if (on != null && !on.check(event)) {
            return event;
        }
        TFilter lastFilter = null;
        try {
            for (TFilter f : filters) {
                lastFilter = f;
                long now = System.currentTimeMillis();
                event = f.filter(event);
                long ut = System.currentTimeMillis() - now;
                if (profiler) {
                    FILTER_PROFILER_COUNTER.labels(f.id()).inc(ut);
                }
                if (event == null) {
                    logger.DEBUG("event has been discarded within filter " + f.getClass());
                    break;
                }
            }
            return event;
        } catch (Exception e) {
            logger.EXCEPTION(String.format("filter %s raise an uncatched exception", lastFilter.id()), e);
            return null;
        }
    }

    public void prepare() throws TPluginInitException {
        for (TFilter filter: filters) {
            filter.prepare();
        }
    }

    @Override
    public void release() {
        for (TFilter filter: filters) {
            try {
                PluginTools.release(filter);
            } catch (Exception e) {
                logger.ERROR("destroy filter %s failed", e, filter.id());
            }
        }
    }
}
