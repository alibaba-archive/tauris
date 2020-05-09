package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Class DistributePipe
 *
 * @author yundun-waf-dev
 * @date 2019-04-17
 */
public class DistributePipe extends AbstractPipe {

    private List<OutputPipe> outputs = new ArrayList<>();

    public DistributePipe(int queueCapacity) {
        super("distribute", queueCapacity);
    }

    @Override
    public void put(TEvent event) throws InterruptedException {
        if (queue == null || outputs.size() == 1) {
            // 如果无队列缓冲区或仅有一个output,则直接将event写入output
            write(event);
        } else {
            queue.put(event);
        }
    }

    @Override
    public void open() throws Exception {
        for (OutputPipe o: outputs) {
            o.open();
        }
        super.open();
    }

    @Override
    protected void write(TEvent event) throws InterruptedException {
        TEvent ep = new EphemEvent(event, outputs.size());
        for (OutputPipe output: outputs) {
            output.put(ep);
        }
    }

    @Override
    public void close() {
        super.close();
        for (OutputPipe o: outputs) {
            o.close();
        }
    }

    @Override
    public void join(TPipe<TEvent> pipe) {
        outputs.add((OutputPipe)pipe);
    }
}
