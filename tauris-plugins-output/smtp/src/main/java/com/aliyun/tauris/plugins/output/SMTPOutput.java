package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;
import com.hubspot.jinjava.Jinjava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("smtp")
public class SMTPOutput extends BaseTOutput {


    private static Counter OUTPUT_COUNTER = Counter.build().name("output_smtp_total").labelNames("id").help("smtp output count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_smtp_error_total").labelNames("id").help("smtp error count").create().register();

    private static Logger logger = LoggerFactory.getLogger("tauris.output.smtp");

    @Required
    String host;

    int port = 25;

    boolean auth;

    String username;

    String password;


    @Required
    InternetAddress from;

    @Required
    InternetAddress[] to;

    InternetAddress[] cc;

    @Required
    String subject;

    String content;

    TResource contentTemplate;

    String contentType = "text/html; charset=UTF-8";

    Map<String, Object> props = new HashMap<>();

    private Session session;

    private Jinjava parser = new Jinjava();

    public void init() throws TPluginInitException {
        if (content == null && contentTemplate == null) {
            throw new TPluginInitException("content or content_template must be set");
        }

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", String.valueOf(port));
        props.setProperty("mail.smtp.auth", String.valueOf(auth));
        for (Map.Entry<String, Object> e : this.props.entrySet()) {
            props.put(e.getKey(), e.getValue().toString());
        }
        if (auth) {
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            session = Session.getDefaultInstance(props, authenticator);
        } else {
            session = Session.getDefaultInstance(props);
        }

    }

    public void start() throws Exception {
        if (contentTemplate != null) {
            content = new String(contentTemplate.fetch());
        }
    }

    @Override
    protected void doWrite(TEvent event) {
        HashMap<String, Object> data = new HashMap<String, Object>() {
            {
                put("__time__", event.getTimestamp());
                put("__date__", new Date(event.getTimestamp()));
                put("__meta__", event.getMeta());
                putAll(event.getFields());
            }
        };
        String subject = parser.render(this.subject, data);
        String content = parser.render(this.content, data);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.setSubject(subject);
            message.addRecipients(Message.RecipientType.TO, to);
            if (cc != null && cc.length > 0) {
                message.addRecipients(Message.RecipientType.CC, cc);
            }
            message.setSentDate(new Date(event.getTimestamp()));
            message.setContent(content, contentType);
            message.saveChanges();

            Transport transport = session.getTransport();
            transport.connect(username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            OUTPUT_COUNTER.labels(id()).inc();
        } catch (Exception e) {
            ERROR_COUNTER.labels(id()).inc();
            logger.error("send smtp mail failed", e);
        }
    }

}
