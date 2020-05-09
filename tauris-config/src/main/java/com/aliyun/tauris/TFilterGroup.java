package com.aliyun.tauris;

import com.alibaba.texpr.TExpression;
import com.aliyun.tauris.metrics.Counter;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class TFilterChain
 *
 * @author ZhangLei
 * @date 2018-09-11
 */
public class TFilterGroup extends TPluginGroup {

    private static final Counter FILTER_PROFILER_COUNTER = Counter.build().name("tauris_filter_profiler").labelNames("id").help("filter used time").create().register();
    private static final Counter FILTER_DISCARDS_TOTAL   = Counter.build().name("tauris_filter_discards_total").labelNames("id").help("filter discards events total").create().register();
    private static final Counter FILTER_ERROR_TOTAL      = Counter.build().name("tauris_filter_error_total").labelNames("id").help("filter error total").create().register();
    public static final  String  SYSPROP_FILTER_PROFILER = "tauris.filter.profiler";

    private TLogger logger;

    private List<TFilter> filters;

    private String id;

    private boolean profiler;

    private AtomicLong exceptionCount = new AtomicLong(0);

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
        TFilter lastFilter = null;
        try {
            if (on != null && !on.check(event)) {
                return event;
            }
            for (TFilter f : filters) {
                lastFilter = f;
                long now = System.currentTimeMillis();
                event = f.filter(event);
                long ut = System.currentTimeMillis() - now;
                if (profiler) {
                    FILTER_PROFILER_COUNTER.labels(f.id()).inc(ut);
                }
                if (event == null) {
                    FILTER_DISCARDS_TOTAL.labels(f.id()).inc();
                    logger.DEBUG("event has been discarded within filter " + f.getClass());
                    break;
                }
            }
            return event;
        } catch (Exception e) {
            long ec = exceptionCount.incrementAndGet();
            String msg;
            if (lastFilter != null) {
                FILTER_ERROR_TOTAL.labels(lastFilter.id()).inc();
                msg = String.format("filter %s raise an uncatched exception", lastFilter.id());
            } else {
                msg = String.format("filter group %s raise an uncatched exception", id());
            }
            if (ec < 100) {
                logger.EXCEPTION(msg, e);
            } else {
                logger.ERROR(msg + "(" + ec + ")");
            }
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
