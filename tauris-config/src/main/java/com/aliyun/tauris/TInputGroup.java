package com.aliyun.tauris;

import java.util.Collections;
import java.util.List;

/**
 * Class TInputGroup
 *
 * @author Ray Chaung<rockis@gmail.com>
 *
 */
public class TInputGroup extends TPluginGroup {

    private List<TInput> inputs;

    public TInputGroup(List<TInput> inputs) {
        this.inputs = inputs;
    }

    public List<TInput> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    @Override
    public void release() {
        inputs.forEach(TPlugin::release);
    }
}
