package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.metrics.Counter;

/**
 * 空输出
 * 将filter输入的内容直接丢弃
 * Created by ZhangLei on 16/12/8.
 */
public class EmptyOutput extends BaseTOutput {

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_empty_total").labelNames("id").help("empty output count").create().register();

    /**
     * 当符合check条件时，sleep一段时间，单位毫秒。为0时不sleep。
     * 默认为0
     */
    long sleep;

    @Override
    protected void doWrite(TEvent event) {
        if (check(event) & sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                return;
            }
        }
        if (id() == null) {
            OUTPUT_COUNTER.labels("empty").inc();
        } else {
            OUTPUT_COUNTER.labels(id()).inc();
        }
        super.doWrite(event);
    }
}
