package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * IPIP库
 * https://www.ipip.net/ip.html
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("ipip")
public class IPIPLocator extends BaseIpLocator {

    @Required
    TResource dataFile;

    private int offset;
    private int[] index = new int[65536];
    private ByteBuffer dataBuffer;
    private ByteBuffer indexBuffer;

    @Override
    public void prepare() throws TPluginInitException {
        super.prepare();
        try {
            this.load(dataFile.fetch());
            this.dataFile.watch((b) -> {
                _lock.lock();
                this.load(b);
                _lock.unlock();
            });
        } catch (Exception e) {
            throw new TPluginInitException("geo data init failed", e);
        }
    }

    @Override
    IPInfo _locate(String ip) {
        String[] ips = ip.split("\\.");
        int      prefix_value  = (Integer.valueOf(ips[0]) * 256 + Integer.valueOf(ips[1]));
        long     ip2long_value = ip2long(ip);
        int start = index[prefix_value];
        int max_comp_len = offset - 262144 - 4;
        long tmpInt;
        long index_offset = -1;
        int index_length = -1;
        byte b = 0;
        for (start = start * 9 + 262144; start < max_comp_len; start += 9) {
            tmpInt = int2long(indexBuffer.getInt(start));
            if (tmpInt >= ip2long_value) {
                index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
                index_length = ((0xFF & indexBuffer.get(start + 7)) << 8) + (0xFF & indexBuffer.get(start + 8));
                break;
            }
        }

        byte[] areaBytes;

        dataBuffer.position(offset + (int) index_offset - 262144);
        areaBytes = new byte[index_length];
        dataBuffer.get(areaBytes, 0, index_length);

        String[] result = (new String(areaBytes, Charset.forName("UTF-8"))).split("\t", -1);
        if (result.length < 3) {
            return null;
        }
        String country = result[0];
        String countryId = result[11];
        String city = result[2];
        String cityId = result[9];
        IPInfo ipInfo = new IPInfo();
        ipInfo.setCountry(country);
        ipInfo.setRegion(result[1]);
        ipInfo.setCity(city);
        //3 Organization
        ipInfo.setIsp(result[4]);
        ipInfo.setLatitude(parseFloat(result[5]));
        ipInfo.setLongitude(parseFloat(result[6]));
        //7 TimeZone Asia/Shanghai
        //8 TimeZone2 UTC+8
        ipInfo.setCityId(cityId);
        //10 PhonePrefix 80
        ipInfo.setCountryId(countryId);
        // 如果是中国ip 计算省份ID
        if ("CN".equalsIgnoreCase(countryId) && !StringUtils.isEmpty(cityId)) {
            ipInfo.setRegionId(String.valueOf((Integer.parseInt(cityId) / 10000) * 10000));
        }
        return ipInfo;
    }

    private void load(byte[] data) {
        dataBuffer = ByteBuffer.wrap(data);
        dataBuffer.position(0);
        offset = dataBuffer.getInt(); // indexLength
        byte[] indexBytes = new byte[offset];
        dataBuffer.get(indexBytes, 0, offset - 4);
        indexBuffer = ByteBuffer.wrap(indexBytes);
        indexBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                index[i * 256 + j] = indexBuffer.getInt();
            }
        }
        indexBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((a & 255) << 24 | (b & 255) << 16 | (c & 255) << 8 | d & 255);
    }

    private static int str2Ip(String ip) {
        String[] ss = ip.split("\\.");
        int a = Integer.parseInt(ss[0]);
        int b = Integer.parseInt(ss[1]);
        int c = Integer.parseInt(ss[2]);
        int d = Integer.parseInt(ss[3]);
        return a << 24 | b << 16 | c << 8 | d;
    }

    private static long ip2long(String ip) {
        return int2long(str2Ip(ip));
    }

    private static long int2long(int i) {
        long l = (long)i & 2147483647L;
        if(i < 0) {
            l |= 2147483648L;
        }
        return l;
    }

    private Float parseFloat(String str) {
        try {
            return Float.valueOf(str);
        } catch (Exception e) {
            return null;
        }
    }
}
