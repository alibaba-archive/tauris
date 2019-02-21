package com.aliyun.tauris.plugins.filter.keymap;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFilter;
import com.aliyun.tauris.config.parser.Parser;
import com.aliyun.tauris.config.parser.Plugin;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class KeyMapFilterTest
 *
 * @author yundun-waf-dev
 * @date 2018-07-18
 */
public class KeyMapFilterTest {

    @Test
    public void testHashKeyMapper() {
        String cfg = "keymap {" +
                "        source => 'antibot_action';" +
                "        target => 'antibot_action';" +
                "        mapper => hash {" +
                "            values => {" +
                "                'test' : 'report'," +
                "                'close' : 'drop'," +
                "                'captcha' : 'challenge'," +
                "                'login' : 'captcha'" +
                "            }" +
                "        }" +
                "    }";

        TFilter f = parseFilter(cfg);
        TEvent  e = new TEvent();
        e.set("antibot_action", "test");
        Assert.assertNotNull(f.filter(e));
        Assert.assertEquals("report", e.get("antibot_action"));

        e.set("antibot_action", "close");
        Assert.assertNotNull(f.filter(e));
        Assert.assertEquals("drop", e.get("antibot_action"));

        e.set("antibot_action", "captcha");
        Assert.assertNotNull(f.filter(e));
        Assert.assertEquals("challenge", e.get("antibot_action"));

        e.set("antibot_action", "login");
        Assert.assertNotNull(f.filter(e));
        Assert.assertEquals("captcha", e.get("antibot_action"));

    }

    private TFilter parseFilter(String cfg) {
        Plugin f = Parser.parsePlugin(cfg);
        return (TFilter)f.build(TFilter.class);
    }
}
