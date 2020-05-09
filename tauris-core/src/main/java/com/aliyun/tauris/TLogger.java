package com.aliyun.tauris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.LoggerWrapper;

/**
 * Class TLogger
 *
 * @author yundun-waf-dev
 * @date 2019-01-22
 */
public class TLogger extends LoggerWrapper {

    public static final String PROP = "tauris.logging.verbose";

    private static boolean verbose = Boolean.valueOf(System.getProperty(PROP, "false"));

    private String pluginId;

    public TLogger(Logger logger, Object object) {
        super(logger, TLogger.class.getName());
        if (object instanceof TPlugin) {
            this.pluginId = ((TPlugin) object).id();
        }
    }

    public static TLogger getLogger(Object object) {
        Logger logger = LoggerFactory.getLogger(object.getClass());
        return new TLogger(logger, object);
    }

    public void INFO(String message, Object... args) {
        if (this.pluginId != null) {
            this.info("[" + pluginId + "]" + String.format(message, args));
        } else {
            this.info(String.format(message, args));
        }
    }

    public void ERROR(String message, Object... args) {
        message = String.format(message, args);
        if (this.pluginId != null) {
            this.error("[" + pluginId + "]" + message);
        } else {
            this.error(message);
        }
    }

    public void ERROR(Throwable e) {
        String message = e.getMessage();
        if (this.pluginId != null) {
            this.error("[" + pluginId + "]" + message);
        }
        if (verbose) {
            this.error(message, e);
        } else {
            this.error(message);
        }
    }

    public void ERROR(String message, Throwable e, Object... args) {
        String msg = String.format(message, args);
        if (this.pluginId != null) {
            this.error("[" + pluginId + "]" + msg);
        }
        if (verbose) {
            this.error(msg, e);
        } else {
            this.error(msg + ",cause by " + e.getMessage());
        }
    }

    public void EXCEPTION(Throwable e) {
        String msg = e.getMessage();
        if (this.pluginId != null) {
            this.error("[" + pluginId + "]" + msg);
        }
        this.error(msg, e);
    }

    public void EXCEPTION(String msg, Throwable e) {
        if (this.pluginId != null) {
            this.error("[" + pluginId + "]" + msg);
        }
        this.error(msg, e);
    }

    public void WARN2(String message, String detail) {
        if (verbose && detail != null) {
            message = message + ", verbose: '" + detail + "'";
        }
        if (this.pluginId != null) {
            this.warn("[" + pluginId + "]" + message);
        } else {
            this.warn(message);
        }
    }

    public void WARN2(String message, Throwable e, String detail) {
        if (verbose && detail != null) {
            message = message + ", verbose: '" + detail + "'";
        }
        if (this.pluginId != null) {
            message = "[" + pluginId + "]" + message;
        }
        if (verbose) {
            this.warn(message, e);
        } else {
            this.warn(message);
        }
    }

    public void WARN(String message, Object... args) {
        if (this.pluginId != null) {
            this.warn("[" + pluginId + "]" + String.format(message, args));
        } else {
            this.warn(String.format(message, args));
        }
    }

    public void DEBUG(String message, Object... args) {
        if (this.isDebugEnabled()) {
            if (this.pluginId != null) {
                this.debug("[" + pluginId + "]" + String.format(message, args));
            } else {
                this.debug(String.format(message, args));
            }
        }
    }
}
