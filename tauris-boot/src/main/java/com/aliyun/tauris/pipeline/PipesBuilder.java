package com.aliyun.tauris.pipeline;

import com.aliyun.tauris.*;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Class ChannelBuilder
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class PipesBuilder {

    private TPipe<TEvent>  filterPipe;
    private DistributePipe distributePipe;
    private boolean distributeGrouped;

    public PipesBuilder withFilters(List<TFilterGroup> filters, int workerCount, int queueCapacity) {
        Preconditions.checkArgument(workerCount >= 0, "workerCount is negative");
        Preconditions.checkArgument(queueCapacity >= 0, "queueCapacity is negative");

        if (!filters.isEmpty() && queueCapacity > 0 && workerCount == 0) {
            throw new IllegalArgumentException("worker count must greater than zero");
        }
        this.filterPipe = new FilterPipe(filters, workerCount, queueCapacity);
        return this;
    }

    public PipesBuilder withDistribute(boolean distributeGrouped, int queueCapacity) {
        Preconditions.checkArgument(queueCapacity >= 0, "queueCapacity is negative");
        this.distributePipe = new DistributePipe(queueCapacity);
        this.distributeGrouped = distributeGrouped;
        this.filterPipe.join(distributePipe);
        return this;
    }

    public PipesBuilder withOutput(List<TOutputGroup> outputs, int queueCapacity) {
        Preconditions.checkArgument(queueCapacity >= 0, "queueCapacity is negative");
        if (outputs.isEmpty()) {
            throw new IllegalArgumentException("no output plugin configured");
        }
        for (TOutputGroup og : outputs) {
            if (distributeGrouped) {
                OutputPipe op = new OutputPipe(og, queueCapacity);
                distributePipe.join(op);
            } else {
                for (TOutput o : og.getOutputs()) {
                    OutputPipe op = new OutputPipe(o, queueCapacity);
                    distributePipe.join(op);
                }
            }
        }
        return this;
    }

    public TPipe<TEvent> build() {
        return filterPipe;
    }

}
