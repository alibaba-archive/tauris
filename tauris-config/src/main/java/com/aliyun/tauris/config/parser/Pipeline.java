package com.aliyun.tauris.config.parser;

import com.aliyun.tauris.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chuanshi.zl on 30.03.16.
 */
public class Pipeline {

    private InputGroup        rawInputGroup;
    private List<FilterGroup> rawFilterGroups;
    private List<OutputGroup> rawOutputGroups;

    private TInputGroup        inputGroup;
    private List<TFilterGroup> filterGroups;
    private List<TOutputGroup> outputGroups;

    Pipeline(InputGroup inputGroup, List<FilterGroup> filterGroups, List<OutputGroup> outputGroups) {
        this.rawInputGroup = inputGroup;
        this.rawFilterGroups = filterGroups;
        this.rawOutputGroups = outputGroups;
    }

    /**
     */
    public void build() {
        Helper.m.init();
        inputGroup = (TInputGroup) rawInputGroup.build(TInputGroup.class);

        filterGroups = new ArrayList<>();
        for (PluginGroup fg : rawFilterGroups) {
            filterGroups.add((TFilterGroup) fg.build(TFilterGroup.class));
        }

        outputGroups = new ArrayList<>();
        for (PluginGroup fg : rawOutputGroups) {
            outputGroups.add((TOutputGroup) fg.build(TOutputGroup.class));
        }
    }

    public TInputGroup getInputGroup() {
        return inputGroup;
    }

    public List<TFilterGroup> getFilterGroups() {
        return filterGroups;
    }

    public List<TOutputGroup> getOutputGroups() {
        return outputGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pipeline      pipeline = (Pipeline) o;
        EqualsBuilder eb       = new EqualsBuilder().append(rawInputGroup, pipeline.rawInputGroup);
        if (rawFilterGroups.size() != pipeline.rawFilterGroups.size()) {
            return false;
        }
        for (int i = 0; i < rawFilterGroups.size(); i++) {
            PluginGroup l = rawFilterGroups.get(i);
            PluginGroup r = pipeline.rawFilterGroups.get(i);
            eb.append(l, r);
        }

        if (rawOutputGroups.size() != pipeline.rawOutputGroups.size()) {
            return false;
        }
        for (int i = 0; i < rawOutputGroups.size(); i++) {
            PluginGroup l = rawOutputGroups.get(i);
            PluginGroup r = pipeline.rawOutputGroups.get(i);
            eb.append(l, r);
        }

        return eb.build();

    }

    @Override
    public int hashCode() {
        HashCodeBuilder hb = new HashCodeBuilder();
        hb.append(rawInputGroup).append(rawInputGroup);
        for (PluginGroup f : rawFilterGroups) {
            hb.append(f);
        }
        for (PluginGroup f : rawOutputGroups) {
            hb.append(f);
        }
        return hb.build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rawInputGroup.toString());
        rawFilterGroups.forEach((f) -> sb.append(f.toString()));
        rawOutputGroups.forEach((f) -> sb.append(f.toString()));
        return sb.toString();
    }
}
