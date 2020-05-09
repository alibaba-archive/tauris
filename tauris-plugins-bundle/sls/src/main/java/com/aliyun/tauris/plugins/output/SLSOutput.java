package com.aliyun.tauris.plugins.output;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.formatter.EventFormatter;
import com.aliyun.tauris.metrics.Counter;

import com.aliyun.tauris.TLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * sls输出插件
 * Created by ZhangLei on 17/5/28.
 */
@Name("sls")
public class SLSOutput extends BaseBatchOutput {


    private static Counter OUTPUT_COUNTER = Counter.build().name("output_sls_total").labelNames("id").help("sls put count").create().register();

    private TLogger logger;

    @Required
    String endPoint;

    @Required
    String accessKeyId;

    @Required
    String accessKeySecret;

    @Required
    String project;

    @Required
    String logstore;

    EventFormatter topic;

    String hashKey;

    String source;

    /**
     * 输出meta到sls, 如果为空则不输出
     * meta在sls的命名前后带两个下划线,比如名字为tag的meta, 在sls中的名字则为__tag__
     */
    String[] meta;

    /**
     * 输出field到sls,如果为空则输出所有field
     */
    String[] fields;


    /**
     * 不输出的field, 仅在fields为空时有效
     */
    String[] excludeFields;

    private Set<String> excludeFieldSet;

    private Client client;

    public void init() throws TPluginInitException {
        super.init();
        this.logger = TLogger.getLogger(this);
        client = new Client(endPoint, accessKeyId, accessKeySecret);
        if (source == null) {
            try {
                source = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new TPluginInitException("cannot read hostname");
            }
        }
        if (fields == null && excludeFields != null) {
            excludeFieldSet = new HashSet<>(Arrays.asList(excludeFields));
        }
    }

    @Override
    protected BatchTask createTask() throws Exception {
        return new SLSWriteTask();
    }

    class SLSWriteTask extends BatchTask {

        Map<String, List<LogItem>> items = new HashMap<>();

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            LogItem item = new LogItem((int) (event.getTimestamp() / 1000));
            if (meta != null) {
                for (String m : meta) {
                    Object mv = event.getMeta(m);
                    if (mv != null) {
                        item.PushBack("__" + m + "__", mv.toString());
                    }
                }
            }
            if (fields != null) {
                for (String field : fields) {
                    String key = field;
                    Object val = null;
                    if (field.contains(":")) {
                        String[] fs = field.split(":");
                        key = fs[0];
                        val = event.get(fs[1]);
                    } else {
                        val = event.get(field);
                    }
                    if (val != null) {
                        item.PushBack(key, val.toString());
                    }
                }
            } else {
                for (Map.Entry<String, Object> e: event.getFields().entrySet()) {
                    if (e.getValue() != null) {
                        String key = e.getKey();
                        if (excludeFieldSet == null || !excludeFieldSet.contains(key)) {
                            item.PushBack(key, e.getValue().toString());
                        }
                    }
                }
            }
            String topic = "";
            if (SLSOutput.this.topic != null) {
                topic = SLSOutput.this.topic.format(event);
            }
            List<LogItem> subItems = items.get(topic);
            if (subItems == null) {
                subItems = new ArrayList<>();
                items.put(topic, subItems);
            }
            subItems.add(item);
        }

        @Override
        protected boolean execute() {
            List<String> topics = new ArrayList<>(items.keySet());
            for (String topic: topics) {
                List<LogItem> es = items.get(topic);
                PutLogsRequest request = new PutLogsRequest(project, logstore, topic, source, es, hashKey);
                try {
                    client.PutLogs(request);
                    OUTPUT_COUNTER.labels(id()).inc(es.size());
                    items.remove(topic);
                } catch (LogException ex) {
                    logger.ERROR("put log to %s/%s with topic '%s' failed", ex, project, logstore, topic);
                }
            }
            return items.isEmpty();
        }

        @Override
        protected void active() {
            clear();
        }

        @Override
        protected void clear() {
            items.values().forEach(List::clear);
        }
    }
}
