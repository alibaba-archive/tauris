package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TObject;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.TPluginInitException;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by ZhangLei on 16/12/11.
 */
@Name("remove_field")
public class RemoveField extends BaseMutate {

    String[] fields;

    Set<String> _fieldSet;

    public void setFieldSet(Set<String> _fieldSet) {
        this._fieldSet = _fieldSet;
    }

    public void init() throws TPluginInitException {
        if (on == null && fields == null) {
            throw new TPluginInitException("neither fields or on must be set");
        }

        if (fields != null) {
            _fieldSet = Sets.newHashSet(fields);
        }
    }

    @Override
    public void mutate(TEvent event) {
        Set<String> keySet = _fieldSet;
        if (keySet == null) {
            keySet = Sets.newHashSet(event.getFields().keySet());;
        }
        ValueGetter vg = new ValueGetter(event);
        for (String k : keySet) {
            vg.name = k;
            if (test(vg)) {
                event.remove(k);
            }
        }
    }

    private static class ValueGetter implements TObject {

        TEvent event;
        String name;

        public ValueGetter(TEvent event) {
            this.event = event;
        }

        @Override
        public Object get(String unused) {
            return event.get(this.name);
        }
    }

}
