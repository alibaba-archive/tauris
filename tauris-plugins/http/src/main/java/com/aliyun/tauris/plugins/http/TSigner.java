package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;
import org.apache.http.client.methods.HttpUriRequest;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by ZhangLei on 2017/11/15.
 */
@Type
public interface TSigner extends TPlugin {

    String AUTHORIZATION_HEADER = "Authorization";

    void sign(Map<String, String> headers, HttpUriRequest request, @Nullable byte[] content);
}
