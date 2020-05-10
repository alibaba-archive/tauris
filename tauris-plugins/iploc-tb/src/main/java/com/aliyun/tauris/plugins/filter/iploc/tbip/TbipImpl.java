package com.aliyun.tauris.plugins.filter.iploc.tbip;

import com.aliyun.tauris.plugins.filter.iploc.IPInfo;
import com.aliyun.tauris.plugins.filter.iploc.IPUtils;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rangeng.llb
 * @author wenshao<shaojin.wensj@alibaba-inc.com>
 */
public class TbipImpl {

    private TbipFileType fileType = TbipFileType.T_3_ipdata_geo_isp_code;

    private int[]  index;
    private byte[] info;

    private String[] countryArray;
    private String[] areaArray;
    private String[] regionArray;
    private String[] cityArray;
    private String[] countyArray;
    private String[] ispArray;
    private String[] addressArray;
    private String[] extra1Array;
    private String[] extra2Array;

    public TbipImpl() {

    }

    public void init(byte[] ipData) throws IOException {
        buf = new ByteArrayOutputStream();
        countryMap = new HashMap<String, Object[]>(512);
        areaMap = new HashMap<String, Object[]>(128);
        regionMap = new HashMap<String, Object[]>(128);
        cityMap = new HashMap<String, Object[]>(1024);
        countyMap = new HashMap<String, Object[]>(4096);

        ispMap = new HashMap<String, Object[]>(256);
        addressMap = new HashMap<String, Object[]>(1024);
        extra1Map = new HashMap<String, Object[]>(256);
        extra2Map = new HashMap<String, Object[]>(256);

        ByteArrayInputStream in    = new ByteArrayInputStream(ipData);
        int                  lines = countLine(in);

        index = new int[lines * 3];
        lineNum = 0;

        BufferedLineReader reader = null;

        try {
            reader = new BufferedLineReader(in);

            while (reader.nextLine()) {
                line = reader.getLine();
                readLine();
                lineNum++;
            }

            if (areaMap.size() > 0xFE) {
                throw new IllegalStateException("not support area > 254 : " + areaMap.size());
            }
            this.info = buf.toByteArray();

            final int blockSize;
            switch (fileType) {
                case T_1_ip_data:
                case T_2_ip_data_cn:
                case T_6_cernet:
                case T_7_school:
                    blockSize = 1;
                    break;
                case T_3_ipdata_geo_isp_code:
                case T_4_ipdata_geo_isp_code_cn:
                case T_5_ipdata_geo_isp_code_full:
                    blockSize = 2;
                    break;
                case T_8_ipdata_code_with_maxmind:
                    blockSize = 3;
                    break;
                default:
                    throw new IllegalStateException("not support fileType : " + fileType);
            }

            countryArray = buildArray(countryMap, blockSize);
            areaArray = buildArray(areaMap, blockSize);
            regionArray = buildArray(regionMap, blockSize);
            cityArray = buildArray(cityMap, blockSize);
            countyArray = buildArray(countyMap, blockSize);
            ispArray = buildArray(ispMap, blockSize);

            if (fileType == TbipFileType.T_5_ipdata_geo_isp_code_full) {
                extra1Array = buildArray(extra1Map, 1);
                extra2Array = buildArray(extra2Map, 1);
            } else if (fileType == TbipFileType.T_6_cernet || fileType == TbipFileType.T_7_school) {
                addressArray = buildArray(addressMap, blockSize);
            }
        } finally {
            line = null;
            buf = null;

            countryMap = null;
            areaMap = null;
            regionMap = null;
            cityMap = null;
            countyMap = null;

            ispMap = null;
            addressMap = null;
            extra1Map = null;
            extra2Map = null;
        }
    }

    private void readLine() {
        switch (this.fileType) {
            case T_1_ip_data:
            case T_2_ip_data_cn:
                readLine_1_2();
                break;
            case T_6_cernet:
            case T_7_school:
                readLine_6_7();
                break;
            case T_3_ipdata_geo_isp_code:
            case T_4_ipdata_geo_isp_code_cn:
                readLine_3_4();
                break;
            case T_5_ipdata_geo_isp_code_full:
                readLine_5();
                break;
            case T_8_ipdata_code_with_maxmind:
                readLine_8();
                break;
            default:
                throw new IllegalStateException("not support fileType : " + fileType);
        }
    }

