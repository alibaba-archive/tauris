package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TResource;
import org.junit.Test;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class IPIPLocatorTest {


    @Test
    public void test() throws Exception {
        IPIPLocator locator = new IPIPLocator();
        locator.dataFile = TResource.valueof("/Users/zhanglei/Work/Projects/ware/tauris4/tauris-plugins-filter/iploc/tmp/ipip/ipip.datx");
        locator.translate = true;
        locator.prepare();

        System.out.println(locator.locate("115.239.211.112"));
    }
}
