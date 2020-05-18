package com.aliyun.tauris.plugins.filter;


import com.aliyun.tauris.*;
import com.aliyun.tauris.annotations.ValueType;
import com.aliyun.tauris.utils.EventFormatter;
import com.aliyun.tauris.utils.EventLogging;
import io.tauris.expression.TExpression;

import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class BaseTFilter extends AbstractPlugin implements TFilter {


    protected TLogger logger;

    protected EventLogging logging;
    protected TExpression  on;

    /**
     * 当filter 结果为true时，增加新field
     */
    @ValueType(EventFormatter.class)
    protected Map<String, EventFormatter> newFields;

    /**
     * 当filter结果为false时丢弃整个事件
     */
    protected boolean discard;


    public void init() throws TPluginInitException {
        logger = TLogger.getLogger(this);
    }

    public boolean test(TEvent event) {
        if (on != null) {
            try {
                return on.check(event);
            } catch (RuntimeException e) {
                String expr = "<??>";
                try {
                    expr = on.toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                logger.ERROR("expression of plugin %s `" + expr + " ` execute error ", e, id());
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
                for (Map.Entry<String, EventFormatter> e: newFields.entrySet()) {
                    String val = e.getValue().format(event);
                    if (val != null) {
                        event.set(e.getKey(), val);
                    }
                }
            }
        }
        return event;
    }

    protected abstract boolean doFilter(TEvent event);
}
