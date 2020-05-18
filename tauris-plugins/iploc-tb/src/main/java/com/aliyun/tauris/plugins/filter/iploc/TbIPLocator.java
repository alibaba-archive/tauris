package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.filter.iploc.tbip.TbipImpl;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 淘宝IP库
 * 需要在机器上安装ip库
 * <pre>
 *     sudo yum install ipdata -b current
 * </pre>
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("tbip")
public class TbIPLocator extends BaseIpLocator {

    private Logger Logger = LoggerFactory.getLogger(TbIPLocator.class);

    @Required
    TResource dataFile;

    TbipImpl impl;

    private Lang lang;

    @Override
    public void prepare() throws TPluginInitException {
        prepareLang();
        try {
            byte[] ipData = dataFile.fetch();
            this.impl = this.load(ipData);
            this.dataFile.watch((b) -> {
                TbipImpl impl = this.load(b);
                _lock.lock();
                this.impl = impl;
                _lock.unlock();
                this.impl = impl;
            });
        } catch (Exception e) {
            throw new TPluginInitException("geo data init failed", e);
        }
    }

    private void prepareLang() throws TPluginInitException {
        if (langFile != null) {
            try {
                this.lang = new Lang();
                lang.load(langFile.fetch());
                translate = true;
            } catch (Exception e) {
                throw new TPluginInitException("language data init failed", e);
            }
        } else if (translate) {
            InputStream is = BaseIpLocator.class.getClassLoader().getResourceAsStream("plugins/filter/iploc/tbip/language.txt");
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
    IPInfo _locate(String ip) {
        return impl.getIpInfo(ip);
    }

    @Override
    protected IPInfo translate(IPInfo ipInfo) {
        if (translate) {
            ipInfo.setCountry(lang.getCountry(ipInfo.getCountryId(), ipInfo.getCountry()));
            ipInfo.setArea(lang.getArea(ipInfo.getAreaId(), ipInfo.getArea()));
            ipInfo.setRegion(lang.getRegion(ipInfo.getRegionId(), ipInfo.getRegion()));
            ipInfo.setCity(lang.getCity(ipInfo.getCityId(), ipInfo.getCity()));
            ipInfo.setIsp(lang.getIsp(ipInfo.getIspId(), ipInfo.getIsp()));
            ipInfo.setCounty(lang.getCounty(ipInfo.getCountyId(), ipInfo.getCounty()));
        } else {
            if (ipInfo.getCity() != null && ipInfo.getCity().endsWith("市")) {
                ipInfo.setCity(ipInfo.getCity().replace("市", ""));
            }
            if (ipInfo.getRegion() != null && ipInfo.getRegion().endsWith("省")) {
                ipInfo.setRegion(ipInfo.getRegion().replace("省", ""));
            }
        }
        return ipInfo;
    }

    private TbipImpl load(byte[] ipData) {
        TbipImpl impl = new TbipImpl();
        try {
            impl.init(ipData);
        } catch (IOException e) {
        }
        return impl;
    }

    private class Lang {

        private Map<String, String> country = new HashMap<>();
        private Map<String, String> area = new HashMap<>();
        private Map<String, String> region = new HashMap<>();
        private Map<String, String> city = new HashMap<>();
        private Map<String, String> county = new HashMap<>();
        private Map<String, String> isp = new HashMap<>();

        public void load(byte[] data) {
            try (Scanner scanner = new Scanner(new ByteArrayInputStream(data))) {
                scanner.useDelimiter("\n");
                while (scanner.hasNext()) {
                    String[] ps = scanner.next().split(",");
                    if (ps.length != 4 || !ps[2].equals("en")) {
                        continue;
                    }
                    String type = ps[0];
                    String id = ps[1];
                    String name = ps[3];
                    switch (type) {
                        case "country":
                            country.put(id, name);
                            break;
                        case "area":
                            area.put(id, name);
                            break;
                        case "region":
                            region.put(id, name);
                            break;
                        case "city":
                            city.put(id, name);
                            break;
                        case "isp":
                            isp.put(id, name);
                            break;
                        case "county":
                            county.put(id, name);
                            break;
                    }
                }
            }
        }

        private String get(Map<String, String> data, String id, String def) {
            String val = data.get(id);
            return val == null ? def : val;
        }

        public String getCountry(String id, String def) {
            return get(country, id, def);
        }

        public String getArea(String id, String def) {
            return get(area, id, def);
        }

        public String getRegion(String id, String def) {
            return get(region, id, def);
        }

        public String getCity(String id, String def) {
            return get(city, id, def);
        }

        public String getCounty(String id, String def) {
            return get(county, id, def);
        }

        public String getIsp(String id, String def) {
            return get(isp, id, def);
        }
    }
}
