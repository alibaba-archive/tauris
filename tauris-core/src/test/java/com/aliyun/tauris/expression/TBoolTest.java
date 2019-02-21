package com.aliyun.tauris.expression;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.expression.TExpression;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangLei on 17/5/26.
 */
public class TBoolTest {

    @Test
    public void test() {
        Assert.assertTrue(TExpression.build("2 > 1").check(null));
        Assert.assertFalse(TExpression.build("2 < 1").check(null));

        TEvent e = new TEvent("");
        e.setField("name", "world");
        e.setField("empty", "");
        e.setField("status", 302);
        e.setField("flag", false);
        e.setField("array", new String[]{"v1", "v2", "v3"});
        e.addMeta("author", "chuanshi.zl");
        Assert.assertTrue(TExpression.build("1 > 0").check(e));
        Assert.assertFalse(TExpression.build("0 > 0").check(e));
        Assert.assertTrue(TExpression.build("$empty is empty").check(e));
        Assert.assertTrue(TExpression.build("[  ] is empty").check(e));
        Assert.assertTrue(TExpression.build("$name == 'world'").check(e));
        Assert.assertTrue(TExpression.build("@author == 'chuanshi.zl'").check(e));
        Assert.assertTrue(TExpression.build("5 in [1,2,3,4,5,6]").check(e));
        Assert.assertTrue(TExpression.build("true in [true, false]").check(e));
        Assert.assertTrue(TExpression.build("'hello' in ['hello', 'world', '!']").check(e));
        Assert.assertTrue(TExpression.build("9.5 in [1.1, 5.5, 7.7, 9.5, 11.1]").check(e));
        Assert.assertFalse(TExpression.build("'hello' not in ['hello', 'world', '!']").check(e));
        Assert.assertTrue(TExpression.build("'1.1.1.1' =~ /[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+/").check(e));
        Assert.assertTrue(TExpression.build("'1.1.1.1' is host").check(e));
        Assert.assertTrue(TExpression.build("'www.taobao.com' is host").check(e));
        Assert.assertTrue(TExpression.build("$not_exist is null").check(e));
        Assert.assertTrue(TExpression.build("2 > 1").check(e));
        Assert.assertTrue(TExpression.build("1 < 2").check(e));
        Assert.assertTrue(TExpression.build("$status >= 300 && $status < 400").check(e));

        Assert.assertTrue(TExpression.build("$flag != true").check(e));
        Assert.assertTrue(TExpression.build("$not_exist != true").check(e));
        Assert.assertFalse(TExpression.build("$not_exist").check(e));

        Assert.assertTrue(TExpression.build("'abc' in 'abcdefg'").check(e));
        Assert.assertTrue(TExpression.build("'fgh' not in 'abcdefg'").check(e));
        Assert.assertTrue(TExpression.build("'a' in ['a', 'b', 'c']").check(e));
        Assert.assertTrue(TExpression.build("'e' not in ['a', 'b', 'c']").check(e));
        Assert.assertTrue(TExpression.build("'v2' in $array").check(e));
        Assert.assertTrue(TExpression.build("'v4' not in $array").check(e));
    }

    @Test
    public void test2() {
        TEvent e = new TEvent("");
        e.setField("acl_id", 999l);
        Assert.assertTrue(TExpression.build("$acl_id is not null && $acl_id > 990").check(e));
    }

    @Test
    public void test3() {
        TEvent e = new TEvent("");
        e.setField("upstream_addr", "");
        Assert.assertTrue(TExpression.build("'99@9' is not host").check(e));
        Assert.assertFalse(TExpression.build("$upstream_addr is not empty").check(e));
    }


