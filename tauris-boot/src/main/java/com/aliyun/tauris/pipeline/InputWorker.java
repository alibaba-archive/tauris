package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TLogger;

/**
 * Class InputWorker
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class InputWorker extends Thread {

    private TLogger       logger;
    private TEventFactory eventFactory;
    private TPipe<TEvent> channel;

    private TInput input;

    public InputWorker(TEventFactory eventFactory, TInput input, TPipe<TEvent> channel) {
        this.eventFactory = eventFactory;
        this.logger = TLogger.getLogger(this);
        this.channel = channel;
        this.input = input;
    }

    public TInput getInput() {
        return input;
    }

    @Override
    public void run() {
        try {
            input.init(channel, eventFactory);
            input.run();
        } catch (Exception e) {
            logger.ERROR("input plugin thread raise an exception, pipeline will be closed", e);
//            pipeline.setState(TPipeline.State.failed);
        }
    }

    public void shutdown() {
        input.close();
        PluginTools.release(input);
    }
}
