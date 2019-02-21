package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.AbstractPlugin;
import com.aliyun.tauris.TEncoder;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TOutput;
import com.aliyun.tauris.plugins.codec.PlainEncoder;
import com.aliyun.tauris.expression.TExpression;
import com.aliyun.tauris.metric.CounterWidget;

/**
 * Created by ZhangLei on 16/12/10.
 */
public abstract class BaseTOutput extends AbstractPlugin implements TOutput {

    protected TEncoder codec = new PlainEncoder();

    protected CounterWidget counter;

    protected TExpression on;

    @Override
    public boolean check(TEvent event) {
        if (on != null) {
            return on.check(event);
        }
        return true;
    }

    /**
     * check event and write event
     * @param event
     */
    @Override
    public boolean write(TEvent event) {
        if (check(event)) {
            doWrite(event);
            return true;
        }
        return false;
    }

    /**
     * 不做check写入event
     * @param event
     */
    protected void doWrite(TEvent event) {
    }

}
