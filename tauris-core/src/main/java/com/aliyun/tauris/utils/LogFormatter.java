package com.aliyun.tauris.utils;

import java.util.*;

/**
 * Created by ZhangLei on 16/11/3.
 */
public interface LogFormatter {

    List<String> getColumns();

    Map<String, String> format(String log);

}
