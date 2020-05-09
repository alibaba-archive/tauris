package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEncoder;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TLogger;

/**
 * 将对象encode
 * Created by ZhangLei on 16/12/10.
 */
@Name("encode")
public class EncodeFilter extends BaseTFilter {

    private TLogger logger;

    @Required
    TEncoder encoder;

    @Required
    String target;

    public void init() {
        logger = TLogger.getLogger(this);
        encoder.init();
    }

    @Override
    public boolean doFilter(TEvent event) {
        try {
            encoder.encode(event, target);
            return true;
        } catch (Exception e) {
            logger.ERROR("encode error", e);
            return false;
        }
    }
}
