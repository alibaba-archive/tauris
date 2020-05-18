package com.aliyun.tauris.plugins.filter.iploc;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class IPInfo {

    private static final long serialVersionUID = 5741890095856633725L;

    /**
     * ip address (long)
     */
    private long              lip              = 0;

    /**
     * ip address (String)
     */
    private String            ip               = "";

    /**
     * ip segment start
     */
    private long              lipstart         = 0;

    /**
     * ip segment end
     */
    private long              lipend           = 0;

    /**
     * country name
     */
    private String            country          = "";

    /**
     * country id
     */
    private String            countryId        = "";

    /**
     * area name
     */
    private String            area             = "";
    /**
     * area id
     */
    private String            areaId           = "";

    /**
     * province/municipality name
     */
    private String            region           = "";

    /**
     * province/municipality id
     */
    private String            regionId         = "";

    /**
     * city name
     */
    private String            city             = "";

    /**
     * city id
     */
    private String            cityId           = "";

    /**
     * county name
     */
    private String            county           = "";

    /**
     * county id
     */
    private String            countyId         = "";

    /**
     * isp name
     */
    private String            isp              = "";

    /**
     * isp id
     */
    private String            ispId            = "";

    private String            address          = "";

    private String            extra1           = "";

    private String            extra2           = "";

    private Float             latitude;
    private Float             longitude;

    private boolean isMobileIp=false;

    private String cityCode;

    public IPInfo(){
    }

    /**
     * 获取直辖市映射行政编号
     *
     * @return
     */
    public String getCityCode() {
        String regionId = getRegionId();
        if (regionId != null
                && (regionId.equals("110000") || regionId.equals("310000") || regionId.equals("120000") || regionId.equals("500000"))) {
            this.cityCode = regionId;
        } else {
            this.cityCode = getCityId();
        }
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public long getLip() {
        return lip;
    }

    public void setLip(long lip) {
        this.lip = lip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLipstart() {
        return lipstart;
    }

    public void setLipstart(long lipstart) {
        this.lipstart = lipstart;
    }

    public long getLipend() {
        return lipend;
    }

    public void setLipend(long lipend) {
        this.lipend = lipend;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getIspId() {
        return ispId;
    }

    public void setIspId(String ispId) {
        this.ispId = ispId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public boolean isMobileIp() {
        return isMobileIp;
    }

    public void setIsMobileIp(boolean isMobileIp) {
        this.isMobileIp = isMobileIp;
    }

    public String toString() {
        ToStringBuilder tb = new ToStringBuilder(this);
        tb.append(String.format("lip=%s", lip));
        tb.append(String.format("ip=%s", ip));
        tb.append(String.format("country=%s", country));
        tb.append(String.format("countryId=%s", countryId));
        tb.append(String.format("area=%s", area));
        tb.append(String.format("areaId=%s", areaId));
        tb.append(String.format("region=%s", region));
        tb.append(String.format("regionId=%s", regionId));
        tb.append(String.format("city=%s", city));
        tb.append(String.format("cityId=%s", cityId));
        tb.append(String.format("county=%s", county));
        tb.append(String.format("countyId=%s", countyId));
        tb.append(String.format("isp=%s", isp));
        tb.append(String.format("ispId=%s", ispId));
        tb.append(String.format("address=%s", address));
        tb.append(String.format("extra1=%s", extra1));
        tb.append(String.format("extra2=%s", extra2));
        tb.append(String.format("latitude=%s", latitude));
        tb.append(String.format("longitude=%s", longitude));
        return tb.toString();
    }

    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(){{
            put("lip", lip);
            put("ip", ip);
            put("country", country);
            put("countryId", countryId);
            put("area", area);
            put("areaId", areaId);
            put("region", region);
            put("regionId", regionId);
            put("city", city);
            put("cityId", cityId);
            put("county", county);
            put("countyId", countyId);
            put("isp", isp);
            put("ispId", ispId);
            put("address", address);
            put("extra1", extra1);
            put("extra2", extra2);
            put("latitude", latitude);
            put("longitude", longitude);
        }};
    }
}