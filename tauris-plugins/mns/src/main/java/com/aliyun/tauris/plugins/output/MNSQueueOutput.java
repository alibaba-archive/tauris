package com.aliyun.tauris.plugins.output;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.metrics.Counter;

import com.aliyun.tauris.TLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ZhangLei on 17/5/28.
 */
@Name("mns_queue")
public class MNSQueueOutput extends BaseTOutput {


    private static Counter OUTPUT_COUNTER = Counter.build().name("output_mns_queue_total").labelNames("id").help("mns queue write count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_mns_queue_error_total").labelNames("id").help("mns queue put error count").create().register();

    private TLogger logger;

    @Required
    String endpoint;

    @Required
    String accessKeyId;

    @Required
    String accessKeySecret;

    @Required
    String queueName;

    Integer batchSize;

    /**
     * 单位毫秒
     * 默认情况下缓冲区的消息会被立即发送到服务端，即使缓冲区的空间并没有被用完。
     * 可以将该值设置为大于0的值，这样发送者将等待一段时间后，再向服务端发送请求，以实现每次请求可以尽可能多的发送批量消息。
     * batchSize和linger是两种实现让客户端每次请求尽可能多的发送消息的机制，它们可以并存使用，并不冲突。
     */
    int linger = 1000; // unit millis

    private MNSClient client;

    private CloudQueue queue;

    private List<Message> buffer;

    private final Object batchLock = new Object();

    private Thread batchThread;
    private long   lastSentTime;


    public void init() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        CloudAccount account = new CloudAccount(accessKeyId, accessKeySecret, endpoint);
        client = account.getMNSClient();
        queue = getQueue(queueName);
        if (batchSize != null) {
            if (batchSize >= 16) {
                throw new TPluginInitException("batch size to big, max 16");
            }
            buffer = new ArrayList<>(batchSize);
            batchThread = new Thread(() -> {
                while (true) {
                    if ((!buffer.isEmpty() && System.currentTimeMillis() - lastSentTime > linger)) {
                        batchSendMessages();
                    }
                    try {
                        Thread.sleep(linger);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
        }

        batchThread.start();
    }

    private CloudQueue getQueue(String queueName) throws TPluginInitException {
        try {
            return client.getQueueRef(queueName);
        } catch (ServiceException se) {
            logger.ERROR("MNS exception requestId:%s", se, se.getRequestId());
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    return null;
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    throw new TPluginInitException("The request is time expired. Please check your local machine timeclock");
                }
            }
            throw new TPluginInitException(se.getMessage());
        }
    }

    @Override
    protected void doWrite(TEvent event) {
        try {
            Message message = encodeToMessage(event);
            if (batchThread != null) {
                synchronized (batchLock) {
                    buffer.add(message);
                    if (buffer.size() == batchSize) {
                        batchSendMessages();
                    }
                }
            } else {
                sendMessage(message);
            }
        } catch (EncodeException e) {
            logger.ERROR("encode event to message error", e);
        }

    }

    private void sendMessage(Message message) {
        try {
            message.setMessageBody(message.getMessageBody());
            queue.putMessage(message);
            OUTPUT_COUNTER.labels(id()).inc(1);
        } catch (ClientException ce) {
            ERROR_COUNTER.labels(id()).inc(1);
            logger.ERROR("Something wrong with the network connection between client and MNS service."
                    + "Please check your network and DNS availablity.", ce);
        } catch (ServiceException se) {
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    logger.ERROR("Queue " + queueName + " is not exist.Please create before use", se);
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    logger.ERROR("The request is time expired. Please check your local machine timeclock", se);
                }
                logger.error("send message failed", se);
            }
        } catch (Exception e) {
            ERROR_COUNTER.inc(1);
            logger.EXCEPTION(e);
        }
    }

    private void batchSendMessages() {
        synchronized (batchLock) {
            try {
                queue.batchPutMessage(buffer);
                OUTPUT_COUNTER.labels(id()).inc(1);
                buffer.clear();
                lastSentTime = System.currentTimeMillis();
            } catch (ClientException ce) {
                ERROR_COUNTER.labels(id()).inc(1);
                logger.ERROR("Something wrong with the network connection between client and MNS service."
                        + "Please check your network and DNS availablity.", ce);
            } catch (ServiceException se) {
                if (se.getErrorCode() != null) {
                    if (se.getErrorCode().equals("QueueNotExist")) {
                        logger.ERROR("Queue " + queueName + " is not exist.Please create before use", se);
                    } else if (se.getErrorCode().equals("TimeExpired")) {
                        logger.ERROR("The request is time expired. Please check your local machine timeclock", se);
                    }
                    logger.ERROR("send message failed", se);
                }
            } catch (Exception e) {
                ERROR_COUNTER.inc(1);
                logger.ERROR("Unknown exception happened!", e);
            }
        }
    }

    private Message encodeToMessage(TEvent event) throws EncodeException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            codec.encode(event, output);
            output.flush();
            Message message = new Message();
            message.setMessageBody(output.toByteArray());
            return message;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void stop() {
        super.stop();
        client.close();
    }
}
