package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.plugins.output.rdb.TDatasource;
import com.aliyun.tauris.utils.EventFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("rdb")
public class RdbOutput extends BaseBatchOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.rdb");

    public enum Mode {
        insert, replace;
    }

    /**
     * 如果datasource为空, driverClass和url需要赋值
     */
    TDatasource datasource;

    protected String driverClass;

    protected EventFormatter url;

    @Required
    String table;

    String[] columns;

    @Required
    String[] fields;

    Mode mode = Mode.insert;

    String valuesSql;

    String[] _columns;

    String[] _fields;

    Map<String, String> defaultValues = new ConcurrentHashMap<>();

    public void init() throws TPluginInitException {
        super.init();
        if (datasource == null && driverClass == null) {
            throw new TPluginInitException("driver class is required");
        }
        if (driverClass != null) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                throw new TPluginInitException("jdbc driver class " + driverClass + " not found");
            }
            if (url == null) {
                throw new TPluginInitException("jdbc url is required");
            }
        }
        if (columns == null) {
            _columns = new String[fields.length];
        } else {
            _columns = columns;
        }
        _fields = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            String[] parts = StringUtils.splitPreserveAllTokens(field, ':'); // field:column:defaultvalue
            String fieldName = parts[0];
            _fields[i] = fieldName;
            if (parts.length > 1) {
                String columnName = parts[1];
                if (StringUtils.isEmpty(columnName)) {
                    columnName = fieldName;
                }
                if (_columns[i] == null) {
                    _columns[i] = columnName;
                }
            } else if (_columns[i] == null){
                _columns[i] = fieldName;
            }
            if (parts.length == 3) {
                defaultValues.put(fieldName, parts[2]);
            }
        }

        if (valuesSql == null) {
            List<String> vs = new ArrayList<>();
            for (String c : _columns) {
                vs.add("?");
            }
            valuesSql = "(" + String.join(",", vs) + ")";
        }
    }

    protected Connection getConnection() throws SQLException {
        Connection connection;
        if (datasource != null) {
            connection = datasource.getConnection();
        } else {
            connection = DriverManager.getConnection(url.format());
        }
        connection.setAutoCommit(false);
        return connection;
    }

    protected String makeInsertSQL(int eventCount) {
        StringBuilder insertSql = new StringBuilder(String.format("%s INTO %s (%s) VALUES ", mode.name(), table, String.join(",", _columns)));
        for (int i = 0; i < eventCount; i++) {
            insertSql.append(valuesSql);
            if (i < eventCount - 1) {
                insertSql.append(",\n");
            }
        }
        return insertSql.toString();
    }

    protected void writeEventsToDatabase(List<Object[]> rows) {
        String insertSql = makeInsertSQL(rows.size());
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            writeEventToDatabase(rows, conn, stmt);
        } catch (SQLException e) {
            logger.error("write to database failed", e);
        }
    }

    protected void writeEventToDatabase(List<Object[]> rows, Connection conn, PreparedStatement stmt) throws SQLException {
        int idx = 1;
        for (Object[] row : rows) {
            for (Object col: row) {
                stmt.setObject(idx++, col);
            }
        }
        stmt.execute();
        conn.commit();
    }

    @Override
    protected BatchWriteTask newTask() throws Exception {
        return new RdbWriteTask();
    }

    @Override
    public void stop() {
        super.stop();
    }

    class RdbWriteTask extends BatchWriteTask {

        List<Object[]> rows;

        public RdbWriteTask() {
            rows = new ArrayList<>();
        }

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            Object[] row = new Object[_fields.length];
            int index = 0;
            for (String field: _fields) {
                Object v = event.get(field);
                if (v == null && defaultValues.containsKey(field)) {
                    v = defaultValues.get(field);
                }
                row[index] = v;
                index++;
            }
            rows.add(row);
        }

        @Override
        protected void execute() {
            writeEventsToDatabase(rows);
        }
    }
}
