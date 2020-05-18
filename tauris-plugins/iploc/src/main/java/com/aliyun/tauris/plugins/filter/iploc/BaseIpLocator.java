package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TResource;
import com.aliyun.tauris.TPluginInitException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public abstract class BaseIpLocator implements TIPLocator {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(BaseIpLocator.class);

    protected ReentrantLock _lock = new ReentrantLock();

    protected TResource langFile;

    protected Charset charset = Charset.defaultCharset();

    protected boolean translate;

    private Lang lang;

    @Override
    public void prepare() throws TPluginInitException {
        if (langFile != null) {
            try {
                this.lang = new Lang();
                lang.load(langFile.fetch());
                translate = true;
            } catch (Exception e) {
                throw new TPluginInitException("language data init failed", e);
            }
        } else if (translate) {
            InputStream is = BaseIpLocator.class.getClassLoader().getResourceAsStream("plugins/filter/iploc/lang.bin");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                IOUtils.copy(is, os);
                this.lang = new Lang();
                lang.load(os.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final IPInfo locate(String ip) {
        IPInfo val;
        _lock.lock();
        try {
            val = _locate(ip);
            return translate(val);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("resolve ip location rise an exception", e);
            return null;
        } finally {
            _lock.unlock();
        }
    }

    protected static String md5(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return null;
        }
        md.update(data);
        byte[] bs = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bs.length; i++) {
            int v = bs[i] & 0xff;
            if (v < 16) {
                sb.append(0);
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

    abstract IPInfo _locate(String ip);

    protected IPInfo translate(IPInfo ipInfo) {
        if (translate) {
            ipInfo.setCountry(lang.translate(ipInfo.getCountry()));
            ipInfo.setArea(lang.translate(ipInfo.getArea()));
            ipInfo.setRegion(lang.translate(ipInfo.getRegion()));
            ipInfo.setCity(lang.translate(ipInfo.getCity()));
            ipInfo.setIsp(lang.translate(ipInfo.getIsp()));
        }
        return ipInfo;
    }

    private class Lang {
        private static final String CN_BACKBONE = "骨干网";
        private static final String EN_BACKBONE = "Backbone Network";
        private Map<String, String> map;

        public void load(byte[] dataArr) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(dataArr);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            int                 size   = byteBuffer.getInt();
            Map<String, String> newMap = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                newMap.put(getValue(byteBuffer), getValue(byteBuffer));
            }
            map = newMap;
        }

        /**
         * 将中文转为英文
         *
         * @param name 中文
         * @return 对应的英文
         */
        public String translate(String name) {
            if (null == map || StringUtils.isBlank(name)) {
                return name;
            }

            String alias = map.get(name);
            if (StringUtils.isNotBlank(alias)) {
                return alias;
            }

            if (name.contains(CN_BACKBONE)) {
                return name.replaceAll(CN_BACKBONE, EN_BACKBONE);
            }

            StringBuilder sb  = new StringBuilder();
            String[]      arr = name.split("/");
            for (String token : arr) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                sb.append(get(token));
            }
            return sb.toString();
        }

        private String get(String name) {
            String alias = map.get(name);
            return StringUtils.isBlank(alias) ? name : alias;
        }

        private String getValue(ByteBuffer byteBuffer) {
            int    size = byteBuffer.get();
            byte[] arr  = new byte[size];
            byteBuffer.get(arr);
            return new String(arr, charset);
        }
    }
}
