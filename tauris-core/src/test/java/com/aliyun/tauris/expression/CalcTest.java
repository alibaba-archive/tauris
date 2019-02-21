package com.aliyun.tauris.expression;

import com.aliyun.tauris.TEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangLei on 17/5/26.
 */
public class CalcTest {

    @Test
    public void test() {

        TEvent e = new TEvent("");
        e.setField("name", "world");
        e.setField("empty", "");
        e.setField("status", 302);
        e.setField("flag", false);
        e.addMeta("author", "chuanshi.zl");
        Assert.assertTrue(TExpression.build("1 + 1 == 2").check(e));
        Assert.assertTrue(TExpression.build("11 - 1 == 10").check(e));
        Assert.assertTrue(TExpression.build("10 * 2 == 20").check(e));
        Assert.assertTrue(TExpression.build("20 / 2 == 10").check(e));
        Assert.assertTrue(TExpression.build("19 % 10 == 9").check(e));

        Assert.assertEquals(TExpression.build("19 % 10").calc(e).intValue(), 19 % 10);
        Assert.assertEquals(TExpression.build("1 + 2 << 3").calc(e).intValue(), 1 + 2 << 3);
        Assert.assertEquals(TExpression.build("1 + (2 << 3)").calc(e).intValue(), 1 + (2 << 3));
        Assert.assertEquals(TExpression.build("( 1 ^ 5) + (2 << 3)").calc(e).intValue(),( 1 ^ 5) + (2 << 3));
        Assert.assertEquals(TExpression.build("2 * 3 + 2").calc(e).intValue(), 2 * 3 + 2);
        Assert.assertEquals(TExpression.build("4 | 3 + 3").calc(e).intValue(), 4 | 3 + 3);
        Assert.assertEquals(TExpression.build("4 | 3 * 3").calc(e).intValue(), 4 | 3 * 3);
    }


}
