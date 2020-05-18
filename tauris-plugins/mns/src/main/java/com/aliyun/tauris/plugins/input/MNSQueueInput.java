package com.aliyun.tauris.plugins.input;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TLogger;


/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("mns")
public class MNSQueueInput extends BaseTInput {

    private TLogger logger;

    @Required
    String endpoint;

    @Required
    String accessKeyId;

    @Required
    String accessKeySecret;

    @Required
    String queueName;

    boolean deleteBeforeRead = true;

    volatile boolean running = true;

    MNSClient client;


    public void doInit() {
        this.logger = TLogger.getLogger(this);
        CloudAccount account = new CloudAccount(accessKeyId, accessKeySecret, endpoint);
        client = account.getMNSClient();
    }

    public void run() throws Exception {
        CloudQueue queue = client.getQueueRef(queueName);
        while (running) {
            try {
                Message msg = queue.popMessage();
                if (msg != null) {
                    consumeMessage(queue, msg);
                } else {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                logger.ERROR("consume thread has been interrupted", e);
                break;
            } catch (Exception e) {
                logger.EXCEPTION(e);
            }
        }
    }

    private void consumeMessage(CloudQueue queue, Message msg) throws Exception {
        try {
            TEvent event = codec.decode(msg.getMessageBodyAsString(), getEventFactory());
            event.setTimestamp(msg.getEnqueueTime().getTime());
            event.addMeta("id", msg.getMessageId());
            event.addMeta("handle", msg.getReceiptHandle());
            event.addMeta("queue_name", queueName);
            event.addMeta("priority", msg.getPriority());
            event.addMeta("dequeue_count", msg.getDequeueCount().longValue());
            this.putEvent(event);
            if (deleteBeforeRead) {
                queue.deleteMessage(msg.getReceiptHandle());
            }
        } catch (DecodeException ce) {
            logger.WARN2("decode error", ce, ce.getSource());
        } catch (ClientException ce) {
            logger.ERROR("Something wrong with the network connection between client and MNS service.", ce);
        } catch (ServiceException se) {
            logger.ERROR("MNS exception requestId:%s", se, se.getRequestId());
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    logger.ERROR("Queue is not exist.Please create before use");
                    throw new Exception("Queue " + queueName + " is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    logger.ERROR("The request is time expired. Please check your local machine timeclock");
                    throw new Exception("Queue " + queueName + " - The request is time expired. Please check your local machine timeclock");
                }
            }
        }
    }

    @Override
    public void close() {
        super.close();
        running = false;
        client.close();
    }
}
