package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.TPlugin;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by ZhangLei on 2017/11/15.
 */
public interface TAuthenticator extends TPlugin {

    boolean check(String credential, HttpServletRequest request);
}
