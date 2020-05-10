package com.aliyun.tauris.plugins.resource;

import com.aliyun.tauris.TResource;
import com.aliyun.tauris.TResourceURI;
import mousio.client.retry.RetryNTimes;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.transport.EtcdNettyClient;
import mousio.etcd4j.transport.EtcdNettyConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by ZhangLei on 2018/4/28.
 */
public class EtcdResource extends TResource {

    private static Logger LOG = LoggerFactory.getLogger(EtcdResource.class);

    private ExecutorService executor;

    private EtcdClient client;

    private volatile boolean watch;

    @Override
    public void setURI(TResourceURI uri) {
        super.setURI(uri);
        watch = uri.getBoolParam("__watch__");
    }

    private EtcdClient getClient() {
        if (client == null) {
            EtcdNettyConfig config = new EtcdNettyConfig().setConnectTimeout(4000).setMaxFrameSize(10000 * 1000);
            client = new EtcdClient(new EtcdNettyClient(config, uri.toURI()));
            client.setRetryHandler(new RetryNTimes(1000, 10));
        }
        return client;
    }

    @Override
    public void watch(Consumer<byte[]> consumer) {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            EtcdClient client = getClient();
            while (watch) {
                try {
                    EtcdKeyGetRequest request = client.get(uri.getPath()).waitForChange();
                    EtcdResponsePromise<EtcdKeysResponse> promise = request.send();
                    EtcdKeysResponse resp = promise.get();
                    EtcdKeysResponse.EtcdNode node = resp.getNode();
                    consumer.accept(node.getValue().getBytes());
                } catch (Exception e) {
                    LOG.error(String.format("read resource from etcd %s failed", uri), e);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        });
    }

    @Override
    public byte[] fetch() throws Exception {
        EtcdClient                            client  = getClient();
        EtcdKeyGetRequest                     request = client.get(uri.getPath());
        EtcdResponsePromise<EtcdKeysResponse> promise = request.send();
        EtcdKeysResponse                      resp    = promise.get();
        EtcdKeysResponse.EtcdNode             node    = resp.getNode();
        return node.getValue().getBytes();
    }

    @Override
    public void release() {
        if (executor != null) {
            executor.shutdownNow();
        }
        IOUtils.closeQuietly(client);
        watch = false;
    }
}
