package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metric.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("udp")
public class UDPOutput extends BaseTOutput {

    private static Counter SENT        = Counter.build().name("input_udp_sent_total").help("Sent udp packets").create().register();
    private static Counter SENT_FAILED = Counter.build().name("input_udp_sent_failed_total").help("Sent udp failed packets").create().register();

    private static Logger LOG = LoggerFactory.getLogger(UDPOutput.class);

    @Required
    String host;

    @Required
    int port;

    private DatagramSocket _socket;

    private InetAddress _dest;

    public void init() throws TPluginInitException {
        try {
            _socket = new DatagramSocket();
            _dest = InetAddress.getByName(host);
        } catch (Exception e) {
            throw new TPluginInitException("create socket error", e);
        }
    }

    public void doWrite(TEvent event) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            codec.encode(event, output);
            output.flush();
            DatagramPacket packet = new DatagramPacket(output.toByteArray(), output.size(), _dest, port);
            _socket.send(packet);
            SENT.inc();
        } catch (EncodeException e) {
            LOG.warn("encode event failed", e);
        } catch (IOException e) {
            SENT_FAILED.inc();
            LOG.warn("send udp packet failed", e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        _socket.close();
    }
}
