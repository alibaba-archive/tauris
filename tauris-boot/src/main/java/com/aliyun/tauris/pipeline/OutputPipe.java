package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TOutput;
import com.aliyun.tauris.TLogger;

/**
 * Class OutputPipe
 *
 * @author yundun-waf-dev
 * @date 2019-04-16
 */
public class OutputPipe extends AbstractPipe {

    private TLogger logger;
    private TOutput output;

    public OutputPipe(TOutput output, int capacity) {
        super(output.id(), capacity);
        this.output = output;
        this.logger = TLogger.getLogger(this);
        this.threadPriority = Thread.MAX_PRIORITY;
    }

    @Override
    public void open() throws Exception {
        output.start();
        super.open();
    }

    @Override
    public void close() {
        super.close();
        output.stop();
    }

    protected void write(TEvent event) throws InterruptedException{
        if (event != null) {
            try {
                output.write(event);
                event.destroy();
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
        }
    }
}
