package com.aliyun.tauris.plugins.filter;

import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TFilter;
import com.aliyun.tauris.config.parser.Parser;
import com.aliyun.tauris.config.parser.Plugin;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 17/8/28.
 */
public class RegexFilterTest {


    @Test
    public void test() throws Exception {
        String log = "111.19.32.76 10380 \"120.55.137.50:80\" [2017-08-28T10:59:59+08:00] 0 \"-\" \"-\" \"-\" \"none\" 0 30 \"0.030\" yundunwafengine3.st3 \"time.wasu.tv\" \"GET /now HTTP/1.1\" 200 \"200\" 25 \"-\" \"-\" \"-\" \"-\" \"Apache-HttpClient/UNAVAILABLE (java 1.4)\" m.yundun.waf.1 111.19.32.76 \"\" \"-\" \"24417\" \"pass\" \"false\" \"0\" \"time.wasu.tv\" \"-\" \"-\" \"119\" \"-\" \"-\" \"1395025539052465\" \"-\" \"time.wasu.tv\" \"-\" \"784c10e615038891990645293e\"";
        RegexFilter cf = new RegexFilter();
        cf.patternFile = new File(RegexFilterTest.class.getClassLoader().getResource("wafaccesslog.patterns").toURI());
        cf.init();;

        TEvent e = new DefaultEvent(log);
        cf.filter(e);
        System.out.println(e.get("httpCookie"));
        Assert.assertEquals("111.19.32.76", e.get("remoteAddr"));
    }

    @Test
    public void test2() throws Exception {
        String log = "49.220.191.2 60417 \"203.195.153.178:80\" [2017-08-28T16:10:22+08:00] 0 \"-\" \"-\" \"-\" \"none\" 0 31 \"0.031\" yundunwafengine33.cloud.em21 \"www.gratong.com\" \"POST /Account/SendCode HTTP/1.1\" 200 \"200\" 39 \"http://www.gratong.com/Account/SendCode\" \"6.240.189.4\" \"-\" \"-\" \"Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1)\" m.yundun.waf.1 49.220.191.2 \"\" \"-\" \"82260\" \"pass\" \"false\" \"0\" \"www.gratong.com\" \"application/x-www-form-urlencoded; Charset=UTF-8\" \"-\" \"402\" \"-\" \"-\" \"1874899910769048\" \"-\" \"www.gratong.com\" \"-\" \"781bad2f15039078225096879e\"";
        List<String> lines = FileUtils.readLines(new File(RegexFilterTest.class.getClassLoader().getResource("wafaccesslog.patterns").toURI()));

        for (int i = 10; i < lines.size(); i++) {
            List<String> sl = lines.subList(0, i);
            String pattern = String.join(" ", sl) + " .*";
            RegexFilter cf = new RegexFilter();
            cf.pattern = Pattern.compile(pattern);
            cf.init();;
            TEvent e = new DefaultEvent(log);
            cf.filter(e);
            if (e.get("remoteAddr") == null) {
                System.out.println(sl.get(sl.size() - 1));
                break;
            }
        }
    }


    @Test
    public void testFromConfig1() throws Exception {
        String cfg = "regex { " +
                "       source => '@source';  " +
                "       pattern => 'antibot_cc_(?<antibotRule>\\d+)_(?<antibotAction>\\w+)'; " +
                "       underscore => true;" +
                "       new_fields => { " +
                "           'ok' : 'true', " +
                "           'value':'%{val}'" +
                "       }" +
                "}";

        TFilter f = parseFilter(cfg);
        TEvent e = new DefaultEvent("antibot_cc_123_ok");
        e.set("val", "world");
        Assert.assertNotNull(f.filter(e));

        Assert.assertEquals("ok", e.get("antibot_action"));
        Assert.assertEquals("123", e.get("antibot_rule"));
        Assert.assertEquals("true", e.get("ok"));
        Assert.assertEquals("world", e.get("val"));
    }

    @Test
    public void testFromConfig2() throws Exception {
        String cfg = "regex { " +
                "       source => '@source';  " +
                "       pattern => 'antibot_cc_(?<antibotRule>\\d+)_(?<antibotAction>\\w+)'; " +
                "       new_fields => { " +
                "           'ok' : 'true', " +
                "           'value':'%{val}'" +
                "       }" +
                "}";

        TFilter f = parseFilter(cfg);
        TEvent e = new DefaultEvent("antibot_cc_123_ok");
        e.set("val", "world");
        Assert.assertNotNull(f.filter(e));

        Assert.assertEquals("ok", e.get("antibotAction"));
        Assert.assertEquals("123", e.get("antibotRule"));
        Assert.assertEquals("true", e.get("ok"));
        Assert.assertEquals("world", e.get("val"));
    }


    @Test
    public void testFromConfig3() throws Exception {
        String cfg = "regex { " +
                "       source => '@source';  " +
                "       pattern => '^(?<requestPath>[^\\?]+)(\\?(?<querystring>.+))?';" +
                "       underscore => true;" +
                "     }";

        TFilter f = parseFilter(cfg);
        TEvent e = new DefaultEvent("/mmmmmmmmmmm?aaaaaa=111111");
        Assert.assertNotNull(f.filter(e));

        Assert.assertEquals("/mmmmmmmmmmm", e.get("request_path"));
        Assert.assertEquals("aaaaaa=111111", e.get("querystring"));
    }

    private TFilter parseFilter(String cfg) {
        Plugin f = Parser.parsePlugin(cfg);
        return (TFilter)f.build(TFilter.class);
    }

    @Test
    public void test3() {
        String cfg = "regex { " +
                "       on => '$cc_rule_id is not empty';" +
                "       source => 'cc_rule_id';  " +
                "       pattern => '(?<antibot>intelligence|algorithm)_(?<antibotRule>.+)" +
                "       underscore => true;" +
                "     }";

        TFilter f = parseFilter(cfg);
        TEvent e = new DefaultEvent();
        e.set("cc_rule_id", "");
        Assert.assertNotNull(f.filter(e));

        Assert.assertEquals("/mmmmmmmmmmm", e.get("request_path"));
        Assert.assertEquals("aaaaaa=111111", e.get("querystring"));
    }
}
