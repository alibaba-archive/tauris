package com.aliyun.tauris.plugins.output;

import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by ZhangLei on 17/5/28.
 */
@Name("ots")
public class OTSOutput extends BaseBatchOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.ots");

    @Required
    String endPoint;

    @Required
    String instanceName;


    @Required
    String accessKeyId;

    @Required
    String accessKeySecret;

    @Required
    String tableName;

    @Required
    String[] primaryKeyNames;

    @Required
    String[] primaryKeyFields;

    @Required
    String[] fields;

    int connectionTimeout = 5000;
    int socketTimeout     = 5000;

    String retry;

    private SyncClient client;

    public void init() throws TPluginInitException {
        super.init();
        if (primaryKeyNames.length != primaryKeyFields.length) {
            throw new TPluginInitException("primaryKeyNames's count must equals to primaryKeyFields's count");
        }
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        // 设置建立连接的超时时间。
        clientConfiguration.setConnectionTimeoutInMillisecond(connectionTimeout);
        // 设置socket超时时间。
        clientConfiguration.setSocketTimeoutInMillisecond(socketTimeout);
        // 设置重试策略，若不设置，采用默认的重试策略。
        RetryStrategy rs;
        if ("always".equals(retry)) {
            rs = new AlwaysRetryStrategy();
        } else {
            rs = new DefaultRetryStrategy();
        }
        clientConfiguration.setRetryStrategy(rs);
        client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName, clientConfiguration);

    }

    @Override
    protected BatchWriteTask newTask() throws Exception {
        return new OTSWriteTask();
    }

    @Override
    public void stop() {
        super.stop();
        client.shutdown();
    }

    class OTSWriteTask extends BatchWriteTask {

        BatchWriteRowRequest request;

        public OTSWriteTask() {
            this.request = new BatchWriteRowRequest();
        }

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            PrimaryKeyBuilder       pkBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
            for (int i = 0; i < primaryKeyNames.length; i++) {
                String primaryKeyName = primaryKeyNames[i];
                Object pkValue = event.get(primaryKeyFields[i]);
                if (pkValue == null) {
                    logger.error(String.format("%s is null", primaryKeyName));
                    return;
                }
                if (pkValue instanceof Long) {
                    pkBuilder.addPrimaryKeyColumn(primaryKeyName, PrimaryKeyValue.fromLong((Long) pkValue));
                } else {
                    pkBuilder.addPrimaryKeyColumn(primaryKeyName, PrimaryKeyValue.fromString(pkValue.toString()));
                }
            }
            PrimaryKey   pk     = pkBuilder.build();
            RowPutChange rowPut = new RowPutChange(tableName, pk);
            // 添加一些列
            for (String field : fields) {
                String fn, cn;
                fn = cn = field;
                if (field.contains(":")) {
                    String[] ps = field.split(":");
                    cn = ps[0];
                    fn = ps[1];
                }
                Object fv = event.get(fn);
                if (fv == null) {
                    continue;
                }
                if (fv instanceof Long) {
                    rowPut.addColumn(cn, ColumnValue.fromLong((Long) fv));
                } else if (fv instanceof Boolean) {
                    rowPut.addColumn(cn, ColumnValue.fromBoolean((Boolean) fv));
                } else if (fv instanceof Double) {
                    rowPut.addColumn(cn, ColumnValue.fromDouble((Double) fv));
                } else {
                    rowPut.addColumn(cn, ColumnValue.fromString(fv.toString()));
                }
            }
            // 添加到batch操作中
            request.addRowChange(rowPut);
        }

        @Override
        protected void execute() {
            BatchWriteRowResponse response = client.batchWriteRow(request);
            if (!response.isAllSucceed()) {
                for (BatchWriteRowResponse.RowResult rowResult : response.getFailedRows()) {
                    PrimaryKey pk = request.getRowChange(rowResult.getTableName(), rowResult.getIndex()).getPrimaryKey();
                    logger.error("put row failed, cause by " + rowResult.getError() + ", primary key: " + pk.toString());
                }
            }
        }
    }
}
