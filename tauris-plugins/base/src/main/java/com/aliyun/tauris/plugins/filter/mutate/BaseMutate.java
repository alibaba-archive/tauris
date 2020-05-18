package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TObject;
import io.tauris.expression.TExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class BaseMutate implements TMutate {

    private static Logger LOG = LoggerFactory.getLogger(BaseMutate.class);

    protected TExpression on;

    protected boolean test(TObject event) {
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
}
