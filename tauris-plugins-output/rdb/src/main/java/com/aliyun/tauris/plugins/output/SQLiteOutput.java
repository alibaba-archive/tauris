package com.aliyun.tauris.plugins.output;

import com.aliyun.tauris.EncodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.metric.Counter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangLei on 17/5/28.
 */
@Name("sqlite")
public class SQLiteOutput extends RdbOutput {

    private static Logger logger = LoggerFactory.getLogger("tauris.output.sqlite");

    private static Counter OUTPUT_COUNTER = Counter.build().name("output_sqlite_total").labelNames("id").help("sqlite put count").create().register();
    private static Counter ERROR_COUNTER  = Counter.build().name("output_sqlite_error_total").labelNames("id").help("sqlite put error count").create().register();

    final static String DRIVER_CLASS = "org.sqlite.JDBC";

    String schema;

    File schemaFile;

    /**
     * 单线程写文件
     */
    boolean singleThread;

    private final Object lock = new Object();

    public void init() throws TPluginInitException {
        super.driverClass = DRIVER_CLASS;
        super.init();
        if (schema == null && schemaFile == null) {
            throw new TPluginInitException("schema or schema file is required");
        }
        if (schema == null) {
            try {
                schema = FileUtils.readFileToString(schemaFile);
            } catch (IOException e) {
                throw new TPluginInitException("cannot read schema file");
            }
        }
        try {
            tryCreateTable();
        } catch (SQLException e) {
            throw new TPluginInitException("schema is invalid");
        }
        if (!url.getExpression().startsWith("jdbc:sqlite:")) {
            throw new TPluginInitException("invalid url format");
        }
    }

    protected Connection makeConnection(String url) throws SQLException {
        File dbFile = new File(url.replace("jdbc:sqlite:", ""));
        File dir    = dbFile.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("cannot create database directory");
        }
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(false);
        if (!dbFile.exists() || dbFile.length() == 0) {
            createDBFile(conn);
        }
        return conn;
    }

    protected void writeEventsToDatabase(Map<String, List<Object[]>> data) {
        if (data.isEmpty()) return;
        for (Map.Entry<String, List<Object[]>> entry : data.entrySet()) {
            List<Object[]> rows = entry.getValue();
            if (singleThread) {
                writeEventsToDatabaseSafety(entry.getKey(), entry.getValue(), makeInsertSQL(entry.getValue().size()));
            } else {
                writeEventsToDatabaseNotSafety(entry.getKey(), entry.getValue(), makeInsertSQL(entry.getValue().size()));
            }
            OUTPUT_COUNTER.labels(id()).inc(rows.size());
        }
    }

    private void writeEventsToDatabaseSafety(String url, List<Object[]> rows, String insertSql) {
        synchronized (lock) {
            writeEventsToDatabaseNotSafety(url, rows, insertSql);
        }
    }

    private void writeEventsToDatabaseNotSafety(String url, List<Object[]> rows, String insertSql) {
        try (Connection conn = makeConnection(url); PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            writeEventToDatabase(rows, conn, stmt);
        } catch (SQLException e) {
            ERROR_COUNTER.labels(id()).inc(rows.size());
            logger.error("write to database failed", e);
        }
    }

    private void createDBFile(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(schema);
        conn.commit();
        stmt.close();
    }

    /**
     * 在内存中创建数据库和表，验证schema是否有效
     *
     * @throws SQLException
     */
    private void tryCreateTable() throws SQLException {
        String url = "jdbc:sqlite::memory:";
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);
            createDBFile(conn);
        }
    }

    @Override
    protected BatchWriteTask newTask() throws Exception {
        return new SQLiteWriteTask();
    }

    class SQLiteWriteTask extends BatchWriteTask {

        Map<String, List<Object[]>> data;

        public SQLiteWriteTask() {
            data = new HashMap<>();
        }

        @Override
        protected void accept(TEvent event) throws EncodeException, IOException {
            String         url  = SQLiteOutput.this.url.format(event);
            List<Object[]> rows = data.get(url);
            if (rows == null) {
                rows = new ArrayList<>();
                data.put(url, rows);
            }
            Object[] row   = new Object[_fields.length];
            int      index = 0;
            for (String field : _fields) {
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
            writeEventsToDatabase(data);
        }
    }
}