    @Test
    public void test4() {
        TEvent e = new TEvent("");
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "my");
        e.set("@tags", tags);
        Assert.assertTrue(TExpression.build("@tags is not null && @tags.source is not empty").check(e));
    }

    @Test
    public void test5() {
        TEvent e = new TEvent("");
        e.addMeta("num", "99");
        e.addMeta("alpha", "my");
        e.addMeta("duplex", "my_99");
        Assert.assertTrue(TExpression.build("@num =~ /[0-9]+/").check(e));
        Assert.assertTrue(TExpression.build("@alpha =~ /[a-z]+/").check(e));
        Assert.assertTrue(TExpression.build("@duplex =~ /[a-z_0-9]+/").check(e));
    }

    @Test
    public void test6() {
        TEvent e = new TEvent("");
        e.addMeta("topic", "warehouse");
        e.setField("product", "gf");
        Assert.assertTrue(TExpression.build("@topic == 'warehouse' && $product == 'gf'").check(e));
        Assert.assertFalse(TExpression.build("@topic == 'warehouse' && $product == 'waf'").check(e));
    }

    @Test
    public void test7() {
        String expr = "$user_info is null || ($user_info.gc_level != 'GC6' && $user_info.gc_level != 'GC7')";

        Map<String, Object> userinfo = new HashMap<>();
        userinfo.put("gc_level", "GC3");

        TEvent e = new TEvent("");
        e.setField("user_info", userinfo);
        System.out.println(TExpression.build("$user_info is null || ($user_info.gc_level != 'GC6' && $user_info.gc_level != 'GC7')"));
        Assert.assertTrue(TExpression.build("$user_info is null || ($user_info.gc_level != 'GC6' && $user_info.gc_level != 'GC7')").check(e));

        userinfo.put("gc_level", "GC6");
        Assert.assertFalse(TExpression.build("$user_info is null || ($user_info.gc_level != 'GC6' && $user_info.gc_level != 'GC7' && $user_info.gc_level != 'GC5')").check(e));
    }

    @Test
    public void test8() {
        TEvent e = new TEvent("");
        Assert.assertFalse(TExpression.build("$geo_block").check(e));
        Assert.assertFalse(TExpression.build("$geo_block is not null && $geo_block").check(e));

        e.setField("geo_block", true);
        Assert.assertTrue(TExpression.build("$geo_block is not null && $geo_block").check(e));

        System.out.println(TExpression.build("$geo_block is not null && $geo_block"));


    }

    @Test
    public void test9() {
        TEvent e = new TEvent("");
        e.setField("tmd_owner", "antibot_cc_4005_close");
        e.setField("http_user_agent", "Scoot/1.8.5 Android/5.0.2");
        Assert.assertTrue(TExpression.build("'Android' in $http_user_agent").check(e));

        Assert.assertTrue(TExpression.build("$tmd_owner is not empty && $tmd_owner =~ /^antibot_cc_\\d+_\\w+$/").check(e));
    }

    @Test
    public void test10() {
        TEvent e = new TEvent("");
        e.setField("warehouse", "cn334");
        Assert.assertTrue(TExpression.build("$warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/").check(e));
        e.setField("warehouse", "am5");
        Assert.assertTrue(TExpression.build("$warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/").check(e));
        e.setField("warehouse", "em21");
        Assert.assertTrue(TExpression.build("$warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/").check(e));
        e.setField("warehouse", "cn-beijing");
        Assert.assertTrue(TExpression.build("$warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/").check(e));
        e.setField("warehouse", "ap-southeast-1");
        Assert.assertTrue(TExpression.build("$warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/").check(e));

        e.setField("warehouse", "123");
        Assert.assertTrue(TExpression.build("not ($warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/)").check(e));

        e.setField("warehouse", "shanghai");
        Assert.assertTrue(TExpression.build("not ($warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/)").check(e));


        e.setField("warehouse", "cn334!");
        Assert.assertTrue(TExpression.build("not ($warehouse =~ /([a-z]{2}[\\d]{1,3}|[a-z]{1,3}\\-[a-z0-9\\-]{1,20})/)").check(e));


    }

    @Test
    public void test1x() {
        TEvent e = new TEvent("");
        e.setField("even", 7 * 1000);
        Assert.assertTrue(TExpression.build("($even / 1000) % 2 == 1").check(e));
        Assert.assertFalse(TExpression.build("($even / 1000) % 2 == 0").check(e));
        e.setField("odd", 8 * 1000);
        Assert.assertTrue(TExpression.build("($odd / 1000) % 2 == 0").check(e));
        Assert.assertFalse(TExpression.build("($odd / 1000) % 2 == 1").check(e));
    }

    @Test
    public void test1y() {
        String expr = "$block_action is empty && $tmd_blocks == 1 && ($cc_test is null || not $cc_test)";
        TExpression expression = TExpression.build(expr);
        System.out.println(expression);

        TEvent e = new TEvent("");
        e.set("tmd_blocks", 1);

        Assert.assertTrue(expression.check(e));

        expr = "$block_action is empty && $geo_block_action == 'block' && ($geo_block_test is not null && not $geo_block_test)";
        System.out.println(TExpression.build(expr));

    }
}