    private void readLine_8() {
        final LineReader lineReader = new LineReader(line);

        String start = lineReader.nextValue();
        String end   = lineReader.nextValue();

        String country   = lineReader.nextValue();
        String region    = lineReader.nextValue();
        String city      = lineReader.nextValue();
        String county    = lineReader.nextValue();
        String isp       = lineReader.nextValue();
        String latitude  = lineReader.nextValue();
        String longitude = lineReader.nextValue();

        int bufIndex = buf.size();

        index[lineNum * 3] = (int) Long.parseLong(start);
        index[lineNum * 3 + 1] = (int) Long.parseLong(end);
        index[lineNum * 3 + 2] = bufIndex;

        if (isEmpty(country, latitude, longitude, region, city, county, isp)) {
            writeEnd();
            return;
        }

        writeCountryWithLanguage(countryMap, country, false);

        if (isEmpty(latitude, longitude, region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeFloat(latitude);
        writeFloat(longitude);

        if (isEmpty(region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeItemWithLanguage(regionMap, region, false);

        if (isEmpty(city, county, isp)) {
            writeEnd();
            return;
        }
        writeItemWithLanguage(cityMap, city, true);

        if (isEmpty(county, isp)) {
            writeEnd();
            return;
        }
        writeItemWithLanguage(countyMap, county, true);

        if (isEmpty(isp)) {
            writeEnd();
            return;
        }
        writeItemWithLanguage(ispMap, isp, true);

        writeEnd();
    }

    private void writeEnd() {
        buf.write(END);
    }

    private void writeFloat(String text) {

        if (text == null || text.length() == 0) {
            buf.write(0);
            buf.write(0);
            buf.write(0);
        } else {
            int commaInex = text.lastIndexOf('.');
            if (commaInex == -1) {
                int intVal = Integer.parseInt(text);
                buf.write((byte) intVal);
                buf.write(0);
                buf.write(0);
            } else {
                int intVal = commaInex == 0 ? 0 : Integer.parseInt(text.substring(0, commaInex));
                int small = Integer.parseInt(text.substring(commaInex + 1));
                buf.write((byte) intVal);
                buf.write((byte) small);
                buf.write((byte) (small >>> 8));
            }
        }
    }

    private void readLine_6_7() {
        final LineReader lineReader = new LineReader(line);

        String start = lineReader.nextValue();
        String end   = lineReader.nextValue();
        lineReader.skip();
        lineReader.skip();

        String country = lineReader.nextValue();
        String region  = lineReader.nextValue();
        String city    = lineReader.nextValue();
        String county  = lineReader.nextValue();
        String address = lineReader.nextValue();
        String isp     = lineReader.nextValue();

        int bufIndex = buf.size();

        index[lineNum * 3] = (int) Long.parseLong(start);
        index[lineNum * 3 + 1] = (int) Long.parseLong(end);
        index[lineNum * 3 + 2] = bufIndex;

        writeItem(countryMap, country, false);
        writeItem(regionMap, region, false);
        writeItem(cityMap, city, true);
        writeItem(countyMap, county, true);
        writeItem(ispMap, isp, true);
        writeItem(addressMap, address, true);
        writeEnd();
    }

    private void readLine_1_2() {
        final LineReader lineReader = new LineReader(line);

        String start = lineReader.nextValue();
        String end   = lineReader.nextValue();
        lineReader.skip();
        lineReader.skip();

        String country = lineReader.nextValue();
        String region  = lineReader.nextValue();
        String city    = lineReader.nextValue();
        String county  = lineReader.nextValue();
        String isp     = lineReader.nextValue();

        int bufIndex = buf.size();

        index[lineNum * 3] = (int) Long.parseLong(start);
        index[lineNum * 3 + 1] = (int) Long.parseLong(end);
        index[lineNum * 3 + 2] = bufIndex;

        if (isEmpty(country, region, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(countryMap, country, false);

        writeItem(regionMap, region, false);
        if (isEmpty(region, county, isp)) {
            writeEnd();
            return;
        }

        writeItem(cityMap, city, true);

        if (isEmpty(county, isp)) {
            writeEnd();
            return;
        }
        writeItem(countyMap, county, true);

        if (isEmpty(isp)) {
            writeEnd();
            return;
        }
        writeItem(ispMap, isp, true);

        writeEnd();
    }

    private static boolean isEmpty(String... values) {
        for (String value : values) {
            if (value != null && value.length() != 0 && !"-1".equals(value)) {
                return false;
            }
        }

        return true;
    }

    private void readLine_3_4() {
        final LineReader lineReader = new LineReader(line);

        String start = lineReader.nextValue();
        String end   = lineReader.nextValue();

        long startValue = Long.parseLong(start);
        long endValue   = Long.parseLong(end);

        lineReader.skip();
        lineReader.skip();

        String country   = lineReader.nextValue();
        String countryId = lineReader.nextValue();
        String area      = lineReader.nextValue();
        String areaId    = lineReader.nextValue();
        String region    = lineReader.nextValue();

        String regionId = lineReader.nextValue();
        String city     = lineReader.nextValue();
        String cityId   = lineReader.nextValue();
        String county   = lineReader.nextValue();
        String countyId = lineReader.nextValue();

        String isp      = lineReader.nextValue();
        String ispId    = lineReader.nextValue();
        int    bufIndex = buf.size();

        index[lineNum * 3] = (int) startValue;
        index[lineNum * 3 + 1] = (int) endValue;
        index[lineNum * 3 + 2] = bufIndex;

        if (isEmpty(country, area, region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(countryMap, country, countryId, false);

        if (isEmpty(area, region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(areaMap, area, areaId, false);

        if (isEmpty(region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(regionMap, region, regionId, false);

        if (isEmpty(city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(cityMap, city, cityId, false);

        if (isEmpty(county, isp)) {
            writeEnd();
            return;
        }
        writeItem(countyMap, county, countyId, true);

        if (isEmpty(isp)) {
            writeEnd();
            return;
        }
        writeItem(ispMap, isp, ispId, true);

        writeEnd();
    }

    private void readLine_5() {
        final LineReader lineReader = new LineReader(line);

        String start = lineReader.nextValue();
        String end   = lineReader.nextValue();
        lineReader.skip();
        lineReader.skip();

        String country   = lineReader.nextValue();
        String countryId = lineReader.nextValue();
        String area      = lineReader.nextValue();
        String areaId    = lineReader.nextValue();
        String region    = lineReader.nextValue();

        String regionId = lineReader.nextValue();
        String city     = lineReader.nextValue();
        String cityId   = lineReader.nextValue();
        String county   = lineReader.nextValue();
        String countyId = lineReader.nextValue();

        String isp    = lineReader.nextValue();
        String ispId  = lineReader.nextValue();
        String extra1 = "";
        String extra2 = "";
        if (fileType == TbipFileType.T_5_ipdata_geo_isp_code_full) {
            extra1 = lineReader.nextValue();
            extra2 = lineReader.nextValue();
        }
        int bufIndex = buf.size();

        index[lineNum * 3] = (int) Long.parseLong(start);
        index[lineNum * 3 + 1] = (int) Long.parseLong(end);
        index[lineNum * 3 + 2] = bufIndex;

        if (isEmpty(country, area, region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(countryMap, country, countryId, false);

        if (isEmpty(area, region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(areaMap, area, areaId, false);

        if (isEmpty(region, city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(regionMap, region, regionId, false);

        if (isEmpty(city, county, isp)) {
            writeEnd();
            return;
        }
        writeItem(cityMap, city, cityId, false);

        if (isEmpty(county, isp)) {
            writeEnd();
            return;
        }
        writeItem(countyMap, county, countyId, true);

        if (isEmpty(isp)) {
            writeEnd();
            return;
        }
        writeItem(ispMap, isp, ispId, true);

        if (fileType == TbipFileType.T_5_ipdata_geo_isp_code_full) {
            writeItem(extra1Map, extra1, true);
            writeItem(extra2Map, extra2, true);
        }

        writeEnd();
    }

    private int countLine(ByteArrayInputStream data) throws IOException {
        int count = new BufferedLineReader(data).countLine();
        data.reset();
        return count;
    }

    private String[] buildArray(Map<String, Object[]> valueMap, int blockSize) {
        String[] array = new String[valueMap.size() * blockSize];
        for (Object[] info : valueMap.values()) {
            if (blockSize == 3) {
                int i = (Integer) info[3];
                array[i * 3] = (String) info[0];
                array[i * 3 + 1] = (String) info[1];
                array[i * 3 + 2] = (String) info[2];
            } else if (blockSize == 2) {
                int i = (Integer) info[2];
                array[i * 2] = (String) info[0];
                array[i * 2 + 1] = (String) info[1];
            } else {
                int i = (Integer) info[1];
                array[i] = (String) info[0];
            }
        }
        return array;
    }

    private int writeCountryWithLanguage(Map<String, Object[]> map, String value, boolean seperator) {
        Object[] info = map.get(value);
        if (info == null && (value.length() > 0 && !"-1".equals(value))) {
            info = new Object[4];
            info[0] = value;
            info[3] = map.size();
            map.put(value, info);
        }

        if (seperator) {
            buf.write(SEPERATOR);
        }

        int id = -1;
        if (info != null) {
            id = (Integer) info[3];
        }

        writeCountry2(seperator, id);
        return id;
    }

    /*
     * only for maxmind
     */
    private int writeItemWithLanguage(Map<String, Object[]> map, String value, boolean seperator) {
        Object[] info = map.get(value);
        if (info == null && (value.length() > 0 && !"-1".equals(value))) {
            info = new Object[4];
            info[0] = value;
            info[3] = map.size();
            map.put(value, info);
        }

        if (seperator) {
            buf.write(SEPERATOR);
        }

        int id = -1;
        if (info != null) {
            id = (Integer) info[3];
        }

        writeItemValue(seperator, id);

        return id;
    }

    private void writeItem(Map<String, Object[]> countryMap, String value, boolean seperator) {
        Object[] info = countryMap.get(value);
        if (info == null && (value.length() > 0 && !"-1".equals(value))) {
            info = new Object[2];
            info[0] = value;
            info[1] = countryMap.size();
            countryMap.put(value, info);
        }

        if (seperator) {
            buf.write(SEPERATOR);
        }

        int id = -1;
        if (info != null) {
            id = (Integer) info[1];
        }

        writeItemValue(seperator, id);
    }

    private void writeItem(Map<String, Object[]> countryMap, String value, String valueId, boolean seperator) {
        Object[] info = countryMap.get(valueId);
        if (info == null && (valueId.length() > 0 && !"-1".equals(valueId))) {
            info = new Object[3];
            info[0] = value;
            info[1] = valueId;
            info[2] = countryMap.size();
            countryMap.put(valueId, info);
        }

        if (seperator) {
            buf.write(SEPERATOR);
        }

        int id = -1;
        if (info != null) {
            id = (Integer) info[2];
        }
        writeItemValue(seperator, id);
    }

    private void writeCountry2(boolean seperator, int id) {
        if (id != -1) {
            byte[] bytes = new byte[2];
            bytes[0] = (byte) (id / RADIX);
            bytes[1] = (byte) (id % RADIX);

            try {
                buf.write(bytes);
            } catch (IOException e) {
                throw new IllegalStateException();
            }
        } else {
            if (!seperator) {
                buf.write(-1);
            }
        }
    }

    private void writeItemValue(boolean seperator, int id) {
        if (id != -1) {
            try {
                buf.write(toBytes(id));
            } catch (IOException e) {
                throw new IllegalStateException();
            }
        } else {
            if (!seperator) {
                buf.write(-1);
            }
        }
    }

    private static void check(char ch, char expect) {
        if (ch != expect) {
            throw new IllegalArgumentException();
        }
    }

    private void parseBlock(IPInfo ipInfoEntity, int start) {
        switch (this.fileType) {
            case T_1_ip_data:
            case T_2_ip_data_cn:
            case T_6_cernet:
            case T_7_school:
                parseBlock_1_2_6_7(ipInfoEntity, start);
                break;
            case T_3_ipdata_geo_isp_code:
            case T_4_ipdata_geo_isp_code_cn:
            case T_5_ipdata_geo_isp_code_full:
                parseBlock_3_4_5(ipInfoEntity, start);
                break;
            case T_8_ipdata_code_with_maxmind:
                parseBlock_8(ipInfoEntity, start);
                break;
            default:
                throw new IllegalStateException("not support fileType : " + fileType);
        }
    }

    private void parseBlock_8(IPInfo ipInfoEntity, int start) {
        BlockParser reader = new BlockParser(info, start);
        int         countryIndex;
        {
            int b0 = reader.nextByte();
            int b1 = reader.nextByte();
            countryIndex = b0 * RADIX + b1;
        }
        float latitude  = reader.nextFloat();
        float longitude = reader.nextFloat();

        ipInfoEntity.setLatitude(latitude);
        ipInfoEntity.setLongitude(longitude);

        int regionIndex = reader.nextInt();
        int cityIndex   = reader.nextInt();
        int countyIndex = reader.nextInt();
        int ispIndex    = reader.nextInt();

        if (countryIndex >= 0) {
            ipInfoEntity.setCountryId(countryArray[countryIndex * 3]);
            ipInfoEntity.setCountry(empty(countryArray[countryIndex * 3 + 2], countryArray[countryIndex * 3]));
        }
        if (regionIndex >= 0) {
            ipInfoEntity.setRegionId(regionArray[regionIndex * 3]);
            ipInfoEntity.setRegion(empty(regionArray[regionIndex * 3 + 2], regionArray[regionIndex * 3]));
        }
        if (cityIndex >= 0) {
            ipInfoEntity.setCityId(cityArray[cityIndex * 3]);
            ipInfoEntity.setCity(empty(cityArray[cityIndex * 3 + 2], cityArray[cityIndex * 3]));
        }
        if (countyIndex >= 0) {
            ipInfoEntity.setCountyId(countyArray[countyIndex * 3]);
            ipInfoEntity.setCounty(empty(countyArray[countyIndex * 3 + 2], countyArray[countyIndex * 3]));
        }
        if (ispIndex >= 0) {
            ipInfoEntity.setIspId(ispArray[ispIndex * 3]);
            ipInfoEntity.setIsp(empty(ispArray[ispIndex * 3 + 2], ispArray[ispIndex * 3]));
        }
    }

    static String empty(String value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private void parseBlock_1_2_6_7(IPInfo ipInfoEntity, int start) {
        BlockParser reader       = new BlockParser(info, start);
        int         countryIndex = reader.nextByte();
        int         regionIndex  = reader.nextInt();
        int         cityIndex    = reader.nextInt();
        int         countyIndex  = reader.nextInt();
        int         ispIndex     = reader.nextInt();

        if (countryIndex >= 0) {
            ipInfoEntity.setCountry(countryArray[countryIndex]);
        }
        if (regionIndex >= 0) {
            ipInfoEntity.setRegion(regionArray[regionIndex]);
        }
        if (cityIndex >= 0) {
            ipInfoEntity.setCity(cityArray[cityIndex]);
        }
        if (countyIndex >= 0) {
            ipInfoEntity.setCounty(countyArray[countyIndex]);
        }
        if (ispIndex >= 0) {
            ipInfoEntity.setIsp(ispArray[ispIndex]);
        }
        if (fileType == TbipFileType.T_6_cernet || fileType == TbipFileType.T_7_school) {
            int addressIndex = reader.nextInt();
            if (addressIndex > 0) {
                ipInfoEntity.setAddress(addressArray[addressIndex]);
            }
        }
    }

    private void parseBlock_3_4_5(IPInfo ipInfoEntity, int start) {
        BlockParser reader       = new BlockParser(info, start);
        int         countryIndex = reader.nextByte();
        int         areaIndex    = reader.nextByte();
        int         regionIndex  = reader.nextByte();
        int         cityIndex    = reader.nextInt();
        int         countyIndex  = reader.nextInt();
        int         ispIndex     = reader.nextInt();

        if (countryIndex >= 0) {
            ipInfoEntity.setCountry(countryArray[countryIndex * 2]);
            ipInfoEntity.setCountryId(countryArray[countryIndex * 2 + 1]);
        }
        if (areaIndex >= 0) {
            ipInfoEntity.setArea(areaArray[areaIndex * 2]);
            ipInfoEntity.setAreaId(areaArray[areaIndex * 2 + 1]);
        }
        if (regionIndex >= 0) {
            ipInfoEntity.setRegion(regionArray[regionIndex * 2]);
            ipInfoEntity.setRegionId(regionArray[regionIndex * 2 + 1]);
        }
        if (cityIndex >= 0) {
            ipInfoEntity.setCity(cityArray[cityIndex * 2]);
            ipInfoEntity.setCityId(cityArray[cityIndex * 2 + 1]);
        }
        if (countyIndex >= 0) {
            ipInfoEntity.setCounty(countyArray[countyIndex * 2]);
            ipInfoEntity.setCountyId(countyArray[countyIndex * 2 + 1]);
        }
        if (ispIndex >= 0) {
            ipInfoEntity.setIsp(ispArray[ispIndex * 2]);
            ipInfoEntity.setIspId(ispArray[ispIndex * 2 + 1]);
        }

        if (fileType == TbipFileType.T_5_ipdata_geo_isp_code_full) {
            int extra1Index = reader.nextInt();
            int extra2Index = reader.nextInt();

            if (extra1Index >= 0) {
                ipInfoEntity.setExtra1(extra1Array[extra1Index]);
            }

            if (extra2Index >= 0) {
                ipInfoEntity.setExtra2(extra2Array[extra2Index]);
            }
        }
    }

    public IPInfo getIpInfo(String ip) throws IllegalArgumentException {
        long   ipValue      = IPUtils.toLong(ip);
        IPInfo ipInfoEntity = new IPInfo();
        ipInfoEntity.setLip(ipValue);
        ipInfoEntity.setIp(ip);

        int count = this.index.length / 3;
        int begin = 0;
        int end   = count - 1;

        while (begin <= end) {
            int middle = (begin + end) / 2;

            long startIp = index[middle * 3] & 0xFFFFFFFFL;
            long endIp = index[middle * 3 + 1] & 0xFFFFFFFFL;

            if (startIp <= ipValue && endIp >= ipValue) {
                int bufIndex = index[middle * 3 + 2];
                parseBlock(ipInfoEntity, bufIndex);
                ipInfoEntity.setLipstart(startIp);
                ipInfoEntity.setLipend(endIp);
                return ipInfoEntity;
            } else if (startIp > ipValue) {
                end = middle - 1;
            } else if (endIp < ipValue) {
                begin = middle + 1;
            }
        }

        return ipInfoEntity;
    }

    private static class LineReader {

        private int    i;
        private char   ch;
        private int    len;
        private String line;

        LineReader(String detail) {
            this.i = 0;
            this.len = detail.length();
            this.line = detail;
            next();
        }

        protected char charAt(int i) {
            return line.charAt(i);
        }

        protected String substring(int start, int end) {
            return (String) line.subSequence(start, end);
        }

        void next() {
            ch = charAt(i++);
        }

        public void skip() {
            while (ch != ',' && i < len) {
                next();
            }

            if (i < len) {
                check(ch, ',');
                next();
            }
        }

        public String nextValue() {
            while (ch == ' ' && i < len) {
                next();
            }

            if (ch == ',') {
                if (i < len) {
                    next();
                }
                return "";
            }

            boolean quote = false;
            if (ch == '"') {
                next();
                quote = true;
            }
            int start = i - 1;

            String value;
            while (ch != ',' && ch != '\n' && i < len) {
                next();
            }

            int end = quote ? i - 2 : i - 1;
            if (i == len) {
                end = end + 1;
            }
            if (end < start) {
                throw new IllegalArgumentException();
            }
            value = substring(start, end);

            if (i < len) {
                check(ch, ',');
                next();
            }

            return value;
        }
    }

    private static class BlockParser {

        private int    i;
        private byte   ch;
        private byte[] bytes;

        BlockParser(byte[] bytes, int offset) {
            this.i = offset;
            this.bytes = bytes;
            next();
        }

        public int nextByte() {
            if (ch == END) {
                return -1;
            }
            int value = ch & 0xFF;
            next();

            if (value == 0xFF) {
                return -1;
            }
            return value;
        }

        public float nextFloat() {
            if (ch == END) {
                return 0;
            }

            byte b0 = ch;
            next();

            byte s0 = ch;
            next();

            byte s1 = ch;
            next();

            int s = (s0 & 0xFF) + ((s1 & 0xFF) << 8);

            if (s == 0) {
                return b0;
            }

            float small;
            if (s >= 1000) {
                small = ((float) s) / 10000f;
            } else if (s >= 100) {
                small = ((float) s) / 1000f;
            } else if (s >= 10) {
                small = ((float) s) / 100f;
            } else {
                small = ((float) s) / 10f;
            }

            float value = b0 + small;
            return value;
        }

        public int nextInt() {
            if (ch == END) {
                return -1;
            }

            if (ch == SEPERATOR) {
                next();
                return -1;
            }

            int value = 0;
            while (ch != SEPERATOR && ch != END) {
                int num = ch & 0xFF;
                value = value * RADIX + num;
                next();
            }

            if (ch != END) {
                next();
            }

            return value;
        }

        void next() {
            ch = bytes[i++];
        }
    }

    private static void close(InputStream x) {
        try {
            x.close();
        } catch (IOException e) {
            // skip
        }
    }

    private static void close(BufferedReader x) {
        try {
            x.close();
        } catch (IOException e) {
            // skip
        }
    }

    private static class BufferedLineReader {

        private Reader reader;

        private char[] buf = new char[8096];
        private int    len = 0;
        private int bp;

        public char ch;
        public int  lines;

        private char[] line = new char[256];
        private int lineLength;

        public BufferedLineReader(InputStream is) throws IOException {
            reader = new InputStreamReader(is, "utf8");
            next();
        }

        public String getLine() {
            return new String(line, 0, lineLength);
        }

        public boolean nextLine() throws IOException {
            Arrays.fill(line, '\0');
            this.lineLength = 0;
            for (; ; ) {
                if (len == -1) {
                    return false;
                }

                if (ch == '\n') {
                    next();
                    lines++;
                    break;
                } else {
                    line[lineLength++] = ch;
                    next();
                }
            }

            return true;
        }

        public char next() throws IOException {
            if (bp >= len) {
                len = reader.read(buf);
                bp = 0;

                if (len == -1) {
                    return '\0';
                }
            }
            ch = buf[bp++];
            return ch;
        }

        public int countLine() throws IOException {
            for (; ; ) {
                if (len == -1) {
                    break;
                }

                if (ch == '\n') {
                    next();
                    lines++;
                } else {
                    next();
                }
            }

            return lines;
        }

        public void close() throws IOException {
            this.buf = null;
            this.line = null;
            reader.close();
        }
    }

    static byte[] toBytes(int i) {
        if (i < RADIX) {
            return new byte[]{(byte) i};
        }
        if (i < RADIX_2) {
            byte[] bytes = new byte[2];
            bytes[0] = (byte) (i / RADIX);
            bytes[1] = (byte) (i % RADIX);
            return bytes;
        }
        if (i < RADIX_3) {
            byte[] bytes = new byte[3];
            bytes[0] = (byte) (i / (RADIX_2));
            bytes[1] = (byte) ((i - bytes[0] * RADIX_2) / RADIX);
            bytes[2] = (byte) (i - bytes[0] * RADIX_2 - bytes[1] * RADIX);
            return bytes;
        }

        throw new IllegalArgumentException("val : " + i);
    }

    private static final byte SEPERATOR = (byte) 0xFF;
    private static final byte END       = (byte) 0xFE;
    private static final int  RADIX     = 254;
    private static final int  RADIX_2   = RADIX * RADIX;
    private static final int  RADIX_3   = RADIX * RADIX * RADIX;

    // temp store for init
    private transient int lineNum = 0;
    private transient String                line;
    private transient ByteArrayOutputStream buf;

    private transient Map<String, Object[]> countryMap;
    private transient Map<String, Object[]> areaMap;
    private transient Map<String, Object[]> regionMap;
    private transient Map<String, Object[]> cityMap;
    private transient Map<String, Object[]> countyMap;

    private transient Map<String, Object[]> ispMap;
    private transient Map<String, Object[]> addressMap;
    private transient Map<String, Object[]> extra1Map;
    private transient Map<String, Object[]> extra2Map;
}
