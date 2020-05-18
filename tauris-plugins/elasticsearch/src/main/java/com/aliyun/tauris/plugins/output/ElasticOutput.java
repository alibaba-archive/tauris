package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TCodecException;
import com.aliyun.tauris.plugins.formatter.SimpleFormatter;
import com.aliyun.tauris.metrics.Counter;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("elasticsearch")
public class ElasticOutput extends BaseTOutput {


    private final static int     BULKACTION        = 20000;
    private final static int     BULKSIZE          = 15; //MB
    private final static int     FLUSHINTERVAL     = 10;
    private final static int     CONCURRENTREQSIZE = 0;
    private final static boolean DEFAULTSNIFF      = true;
    private final static boolean DEFAULTCOMPRESS   = false;

    private static Counter OUTPUT_COUNTER = Counter.build().name("elasticsearch_output_total").help("elasticsearch output count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("elasticsearch_error_total").help("elasticsearch error count").create().register();

    private static Logger LOG = LoggerFactory.getLogger("tauris.output.elasticsearch");

    @Required
    private String cluster;

    private String id;

    private String index = "tauris-%{+yyyy.MM.dd}";

    @Required
    private String type;

    private String[] hosts = new String[]{"127.0.0.1:9200"};

    private String user;

    private String password;

    private boolean sniff = DEFAULTSNIFF;

    private boolean compress = DEFAULTCOMPRESS;

    private int bulkActions        = BULKACTION;
    private int bulkSize           = BULKSIZE;
    private int flushInterval      = FLUSHINTERVAL;
    private int concurrentRequests = CONCURRENTREQSIZE;

    private SimpleFormatter _indexFormatter;
    private SimpleFormatter _idFormatter;

    private BulkProcessor   _bulkProcessor;
    private TransportClient _client;

    public void init() throws TCodecException, Exception {
        _indexFormatter = SimpleFormatter.build(index);
        _idFormatter = id == null ? null : SimpleFormatter.build(id);

        Settings settings = Settings.builder()
//                .put("client.transport.sniff", sniff)
//                .put("transport.tcp.compress", compress)
                .put("node.name", InetAddress.getLocalHost().getHostName())
                .put("cluster.name", cluster).build();
        _client = new PreBuiltTransportClient(Settings.EMPTY);

        for (String h : hosts) {
            String[] parsedHost = h.split(":");
            try {
                String host = parsedHost[0];
                String port = parsedHost.length == 2 ? parsedHost[1] : "9300";
                _client.addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName(host), Integer.parseInt(port)));
            } catch (UnknownHostException e) {
                throw new TCodecException("elasticsearch host format error", e);
            }
        }

        _bulkProcessor = BulkProcessor.builder(
                _client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {
                        LOG.info("executionId: " + executionId);
                        LOG.info("numberOfActions: " + request.numberOfActions());
                        LOG.info("Hosts:" + _client.transportAddresses().toString());
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request,
                                          BulkResponse response) {
                        LOG.info("bulk done with executionId: " + executionId);
                        List<DocWriteRequest> requests = request.requests();
                        int toBeTry = 0;
                        int totalFailed = 0;
                        for (BulkItemResponse item : response.getItems()) {
                            if (item.isFailed()) {
                                switch (item.getFailure().getStatus()) {
                                    case TOO_MANY_REQUESTS:
                                    case SERVICE_UNAVAILABLE:
                                        if (toBeTry == 0) {
                                            LOG.error("bulk has failed item which NEED to retry");
                                            LOG.error(item.getFailureMessage());
                                        }
                                        toBeTry++;
                                        _bulkProcessor.add(requests.get(item.getItemId()));
                                        break;
                                    default:
                                        if (totalFailed == 0) {
                                            LOG.error("bulk has failed item which do NOT need to retry");
                                            LOG.error(item.getFailureMessage());
                                        }
                                        break;
                                }
                                totalFailed++;
                            }
                        }

                        if (totalFailed > 0) {
                            LOG.info(totalFailed + " doc failed, " + toBeTry + " need to retry");
                        } else {
                            LOG.debug("no failed docs");
                        }

                        if (toBeTry > 0) {
                            try {
                                LOG.info("sleep " + toBeTry / 2 + "millseconds after bulk failure");
                                Thread.sleep(toBeTry / 2);
                            } catch (InterruptedException e) {
                            }
                        } else {
                            LOG.debug("no docs need to retry");
                        }

                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                        LOG.error("bulk got exception: " + failure.getMessage());
                    }
                }).setBulkActions(bulkActions)
                .setBulkSize(new ByteSizeValue(bulkSize, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(flushInterval))
                .setConcurrentRequests(concurrentRequests).build();
    }

    @Override
    public void writeEvent(TEvent event) {
        IndexRequest indexRequest;
        String _index = _indexFormatter.format(event);
        if (_idFormatter != null) {
            String id = _idFormatter.format(event);
            indexRequest = new IndexRequest(_index, type, id).source(event.getFields());
        } else {
            indexRequest = new IndexRequest(_index, type).source(event.getFields());
        }
        this._bulkProcessor.add(indexRequest);

    }

    @Override
    public void close() {
        _client.close();
    }
}
