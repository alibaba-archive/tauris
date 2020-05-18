package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TResource;
import org.junit.Test;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class TBIPLocatorTest {

    @Test
    public void test() throws Exception {
        TbIPLocator locator = new TbIPLocator();
        locator.dataFile = TResource.valueof("/Users/zhanglei/Work/Projects/ware/tauris4/tauris-plugins-filter/iploc/tmp/tbip/ipdata_geo_isp_code.txt.utf8");
//        locator.translate = true;
        locator.prepare();
        System.out.println(locator.locate("180.97.33.107"));
    }
}
