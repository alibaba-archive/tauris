package com.aliyun.tauris.plugins.output.influxdb;

/**
 * A wrapper for various exceptions caused while interacting with InfluxDB.
 *
 * @author Simon Legner
 */
public class InfluxDBException extends RuntimeException {

    public static final int UNKNOW_ERROR = 1;

    private final int code;

    public InfluxDBException(final int code, final String message) {
        super(message);
        this.code = code;
    }

    public InfluxDBException(final int code, final String message, final Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public InfluxDBException(final Throwable cause) {
        super(cause);
        this.code = UNKNOW_ERROR;
    }

    public int getCode() {
        return code;
    }
}
