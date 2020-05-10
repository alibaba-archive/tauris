package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TLogger;
import com.sproutsocial.nsq.*;


/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("nsq")
public class NSQInput extends BaseTInput {

    private TLogger logger;

    @Required
    String[] lookupHosts;

    @Required
    String topic;

    @Required
    String channel;

    int maxInFlight = 200;

    private Subscriber subscriber;

    @Override
    public void doInit() {
        this.logger = TLogger.getLogger(this);
        subscriber = new Subscriber(lookupHosts);
    }

    public void run() throws Exception {
        subscriber.setDefaultMaxInFlight(maxInFlight);
        subscriber.subscribe(topic, channel, new NSQMessageDataHandler());
    }

    @Override
    public void close() {
        super.close();
        subscriber.stop();
        logger.info("nsq input {} closed", id());
    }

    private class NSQMessageDataHandler implements MessageDataHandler {
        @Override
        public void accept(byte[] data) {
            try {
                TEvent event = getCodec().decode(new String(data, charset));
                putEvent(event);
            } catch (DecodeException e) {
                logger.WARN2("decode error", e, e.getSource());
            } catch (InterruptedException e) {

            }
        }
    }
}
