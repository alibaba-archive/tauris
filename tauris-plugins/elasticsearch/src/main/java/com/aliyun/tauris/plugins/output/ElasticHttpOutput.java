package com.aliyun.tauris.plugins.output;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.codec.JSONEncoder;
import com.aliyun.tauris.plugins.codec.PlainEncoder;
import com.aliyun.tauris.TCodecException;
import com.aliyun.tauris.plugins.formatter.SimpleFormatter;
import com.aliyun.tauris.metrics.Counter;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("elasticsearch_http")
public class ElasticHttpOutput extends BaseTOutput {

    private final static int    BULKACTION    = 20000;
    private final static int    BULKSIZE      = 15 * 1024 * 1024; //MB
    private final static int    FLUSHINTERVAL = 10;
    private final static String BULKPATH      = "_bulk";

    private static Counter OUTPUT_COUNTER = Counter.build().name("elasticsearch_output_total").help("elasticsearch output count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("elasticsearch_error_total").help("elasticsearch error count").create().register();

    private static Logger LOG = LoggerFactory.getLogger("tauris.output.elasticsearch");


    private int bulkActions   = BULKACTION;
    private int bulkSize      = BULKSIZE;
    private int flushInterval = FLUSHINTERVAL;

    private String id;

    private String action = "index";

    private String index = "tauris-%{+yyyy.MM.dd}";

    @Required
    private String type;

    @Required
    private String[] hosts;

    private boolean https;

    private boolean metric;

    private boolean trace;

    private String _metaTemplate;

    private SimpleFormatter _idFormatter;
    private SimpleFormatter _indexFormatter;

    private Thread _bulkThread;

    private RestClient _client;

    private ExecutorService _executor = Executors.newFixedThreadPool(100);

    private LinkedList<String> _buffer = new LinkedList<>();
    private long _lastFlushTime;
    private Lock _lock;

    public void init() {
        if (this.codec == null || this.codec instanceof PlainEncoder) {
            this.codec = new JSONEncoder();
        }
        _lock = new ReentrantLock();
        _metaTemplate = String.format("{ \"%s\" : { \"_index\" : \"%%s\", \"_type\" : \"%s\" %%s  } }", action, type);
        _indexFormatter = SimpleFormatter.build(index);
        _idFormatter = id == null ? null : SimpleFormatter.build(id);
        List<HttpHost> hs = Lists.transform(Arrays.asList(this.hosts), new Function<String, HttpHost>() {
            @Override
            public HttpHost apply(String a) {
                String[] parsedHost = a.split(":");
                String   host       = parsedHost.length == 2 ? parsedHost[0] : a;
                Integer  port       = parsedHost.length == 2 ? Integer.parseInt(parsedHost[1]) : 9200;
                return new HttpHost(host, port, https ? "https" : "http");
            }
        });
        RestClientBuilder builder = RestClient.builder(hs.toArray(new HttpHost[hs.size()]));
        this._client = builder.build();

        _lastFlushTime = System.currentTimeMillis();
        _bulkThread = new Thread(() -> {
            while (true) {
                _lock.lock();
                if (!_buffer.isEmpty() && System.currentTimeMillis() - _lastFlushTime > flushInterval * 1000) {
                    flush();
                }
                _lock.unlock();
                try {
                    Thread.sleep(flushInterval * 1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        _bulkThread.start();
    }

    @Override
    public void writeEvent(TEvent event) {
        String index = _indexFormatter.format(event);
        String id = "";
        if (_idFormatter != null) {
            id = ", \"_id\":\"" + _idFormatter.format(event) + "\"";
        }
        String meta = String.format(_metaTemplate, index, id);
        _lock.lock();
        _buffer.add(meta);
        if (codec == null) {
            _buffer.add(event.getSource());
        } else {
            try {
                _buffer.add(codec.encode(event));
            } catch (TCodecException e) {
                _buffer.removeLast();
            }
        }
        if (_buffer.size() > bulkActions / 2) {
            flush();
        }
        _lock.unlock();
    }

    private void flush() {
        if (_buffer.isEmpty()) return;
        List<String> data = _buffer;
        _executor.submit(() -> {
            int total = data.size() / 2;
            int success = transport(data);
            OUTPUT_COUNTER.inc(success);
            int error = total - success;
            if (total > success) {
                ERROR_COUNTER.inc(error);
            }
        });
        _buffer = new LinkedList<>();
        _lastFlushTime = System.currentTimeMillis();
    }

    private int transport(List<String> content) {
        if (content.isEmpty()) return 0;

        StringBuilder requestBody = new StringBuilder();
        for (String line : content) {
            requestBody.append(line);
            requestBody.append('\n');
        }
        try {
            Response response = _client.performRequest(
                    "POST",
                    BULKPATH,
                    Collections.<String, String>emptyMap(),
                    new NStringEntity(
                            requestBody.toString(),
                            ContentType.APPLICATION_JSON
                    )
            );
            if (response.getStatusLine().getStatusCode() < 300) {
                if (trace) {
                    String resposeBody = StringUtils.join(IOUtils.readLines(response.getEntity().getContent()), "");
                    JSONObject ro = JSON.parseObject(resposeBody);
                    if (ro.containsKey("errors") && ro.getBoolean("errors")) {
                        if (ro.containsKey("items")) {
                            JSONArray items = ro.getJSONArray("items");
                            for (int i = 0; i < items.size(); i++) {
                                LOG.error("index failed:" + items.getJSONObject(i).toJSONString());
                            }
                        }
                    } else {
                        return content.size() / 2;
                    }
                } else {
                    return content.size() / 2;
                }
            } else {
                return 0;
            }
        } catch (IOException e) {
            return 0;
        }
        return 0;
    }

    @Override
    public void close() {
        _bulkThread.interrupt();
        _lock.lock();
        flush();
        _lock.unlock();
    }
}
