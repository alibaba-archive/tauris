package com.aliyun.tauris.plugins.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tauris.*;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * Class Test
 *
 * @author yundun-waf-dev
 * @date 2020-04-19
 */
public class Test {

    public static void main(String[] argv) throws Exception {
        KVFlowPrinterBuilder builder = new KVFlowPrinterBuilder();
        File f = new File("/tmp/500.log");
        Scanner scanner = new Scanner(f);
        scanner.useDelimiter("\n");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TPrinter p = builder.create(bos);
        int limit = 500;
        int i = 0;
        while (scanner.hasNext()) {
            String ln = scanner.nextLine();
            JSONObject o = (JSONObject)JSON.parse(ln);
            TEvent event = new DefaultEvent();
            event.setFields(o);
            p.write(event);
            if (i > limit) {
                break;
            }
            i++;
        }
        p.flush();
        File o = new File(String.format("%d.kvflow", limit));
        FileOutputStream fos = new FileOutputStream(o);
        IOUtils.write(bos.toByteArray(), fos);
        fos.close();

//        ByteBuffer b = ByteBuffer.allocate(1024);
//        b.put("hello".getBytes());
//        System.out.println(new String(b.array(), 0, b.position()));
//
//        b.rewind();
//        System.out.println(b.position());
//        b.flip();
//        System.out.println(new String(b.array(), 0, b.position()));

    }
}
