package com.aliyun.tauris.plugins.output.rdb;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;

import javax.sql.DataSource;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Type
public interface TDatasource extends TPlugin, DataSource {


}
