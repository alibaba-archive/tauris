package com.aliyun.tauris.expression;

import com.aliyun.tauris.TObject;
import com.google.common.base.Preconditions;

import java.util.function.BiFunction;

/**
 * Created by ZhangLei on 2018/5/14.
 */
public class CalcExpression extends TExpression {

    private TExpression left;
    private TExpression right;
    private String opChar;
    private BiFunction<Number, Number, Number> op;

    public CalcExpression(TExpression left, TExpression right, String opChar, BiFunction<Number, Number, Number> op) {
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        this.left = left;
        this.right = right;
        this.opChar = opChar;
        this.op = op;
    }

    @Override
    public Object eval(TObject e) {
        Number lv = left.calc(e);
        Number rv = right.calc(e);
        return op.apply(lv, rv);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", left.toString(), opChar, right.toString());
    }
}
