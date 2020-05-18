package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;
import org.apache.http.client.methods.HttpUriRequest;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type
public interface TSigner extends TPlugin {

    String AUTHORIZATION_HEADER = "Authorization";

    void sign(Map<String, String> headers, HttpUriRequest request, @Nullable byte[] content);
}
