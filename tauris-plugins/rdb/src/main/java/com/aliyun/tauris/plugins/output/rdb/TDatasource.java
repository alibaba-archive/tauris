package com.aliyun.tauris.plugins.output.rdb;

import com.aliyun.tauris.TPlugin;
import com.aliyun.tauris.annotations.Type;

import javax.sql.DataSource;

/**
 * Created by ZhangLei on 17/5/28.
 */
@Type
public interface TDatasource extends TPlugin, DataSource {


}
