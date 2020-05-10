package com.aliyun.tauris.plugins.resource;

import com.aliyun.tauris.utils.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by ZhangLei on 2018/4/13.
 */
public class HttpResource extends AbstractScheduleUpdateResource {

    @Override
    public byte[] fetch() throws Exception {
        BufferedReader rd = null;
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) uri.toURI().toURL().openConnection();
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code < 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();
                return result.toString().getBytes();
            } else {
                throw new IOException("read url failed, status:" + code);
            }
        } finally {
            IOUtils.closeQuietly(rd);
        }
    }

    @Override
    public String toString() {
        return getURI().toString();
    }
}
