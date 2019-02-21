package com.aliyun.tauris.plugins.output.file;

import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class TimeRollingFileManager
 *
 * @author yundun-waf-dev
 * @date 2018-10-25
 */
@Name("time_rolling")
public class TimeRollingFileManager extends AbstractFileManager {

    @Required
    File directory;

    @Required
    String filename;

    String datePattern;

    @Required
    String interval;

    private DateTimeFormatter dateFormatter;

    private long nextRollingTime;

    private long intervalMillis;

    private File currentFile;

    public void init() throws TPluginInitException {
        Pattern p = Pattern.compile("^(\\d+)(d|h|m|s)$");
        Matcher m = p.matcher(interval);
        if (m.matches()) {
            long t = Long.parseLong(m.group(1));
            String u = m.group(2);
            switch (u) {
                case "d":
                    intervalMillis = t * 24 * 3600 * 1000;
                    break;
                case "h":
                    intervalMillis = t * 3600 * 1000;
                    break;
                case "m":
                    intervalMillis = t * 60 * 1000;
                    break;
                case "s":
                    intervalMillis = t * 1000;
            }
        } else {
            throw new TPluginInitException("invalid interval '" + interval + "'");
        }
        if (datePattern != null) {
            dateFormatter = DateTimeFormat.forPattern(datePattern);
        }
    }

    @Override
    public File resolve(TEvent event) throws IOException {
        if (needRolling()) {
            long now = System.currentTimeMillis();
            long current = now - now % intervalMillis;
            currentFile = newFile(current);
            nextRollingTime = current + intervalMillis;
        }
        return currentFile;
    }

    @Override
    public boolean canClose(File file) {
        return needRolling() || !file.getAbsolutePath().equals(currentFile.getAbsolutePath());
    }

    private boolean needRolling() {
        return System.currentTimeMillis() > nextRollingTime;
    }

    private File newFile(long time) throws IOException {
        String filename = this.filename;
        if (dateFormatter != null) {
            String date = new DateTime(time).toString(dateFormatter);
            filename = filename.replaceAll("\\$\\{date\\}", date);
        }
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("cannot make directory:" + directory);
            }
        }
        return new File(directory, filename);
    }
}
