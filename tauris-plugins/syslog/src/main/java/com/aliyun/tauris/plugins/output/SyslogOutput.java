package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TLogger;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.utils.EventFormatter;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("syslog")
public class SyslogOutput extends BaseTOutput implements SyslogConstants {

    private TLogger logger;

    private String protocol = "udp";

    @Required
    String host;

    @Required
    int port;

    EventFormatter level = EventFormatter.build("info");

    private SyslogIF syslog;

    public void init() throws TPluginInitException {
        logger = TLogger.getLogger(this);
        if (protocol == null || "".equals(protocol.trim())) {
            throw new TPluginInitException("Instance protocol cannot be null or empty");
        }
        if (!protocol.equals(TCP) && !protocol.equals(UDP)) {
            throw new TPluginInitException("Unsupported protocol " + protocol);
        }
        syslog = Syslog.getInstance(protocol);

        syslog.getConfig().setHost(host);
        syslog.getConfig().setPort(port);
    }

    public void doWrite(TEvent event) {
        try {
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            String lv = level.format(event);
            Level lvl = lv == null ? Level.info : Level.valueOf(lv);
            if (lvl == null) {
                lvl = Level.info;
            }
            codec.encode(event, writer);
            syslog.log(lvl.ordinal(), writer.toString());
        } catch (EncodeException e) {
            logger.WARN("encode event failed", e);
        } catch (IOException e) {
            logger.ERROR("send syslog event failed", e);
        }
    }
}

enum Level {
    emerg, alert, crit, err, warning, notice, info, debug
}