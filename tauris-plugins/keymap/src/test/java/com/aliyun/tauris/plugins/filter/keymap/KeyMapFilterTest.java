package com.aliyun.tauris.plugins.filter.keymap;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFilter;
import com.aliyun.tauris.TPluginResolver;
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
    public void testHashKeyMapper() throws Exception {
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
        TEvent  e = new DefaultEvent();
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

    private TFilter parseFilter(String cfg)  throws Exception {
        Plugin f = Parser.parsePlugin(cfg);
        TFilter real = TPluginResolver.resolver().resolve(TFilter.class, f.getName());
        return (TFilter)f.marshal(real);
    }
}
