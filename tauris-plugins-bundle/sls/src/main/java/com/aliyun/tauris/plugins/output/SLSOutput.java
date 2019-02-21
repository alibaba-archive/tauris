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
import com.aliyun.tauris.formatter.SimpleFormatter;
import com.aliyun.tauris.metric.Counter;

import com.aliyun.tauris.utils.TLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sls输出插件
 * Created by ZhangLei on 17/5/28.
 */
@Name("sls")
public class SLSOutput extends BaseBatchOutput {


    private static Counter OUTPUT_COUNTER = Counter.build().name("output_sls_total").labelNames("id").help("sls put count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_sls_error_total").labelNames("id").help("sls put error count").create().register();

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

    SimpleFormatter topic;

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
    }

    @Override
    protected BatchWriteTask newTask() throws Exception {
        return new SLSWriteTask();
    }

    class SLSWriteTask extends BatchWriteTask {

        Map<String, List<LogItem>> items = new HashMap<>();

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            LogItem item = new LogItem((int) (event.getTimestamp().getMillis() / 1000));
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
                        item.PushBack(e.getKey(), e.getValue().toString());
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
        protected void execute() {
            for (Map.Entry<String, List<LogItem>> e: items.entrySet()) {
                PutLogsRequest request = new PutLogsRequest(project, logstore, e.getKey(), source, e.getValue(), hashKey);
                try {
                    client.PutLogs(request);
                    OUTPUT_COUNTER.labels(id()).inc(e.getValue().size());
                } catch (LogException ex) {
                    ERROR_COUNTER.labels(id()).inc(elementCount());
                    logger.ERROR("put log to %s/%s with topic '%s' failed", ex, project, logstore, e.getKey());
                }
            }
        }
    }
}
