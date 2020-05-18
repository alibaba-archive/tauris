package com.aliyun.tauris.plugins.output;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("mns_topic")
public class MNSTopicOutput extends BaseTOutput {

    private static Logger LOG = LoggerFactory.getLogger("tauris.output.mns_topic");

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_mns_topic_total").help("mns topic write count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_mns_topic_error_total").help("mns topic put error count").create().register();

    @Required
    String endPoint;

    @Required
    String accessKeyId;

    @Required
    String accessKeySecret;

    @Required
    String topicName;

    String defaultFilterTag;

    String filterTagField;

    MNSClient _client;

    CloudTopic _topic;

    public void init() throws TPluginInitException {
        CloudAccount account = new CloudAccount(accessKeyId, accessKeySecret, endPoint);
        _client = account.getMNSClient();
        _topic = getTopic(topicName);
    }

    private CloudTopic getTopic(String topicName) throws TPluginInitException {
        try {
            return _client.getTopicRef(topicName);
        } catch (ServiceException se) {
            LOG.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("TopicNotExist")) {
                    throw new TPluginInitException("Topic " + topicName + " is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    throw new TPluginInitException("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw new TPluginInitException(se.getMessage());
        }
    }

    @Override
    protected void doWrite(TEvent event) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            codec.encode(event, output);
            TopicMessage message = new Base64TopicMessage();
            message.setMessageBody(output.toByteArray());
            String tag = getFilterTag(event);
            if (tag != null) {
                message.setMessageTag(tag);
            }
            _topic.publishMessage(message);
            OUTPUT_COUNTER.inc(1);
        } catch (ClientException ce) {
            ERROR_COUNTER.inc(1);
            LOG.error("Something wrong with the network connection between client and MNS service."
                    + "Please check your network and DNS availablity.", ce);
        } catch (Exception e) {
            ERROR_COUNTER.inc(1);
            LOG.error("Unknown exception happened!", e);
        }
    }

    private String getFilterTag(TEvent event) {
        String tg = filterTagField != null ? (String)event.get(filterTagField) : null;
        if (tg != null) {
            return tg;
        }
        return defaultFilterTag;
    }

    @Override
    public void stop() {
        super.stop();
        _client.close();
    }
}
