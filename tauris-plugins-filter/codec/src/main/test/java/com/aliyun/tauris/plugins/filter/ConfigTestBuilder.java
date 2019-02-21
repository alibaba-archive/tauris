package com.aliyun.tauris.plugins.filter;/**
 * Created by ZhangLei on 2018/6/5.
 */

import com.aliyun.tauris.TFilter;
import com.aliyun.tauris.config.TConfig;
import com.aliyun.tauris.config.TConfigSource;
import com.aliyun.tauris.config.TConfigText;

import java.util.List;

/**
 * @author yundun-waf-dev
 * @date 2018-06-05
 */
public class ConfigTestBuilder {


    public static List<TFilter> buildFilters(String config) {
        String input = "input { stdin {} }\n";
        String filter = String.format("filter {\n %s \n}\n", config);
        String output = "output { empty {} }\n";

        String cfg = input + filter + output;

        TConfigSource cs = new TConfigText(cfg);
        TConfig c = new TConfig(cs);
        c.load();
        return c.getFilters();
    }
}
