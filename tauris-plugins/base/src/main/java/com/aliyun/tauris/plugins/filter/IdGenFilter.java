package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("idgen")
public class IdGenFilter extends BaseTFilter {

    public enum IdType {
        uuid, alphabetic, alphanumeric, numeric, ascii;
    }

    String target = "@id";

    @Required
    IdType type;

    int length = 10;

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
