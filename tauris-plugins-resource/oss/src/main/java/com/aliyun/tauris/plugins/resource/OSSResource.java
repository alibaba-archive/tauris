package com.aliyun.tauris.plugins.resource;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.tauris.TResourceURI;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.resource.AbstractScheduleUpdateResource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by ZhangLei on 2018/4/28.
 */
@Name("oss")
public class OSSResource extends AbstractScheduleUpdateResource {

    private static Logger LOG = LoggerFactory.getLogger(OSSResource.class);

    private String md5suffix;

    private String md5sum;

    private OSSClient client;

    private String filename;

    private String bucket;

    private String endpoint;

    @Override
    public void setURI(TResourceURI uri) {
        super.setURI(uri);
        String ak = uri.getParam("ak");
        String sk = uri.getParam("sk");
        String host = uri.getHost();
        int firstDot = host.indexOf('.');
        this.bucket = host.substring(0, firstDot);
        this.endpoint = host.substring(firstDot + 1);
        this.client = new OSSClient(endpoint, ak, sk);
        this.filename = uri.getPath().substring(1);
        this.md5suffix = uri.getParam(P_MD5SUM);
    }

    @Override
    public byte[] fetch() throws Exception {
        if (md5suffix != null) {
            String md5file = filename + "." + md5suffix;
            String md5sum = new String(readFromOSS(md5file));
            if (md5sum.trim().equals(this.md5sum)) {
                return null;
            }
            this.md5sum = md5sum.trim();
        }
        return readFromOSS(filename);
    }

    private byte[] readFromOSS(String filename) throws IOException{
        OSSObject object = client.getObject(bucket, filename);
        byte[] ret = IOUtils.toByteArray(object.getObjectContent());
        object.close();
        return ret;
    }

    @Override
    public String toString() {
        return String.format("oss://%s/%s/%s", endpoint, bucket, filename);
    }
}
