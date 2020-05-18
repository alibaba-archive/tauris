package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.*;
import com.aliyun.tauris.TLogger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Class FilterWorker
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class FilterWorker extends Thread {

    private          TLogger               logger;
    private          BlockingQueue<TEvent> queue;
    private          DistributePipe        distribute;
    private volatile List<TFilterGroup>    filterGroups;
    private volatile boolean               running;

    public FilterWorker(List<TFilterGroup> filterGroups, BlockingQueue<TEvent> queue, DistributePipe distribute) {
        this.filterGroups = filterGroups;
        this.queue = queue;
        this.distribute = distribute;
        this.logger = TLogger.getLogger(this);
        setDaemon(true);
    }

    public void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        while (running || !queue.isEmpty()) {
            try {
                TEvent event = queue.poll(50, TimeUnit.MILLISECONDS);
                if (event == null) {
                    continue;
                }
                for (TFilterGroup fg : filterGroups) {
                    TEvent e = event;
                    event = fg.filter(event);
                    if (event == null) {
                        e.destroy();
                        break;
                    }
                }
                if (event != null) {
                    distribute.put(event);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
        }
        logger.warn("filter worker quited");
    }
}
