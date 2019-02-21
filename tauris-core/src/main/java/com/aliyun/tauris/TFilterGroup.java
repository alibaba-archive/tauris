package com.aliyun.tauris;

import com.aliyun.tauris.expression.TExpression;
import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.utils.TLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Class TFilterChain
 *
 * @author ZhangLei
 * @date 2018-09-11
 */
public class TFilterGroup extends TPluginGroup {

    private static final Counter FILTER_DISCARD_COUNTER  = Counter.build().name("tauris_filter_discards").labelNames("id").help("filter discards event count").create().register();
    private static final Counter FILTER_PROFILER_COUNTER = Counter.build().name("tauris_filter_profiler").labelNames("id").help("filter used time").create().register();

    private static final Counter FILTERGROUP_INPUT_COUNTER = Counter.build().name("tauris_filtergroup_total").labelNames("id").help("filter group accepted event total").create().register();

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

    public List<TFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    public TEvent filter(TEvent event) {
        if (on != null && !on.check(event)) {
            return event;
        }
        FILTERGROUP_INPUT_COUNTER.labels(id()).inc();
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
                if (event == null && f.id() != null) {
                    FILTER_DISCARD_COUNTER.labels(f.id()).inc();
                }
                if (event == null) {
                    logger.DEBUG("event has been discarded within filter " + f.getClass());
                    break;
                }
            }
            return event;
        } catch (Exception e) {
            logger.ERROR("filter %s raise an uncatched exception", e, lastFilter.id());
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
