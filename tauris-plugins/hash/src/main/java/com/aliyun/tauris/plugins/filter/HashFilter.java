package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 生成 md5, sha1, hashcode
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("hash")
public class HashFilter extends BaseTFilter {

    public enum Algorithm {
        md5, sha1, code
    }

    @Required
    String source;

    @Required
    String target;

    @Required
    Algorithm algorithm;

    @Override
    public boolean doFilter(TEvent event) {
        Object s = event.get(source);
        if (s == null) {
            return false;
        }

        Object hash = null;
        switch (algorithm) {
            case md5:
                hash = DigestUtils.md5Hex(s.toString());
                break;
            case sha1:
                hash = DigestUtils.sha1Hex(s.toString());
                break;
            case code:
                hash = new HashCodeBuilder().append(s).toHashCode();
        }
        event.set(target, hash);
        return true;
    }
}
