package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TObject;
import com.aliyun.tauris.expression.TExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ZhangLei on 17/5/26.
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
