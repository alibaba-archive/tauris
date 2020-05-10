package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPrinter;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.plugins.codec.DefaultPrinter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("tcp")
public class TCPOutput extends BaseBatchOutput {

    private static Logger logger = LoggerFactory.getLogger(TCPOutput.class);

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_tcp_total").labelNames("id").help("tcp output event count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_tcp_error_total").labelNames("id").help("tcp output failed count").create().register();
    private static Counter RETRY_COUNTER  = Counter.build().name("output_tcp_retry_total").labelNames("id").help("tcp output retry count").create().register();

    @Required
    String target;

    /**
     * socket连接超时时间, 毫秒
     */
    int connectionTimeout = 3000;

    /**
     * 发送失败重试间隔时间，单位毫秒
     */
    long retryInterval = 1000;

    int retryTimes = 0;

    /**
     * 最大重试次数
     */
    long maxRetryCount = 0;

    boolean keepAlive = true;

    TPrinter printer = new DefaultPrinter();

    private Socket newSocket() throws IOException {
        try {
            Socket socket = new Socket();
            InetSocketAddress address = stringToSocketAddr(target);
            socket.setKeepAlive(keepAlive);
            socket.connect(address, connectionTimeout);
            logger.info(String.format("connect to %s success", target));
            return socket;
        } catch (java.net.ConnectException e) {
            logger.error(String.format("connect to %s failed", target));
            throw e;
        }
    }

    public static InetSocketAddress stringToSocketAddr(String str) {
        try {
            String[] ps = str.split(":");
            return new InetSocketAddress(ps[0], Integer.parseInt(ps[1]));
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid address:" + str);
        }
    }

    @Override
    protected BatchWriteTask newTask() throws Exception {
        return new TCPWriteTask();
    }

    @Override
    public void release() {

    }

    class TCPWriteTask extends BatchWriteTask {

        private TPrinter printer;

        private ByteArrayOutputStream data = new ByteArrayOutputStream();

        public TCPWriteTask() {
            printer = printer.withCodec(getCodec()).wrap(data);
        }

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            printer.write(event);
        }

        @Override
        protected void execute() {
            try {
                printer.flush();
                printer.close();
                int count = 0;
                while (count < retryTimes + 1) {
                    if (sendRequest()) {
                        OUTPUT_COUNTER.labels(id).inc(elementCount());
                        return;
                    } else {
                        try {
                            Thread.sleep(retryInterval * 1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        count++;
                        RETRY_COUNTER.labels(id).inc();
                    }
                }
                ERROR_COUNTER.labels(id).inc(elementCount());
            } catch (IOException e) {
                logger.error("execute http post failed", e);
            }
        }

        private boolean sendRequest() throws IOException {
            Socket socket = null;
            try {
                socket = newSocket();
                socket.getOutputStream().write(data.toByteArray());
            } catch (SocketTimeoutException e) {
                return false;
            } finally {
                IOUtils.closeQuietly(socket);
            }
            return true;
        }
    }
}
