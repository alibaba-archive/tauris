package com.aliyun.tauris.utils;

import com.aliyun.tauris.TPlugin;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZhangLei on 2018/6/7.
 */
public interface BlockTextReader extends Closeable, TPlugin  {

    BlockTextReader wrap(InputStream in);

    String read() throws IOException ;

}
