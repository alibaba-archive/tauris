package com.aliyun.tauris.plugins.filter.keymap;

import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TLogger;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("csv")
public class CSVKeyMapper extends AbstractResourceKeyMapper {

    private TLogger logger;

    char separator = ',';

    char quotechar = '\"';

    char escape = '\\';
    /**
     * key在csv列的索引, 从1开始。
     */
    @Required
    int keyIndex;

    /**
     * value在csv列的索引, 从1开始。
     */
    Integer valueIndex;

    public void init() throws TPluginInitException {
        this.logger = TLogger.getLogger(this);
        if (valueIndex != null && valueIndex < 0) {
            throw new TPluginInitException("invalid value_index");
        } else if (valueIndex != null) {
            valueIndex = valueIndex - 1;
        }
        keyIndex -= 1;
    }

    public void update(String text) {
        int              colIndex = this.keyIndex;
        CSVReaderBuilder builder  = new CSVReaderBuilder(new CharArrayReader(text.toCharArray()));
        try (CSVReader reader = builder.withCSVParser(new CSVParserBuilder().withSeparator(separator).withEscapeChar(escape).withQuoteChar(quotechar).build()).build()) {
            ;
            Map<String, Object> data = new HashMap<>();
            for (String[] row : reader.readAll()) {
                if (colIndex < row.length) {
                    String key = row[colIndex];
                    if (data.containsKey(key)) {
                        logger.WARN("duplicated key:%s", key);
                    }
                    data.put(key, row);
                }
            }
            if (data.isEmpty() && !mapping.isEmpty()) {
                logger.ERROR("the new resource is empty from " + resource.getURI() + "");
            } else {
                update(data);
            }
            logger.INFO("update csv keymapper from " + resource.getURI());
        } catch (IOException e) {
            throw new RuntimeException("cannot read csv file " + resource.getURI() + " failed", e);
        }
    }

    @Override
    public Object resolveValue(Object value) {
        if (valueIndex == null || value == null) {
            return value;
        }
        String[] cols = (String[]) value;
        if (cols.length <= valueIndex) {
            return null;
        }
        return cols[valueIndex];
    }
}
