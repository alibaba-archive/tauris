package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TQueue;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.utils.TLogger;
import org.joda.time.DateTime;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.*;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServerConfig;

/**
 * Created by ZhangLei on 16/12/7.
 */
public class SyslogInput extends BaseTInput implements SyslogConstants, SyslogServerEventHandlerIF {

    private TLogger logger;

    String protocol = "udp";

    String host = "0.0.0.0";

    @Required
    int port;

    private SyslogServerIF _server;

    public void doInit() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        if (protocol == null || "".equals(protocol.trim())) {
            throw new TPluginInitException("Instance protocol cannot be null or empty");
        }
        if (!protocol.equals(TCP) && !protocol.equals(UDP)) {
            throw new TPluginInitException("Unknown protocol " + protocol);
        }
        SyslogServerConfigIF config = null;
        if (protocol.equals(TCP)) {
            config = new TCPNetSyslogServerConfig();
        } else {
            config = new UDPNetSyslogServerConfig();
        }
        config.setHost(host);
        config.setPort(port);
        String syslogProtocol = protocol.toLowerCase();

        try {
            Class syslogClass = config.getSyslogServerClass();
            _server = (SyslogServerIF) syslogClass.newInstance();

        } catch (ClassCastException | IllegalAccessException | InstantiationException cse) {
            throw new TPluginInitException("syslog initialize failed", cse);
        }
        config.addEventHandler(this);
        _server.initialize(syslogProtocol,config);
    }

    public void run() throws Exception {
        _server.run();
    }

    @Override
    public void event(SyslogServerIF syslogServerIF, SyslogServerEventIF event) {
        String message = event.getMessage().trim();
        try {
            TEvent e = codec.decode(message);
            e.setTimestamp(new DateTime(event.getDate()));
            e.addMeta("level", event.getLevel());
            e.addMeta("facility", event.getFacility());
            if (event.getHost() != null){
                e.addMeta("host", event.getHost());
            }
            putEvent(e);
        } catch (DecodeException ex) {
            logger.WARN2("decode message failed", ex, message);
        } catch (Exception e) {
            logger.ERROR("decode message failed, unexpected exception happend", e);
        }
    }

    @Override
    public void close() {
        super.close();
        logger.INFO("syslog input closing");
        _server.shutdown();
        logger.INFO("syslog input has been closed");
    }
}
