package com.aliyun.tauris.plugins.http;

import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.TPluginInitException;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
@Name("basic")
public class BasicAuthenticator implements TAuthenticator {

    public static final String AUTH_METHOD = "Basic";

    File htpasswdFile;

    /**
     * 是否支持纯文本密码
     */
    boolean plainPassword = false;

    String username;
    String password;

    Map<String, String> htpasswd;

    public BasicAuthenticator() {
    }

    public BasicAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
        this.plainPassword = true;
    }

    public void init() throws TPluginInitException {
        if (htpasswd == null) {
            this.htpasswd = new HashMap<>();
        }
        if (htpasswdFile != null) {
            try {
                List<String> lines = FileUtils.readLines(htpasswdFile, "UTF-8");
                for (String line : lines) {
                    line = line.trim();
                    int indexOfC = line.indexOf(':');
                    if (indexOfC < 0) {
                        continue;
                    }
                    String u = line.substring(0, indexOfC);
                    String h = line.substring(indexOfC + 1);
                    htpasswd.put(u, h);
                }
            } catch (IOException e) {
                throw new TPluginInitException("read htpasswd file failed", e);
            }
        }
        if (username != null && password != null) {
            if (plainPassword) {
                this.htpasswd.put(username, password);
            } else {
                this.htpasswd.put(username, Htpasswd.md5.encrypt(password));
            }
        }
    }

    @Override
    public boolean check(String credential, HttpServletRequest request) {
        String userpasswd = new String(Base64.getDecoder().decode(credential));
        int    indexOfC   = userpasswd.indexOf(':');
        if (indexOfC < 0) {
            return false;
        }
        String u = userpasswd.substring(0, indexOfC);
        String p = userpasswd.substring(indexOfC + 1);
        String hash     = htpasswd.get(u);
        if (hash == null) {
            return false;
        }
        return Htpasswd.verify(p, hash, plainPassword);
    }
}
