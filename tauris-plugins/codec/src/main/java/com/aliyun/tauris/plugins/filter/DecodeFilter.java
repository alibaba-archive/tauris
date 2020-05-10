package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TDecoder;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TLogger;

/**
 * 将对象decode
 * Created by ZhangLei on 16/12/10.
 */
@Name("decode")
public class DecodeFilter extends BaseTFilter {


    private TLogger logger;

    String source = TEvent.META_SOURCE;

    String target;

    @Required
    TDecoder decoder;

    public DecodeFilter() {
    }

    public void init() {
        logger = TLogger.getLogger(this);
    }

    @Override
    public boolean doFilter(TEvent event) {
        try {
            Object val = event.get(source);
            if (val == null) {
                logger.warn("plugin:{} - source {} is null", id(), source);
                return false;
            }
            decoder.decode((String) val, event, target);
            return true;
        } catch (DecodeException e) {
            logger.WARN2("decode error", e.getSource());
            return false;
        } catch (Exception e) {
            logger.EXCEPTION(e);
            return false;
        }
    }
}
