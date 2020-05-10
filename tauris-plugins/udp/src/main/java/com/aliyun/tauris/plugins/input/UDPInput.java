package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;
import com.aliyun.tauris.TLogger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("udp")
public class UDPInput extends BaseTInput {

    private static Counter RECEIVED = Counter.build().name("input_udp_received_total").labelNames("id").help("Received udp packets").create().register();

    private TLogger logger;

    int bufferLen = 2048;

    String host = "0.0.0.0";

    @Required
    int port;

    Charset charset = Charset.defaultCharset();

    private DatagramSocket socket;
    private boolean        running;
    private byte[]         buffer;
    private Thread         thread;

    public void doInit() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        buffer = new byte[bufferLen];
        try {
            socket = new DatagramSocket(new InetSocketAddress(host, port));
        } catch (Exception e) {
            throw new TPluginInitException(e.getMessage());
        }
    }

    public void run() throws Exception {
        thread = new Thread(new ServerWorker());
        thread.start();
    }

    @Override
    public void close() {
        super.close();
        logger.info("udp input closing");
        running = false;
        thread.interrupt();
        logger.info("udp input has been closed");
    }

    private class ServerWorker implements Runnable {
        @Override
        public void run() {
            running = true;

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, bufferLen);
                try {
                    socket.receive(packet);
                    RECEIVED.labels(id()).inc();
                    byte[] bs = packet.getData();
                    String text = new String(bs, 0, packet.getLength(), charset);
                    TEvent event = getCodec().decode(text, getEventFactory());
                    putEvent(event);
                } catch (Exception e) {
                    logger.EXCEPTION(e);
                }
            }
        }
    }
}
