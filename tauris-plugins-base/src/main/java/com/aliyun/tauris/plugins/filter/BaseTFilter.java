package com.aliyun.tauris.plugins.filter;


import com.aliyun.tauris.AbstractPlugin;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFilter;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.annotations.ValueType;
import com.aliyun.tauris.formatter.SimpleFormatter;
import com.aliyun.tauris.utils.EventLogging;
import com.aliyun.tauris.expression.TExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by ZhangLei on 16/12/10.
 */
public abstract class BaseTFilter extends AbstractPlugin implements TFilter {

    private static Logger LOG = LoggerFactory.getLogger(BaseTFilter.class);

    protected EventLogging logging;
    protected TExpression  on;

    /**
     * 当filter 结果为true时，增加新field
     */
    @ValueType(SimpleFormatter.class)
    protected Map<String, SimpleFormatter> newFields;

    /**
     * 当filter结果为false时丢弃整个事件
     */
    protected boolean discard;

    public boolean test(TEvent event) {
        if (on != null) {
            try {
                return on.check(event);
            } catch (RuntimeException e) {
                LOG.error("expression `" + on.toString() + " ` execute error", e);
                throw e;
            }
        }
        return true;
    }

    @Override
    public TEvent filter(TEvent event) {
        if (test(event)) {
            if (logging != null) {
                logging.log(event);
            }
            boolean success = doFilter(event);
            if (discard && !success) {
                return null;
            }
            if (success && newFields != null) {
                for (Map.Entry<String, SimpleFormatter> e: newFields.entrySet()) {
                    String val = e.getValue().format(event);
                    if (val != null) {
                        event.set(e.getKey(), val);
                    }
                }
            }
        }
        return event;
    }

    abstract boolean doFilter(TEvent event);

    @Override
    public void release() {
        super.release();
    }
}
