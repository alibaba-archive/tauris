package com.aliyun.tauris.metric;

import com.aliyun.tauris.metric.server.FileMetricServer;
import com.aliyun.tauris.metric.server.HttpMetricServer;
import com.aliyun.tauris.utils.TLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * Created by ZhangLei on 16/12/8.
 */
public abstract class MetricServer {

    protected TLogger logger;

    /**
     * 通过VM参数创建MetricServer
     * @return MetricServer
     */
    public static MetricServer createMetricServer() {
        String pt   = System.getProperty("tauris.metric.port");
        String path = System.getProperty("tauris.metric.path", "/metrics");
        String host = System.getProperty("tauris.metric.host", "127.0.0.1");
        if (pt != null) {
            try {
                int port = Integer.parseInt(pt);
                return new HttpMetricServer(host, port, path);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid metric port number '" + pt + "'");
            }
        }
        String mfile  = System.getProperty("tauris.metric.file");
        String sint   = System.getProperty("tauris.metric.interval", "15");
        if (mfile != null) {
            try {
                int interval = Integer.parseInt(sint);
                int[] validIntervals = new int[]{5, 15, 20, 30, 60};
                if (Arrays.binarySearch(validIntervals, interval) < 0) {
                    throw new IllegalArgumentException("Invalid interval '" + sint + "', optional interval is [5, 15, 20, 30, 60]");
                }
                return new FileMetricServer(new File(mfile), interval);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid interval '" + sint + "' number");
            }
        }
        return null;
    }

    public abstract void init();

    public abstract void start();

    public abstract void shutdown();
}

