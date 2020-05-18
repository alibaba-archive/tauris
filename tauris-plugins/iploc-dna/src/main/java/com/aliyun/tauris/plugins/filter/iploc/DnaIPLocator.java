package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TResource;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.plugins.filter.iploc.dna.LineReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 集团安全IP库
 * <pre>
 *     http://gitlab.alibaba-inc.com/yihong.lb/attribution/wikis/home
 * </pre>
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("dna")
public class DnaIPLocator extends BaseIpLocator {

    private static Logger logger = LoggerFactory.getLogger(DnaIPLocator.class);

    private static final String SEP = "\u0001";

    @Required
    TResource dataFile;

    private Locator locator;

    @Override
    public void prepare() throws TPluginInitException {
        super.prepare();
        try {
            locator = new Locator();
            locator.load(dataFile.fetch());

            this.dataFile.watch((d) -> {
                Locator newLocator = new Locator();
                newLocator.load(d);
                _lock.lock();
                this.locator = newLocator;
                _lock.unlock();
            });

        } catch (Exception e) {
            throw new TPluginInitException("geo data init failed", e);
        }
    }

    @Override
    IPInfo _locate(String ip) {
        return locator.search(ip);
    }

    private class Locator {

        private ReentrantLock lock = new ReentrantLock();

        private String[] countryArr;
        private String[] provArr;
        private String[] cityArr;
        private String[] ispArr;

        private int[]      indexArr;                  // ip段toIdx列表
        private ByteBuffer attrIdxBuffer;             // 归属信息索引
        private ByteBuffer attrBuffer;                // 归属信息段

        /**
         * 根据ip查找归属地
         */
        public IPInfo search(String ip) {
            int pos = search(IPUtils.toLong(ip));
            if (pos == -1) {
                return null;
            }

            byte[] arr    = new byte[]{attrIdxBuffer.get(pos), attrIdxBuffer.get(pos + 1), attrIdxBuffer.get(pos + 2)};
            Long   offset = IPUtils.toLong(arr);
            arr = new byte[]{attrIdxBuffer.get(pos + 3), attrIdxBuffer.get(pos + 4)};
            Long len = IPUtils.toLong(arr);

            if (offset == null || len == null) {
                return null;
            }

            byte[] byteArr;

            lock.lock();
            try {
                attrBuffer.position(offset.intValue());
                byteArr = new byte[len.intValue()];
                attrBuffer.get(byteArr, 0, len.intValue());
            } finally {
                lock.unlock();
            }
            String data = new String(byteArr, charset);
            IPInfo ipEntity = new IPInfo();
            try {
                parseAttr(ipEntity, data);
            } catch (Exception e) {
                return ipEntity;
            }
            return ipEntity;
        }

        private void load(byte[] dataArr) {
            ByteBuffer buffer = ByteBuffer.wrap(dataArr);

            byte arrLen = buffer.get();
            // data
            countryArr = readAttr(buffer);
            provArr = readAttr(buffer);
            cityArr = readAttr(buffer);
            ispArr = readAttr(buffer);

            // 新版数据如果添加了归属地字段, 则可以新老数据版本兼容
            for (int i = 4; i < arrLen; i++) {
                readAttr(buffer);
            }

            // 归属地信息段
            int attrLen = buffer.getInt();
            byte[] attrArr = new byte[attrLen];
            buffer.get(attrArr);
            attrBuffer = ByteBuffer.wrap(attrArr);

            int pos = buffer.position();
            int limit = buffer.capacity();
            int size = (limit - pos) / 9;
            indexArr = new int[size];

            attrIdxBuffer = ByteBuffer.allocate(size * 5);
            byte[] byteArr;
            for (int i = 0; i < size; i++) {
                indexArr[i] = buffer.getInt();
                byteArr = new byte[5];
                buffer.get(byteArr);
                attrIdxBuffer.put(byteArr);
            }
        }

        /**
         * 二分查找 获取详细信息的具体位置
         *
         * @param value ip对应的值
         * @return ip对应归属地位置
         */
        private int search(long value) {
            int toIdx = indexArr.length - 1;
            int fromIdx = 0;
            int pos = -1;
            while (fromIdx <= toIdx) {
                int idx = (fromIdx + toIdx) >> 1;
                long toV = IPUtils.toLong(indexArr[idx]);
                if (toV < value) {
                    fromIdx = idx + 1;
                    continue;
                }
                long fromV = 0;
                if (idx > 0) {
                    fromV = IPUtils.toLong(indexArr[idx - 1]) + 1;
                }
                if (fromV > value) {
                    toIdx = idx - 1;
                    continue;
                }
                pos = idx * 5;
                break;
            }
            return pos;
        }

        private void parseAttr(IPInfo ipEntity, String data) {
            // 解析归属地信息
            LineReader lr = new LineReader(data, SEP);

            String country = "";
            String v = lr.nextValue();
            if (v.length() > 0) {
                country = countryArr[Integer.parseInt(v)];
            }

            v = lr.nextValue();
            String prov = "";
            if (v.length() > 0) {
                prov = provArr[Integer.parseInt(v)];
            }

            v = lr.nextValue();
            String city = "";
            if (v.length() > 0) {
                city = cityArr[Integer.parseInt(v)];
            }

            v = lr.nextValue();
            String isp = "";
            if (v.length() > 0) {
                isp = ispArr[Integer.parseInt(v)];
            }

            lr = new LineReader(country, SEP);
            String countryId = lr.nextValue();
            ipEntity.setCountryId(countryId);
            ipEntity.setCountry(lr.nextValue());

            lr = new LineReader(city, SEP);
            String cityId = lr.nextValue();
            ipEntity.setCityId(cityId);
            ipEntity.setCity(lr.nextValue());

            ipEntity.setRegion(prov);
            ipEntity.setIsp(isp);

            // 如果是中国ip 计算省份ID
            if ("CN".equalsIgnoreCase(countryId) && !StringUtils.isEmpty(cityId)) {
                ipEntity.setRegionId(String.valueOf((Integer.parseInt(cityId) / 10000) * 10000));
            }
        }

        /**
         * 读取对应国家、省份 城市等数组
         *
         * @param buffer 读取buffer
         * @return 内容数组 下标即编号
         */
        private String[] readAttr(ByteBuffer buffer) {
            int size = buffer.getInt();
            String[] arr = new String[size];
            for (int i = 0; i < size; i++) {
                byte len = buffer.get();
                byte[] byteArr = new byte[len];
                buffer.get(byteArr);
                arr[i] = new String(byteArr, charset);
            }
            return arr;
        }
    }


}
