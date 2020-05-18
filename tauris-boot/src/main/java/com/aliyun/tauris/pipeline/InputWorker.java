package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TLogger;

/**
 * Class InputWorker
 *
 * @author Ray Chaung<rockis@gmail.com>
 */
public class InputWorker extends Thread {

    private TLogger       logger;
    private TEventFactory eventFactory;
    private TPipe<TEvent> channel;

    private TInput input;

    private volatile boolean running;

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
            running = true;
            input.run();
        } catch (Exception e) {
            logger.ERROR("input plugin thread raise an exception, pipeline will be closed", e);
            running = false;
        }
    }

    public void shutdown() {
        if (running) {
            input.close();
        }
        PluginTools.release(input);
    }
}
