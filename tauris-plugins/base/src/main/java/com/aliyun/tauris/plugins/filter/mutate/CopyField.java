package com.aliyun.tauris.plugins.filter.mutate;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("copy")
public class CopyField extends BaseMutate {

    String source;

    String target;

    /**
     * 需要copy的fields, 每个item是:分隔的字符串, :前是source field, :后是target field
     */
    String[] fields;

    boolean rename;

    /**
     * 忽略值为null的字段
     */
    boolean ignoreNull;

    private List<String[]> _fields;

    public CopyField() {
    }

    public CopyField(String source, String target, String[] fields, boolean rename) {
        this.source = source;
        this.target = target;
        this.fields = fields;
        this.rename = rename;
    }

    public void init() throws TPluginInitException {
        _fields = new ArrayList<>();
        if (source != null && target != null) {
            _fields.add(new String[]{source, target});
        }
        if (fields != null) {
            for (String f : fields) {
                String[] ps = f.split(":");
                if (ps.length != 2) {
                    throw new TPluginInitException("invalid field define:" + f + ", eg. source_field:target_field");
                }
                _fields.add(new String[]{ps[0], ps[1]});
            }
        }
        if (_fields.isEmpty()) {
            throw new TPluginInitException("there no field defined");
        }
    }

    @Override
    public void mutate(TEvent event) {
        if (!test(event)) {
            return;
        }
        for (String[] e: _fields) {
            String oldName = e[0];
            String newName = e[1];
            Object v = event.get(oldName);
            if (ignoreNull && v == null) {
                continue;
            }
            event.set(newName, v);
        }
        if (rename) {
            for (String[] e: _fields) {
                String oldName = e[0];
                event.remove(oldName);
            }
        }
    }

}
