package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.UUID;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("random")
public class RandomFilter extends BaseTFilter {

    public enum Type {
        uuid, alphabetic, alphanumeric, numeric, ascii, integer;
    }

    @Required
    String target;

    @Required
    Type type;

    int length = 10;

    private Random _random;

    public void init() {
        _random = new Random(System.currentTimeMillis());
    }

    @Override
    public boolean doFilter(TEvent event) {
        Object id;
        switch (type) {
            case uuid:
                id = UUID.randomUUID().toString();
                break;
            case numeric:
                id = RandomStringUtils.randomNumeric(length);
                break;
            case integer:
                id = Math.abs(_random.nextInt()) % length;
                break;
            case alphabetic:
                id = RandomStringUtils.randomAlphabetic(length);
                break;
            case ascii:
                id = RandomStringUtils.randomAscii(length);
                break;
            case alphanumeric:
                id = RandomStringUtils.randomAlphanumeric(length);
                break;
            default:
                return false;
        }
        event.set(target, id);
        return true;
    }
}
