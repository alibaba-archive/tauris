package com.aliyun.tauris.plugins.filter.iploc;

import com.aliyun.tauris.TResource;
import org.junit.Test;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class DNAIPLocatorTest {


    @Test
    public void test() throws Exception {
        DnaIPLocator locator = new DnaIPLocator();
        locator.dataFile = TResource.valueof("/Users/zhanglei/Work/Projects/ware/tauris4/tauris-plugins-filter/iploc/tmp/dna/geocache.bin");
//        locator.translate = true;
        locator.prepare();

        System.out.println(locator.locate("115.239.211.112"));
    }
}
