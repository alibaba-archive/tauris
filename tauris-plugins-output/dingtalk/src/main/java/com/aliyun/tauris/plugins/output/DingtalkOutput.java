package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.metric.Counter;
import com.aliyun.tauris.plugins.output.dingtalk.TDingMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by ZhangLei on 16/12/8.
 */
@Name("ding")
public class DingtalkOutput extends BaseTOutput {


    private static Counter OUTPUT_COUNTER = Counter.build().name("output_dingtalk_total").labelNames("id").help("dingtalk output count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_dingtalk_error_total").labelNames("id").help("dingtalk error count").create().register();

    private static Logger logger = LoggerFactory.getLogger("tauris.output.dingtalk");

    public static String SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=%s";

    String[] tokens;

    String tokensField;

    @Required
    TDingMessage message;

    public void init() throws TPluginInitException {
        if (tokens == null && tokensField == null) {
            throw new TPluginInitException("neither the tokens nor the tokens_field is defined");
        }
    }

    @Override
    protected void doWrite(TEvent event) {
        HttpClient httpclient = HttpClients.createDefault();

        for (String t : tokens(event)) {
            String url = String.format(SEND_URL, t);
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");

            String msg = message.format(event);
            StringEntity se = new StringEntity(msg, "utf-8");
            httppost.setEntity(se);

            try {
                HttpResponse response = httpclient.execute(httppost);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    OUTPUT_COUNTER.labels(id()).inc();
                }
            } catch (Exception e) {
                logger.error("send ding message error", e);
                ERROR_COUNTER.labels(id()).inc();
            }
        }
    }

    private String[] tokens(TEvent event) {
        if (tokens != null) {
            return tokens;
        }
        Object f = event.get(tokensField);
        if (f.getClass().isArray()) {
            return (String[])f;
        } else if (f instanceof Collection) {
            Collection<String> e = (Collection<String>) f;
            String[] ts = e.toArray(new String[e.size()]);
            return ts;
        }
        return new String[0];
    }

}
