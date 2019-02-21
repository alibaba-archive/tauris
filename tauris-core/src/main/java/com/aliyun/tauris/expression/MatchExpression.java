package com.aliyun.tauris.expression;

import com.aliyun.tauris.TObject;

import java.util.regex.Pattern;

/**
 * Created by ZhangLei on 17/5/26.
 */
public class MatchExpression extends TExpression {

    private TExpression left;
    private Pattern     pattern;

    public MatchExpression(TExpression left, Pattern pattern) {
        this.left = left;
        this.pattern = pattern;
    }

    @Override
    public Object eval(TObject e) {
        Object bo = left.eval(e);
        if (bo == null) return false;
        return pattern.matcher(bo.toString()).matches();
    }
}
