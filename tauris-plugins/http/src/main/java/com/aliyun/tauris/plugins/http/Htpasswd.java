package com.aliyun.tauris.plugins.http;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.RandomStringUtils;

public enum Htpasswd {

    md5(new Htpasswd.Md5()), sha(new Htpasswd.Sha()), bcrypt(new Htpasswd.UnixCrypt()), plaintext(new Htpasswd.PlainText());

    private Algorithm algo;

    Htpasswd(Algorithm algo) {
        this.algo = algo;
    }

    public String encrypt(String password) {
        return algo.encrypt(password);
    }

    public boolean verify(String hash, String password) {
        return algo.matches(hash, password);
    }

    /**
     * Hash matches the password?
     * @param inputPwd The input password
     * @param storePwd stored password
     * @return TRUE if they match
     */
    public static boolean verify(final String inputPwd, final String storePwd, boolean supportPlain){
        for (Htpasswd p: Htpasswd.values()) {
            if (p == plaintext && !supportPlain) {
                continue;
            }
            if (p.verify(storePwd, inputPwd)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Algorithm.
     */
    private interface Algorithm {
        /**
         * Do they match?
         * @param hash The hash
         * @param password The password
         * @return TRUE if they match
         * @throws IOException If some error inside
         */
        boolean matches(String hash, String password);

        String encrypt(String password);
    }

    /**
     * MD5 hash builder.
     */
    private static final class Md5 implements Algorithm {
        /**
         * MD5 pattern.
         */
        private static final Pattern PATTERN =
                Pattern.compile("\\$apr1\\$([^\\$]+)\\$([a-zA-Z0-9/\\.]+=*)");
        @Override
        public boolean matches(final String hash, final String password) {
            final Matcher matcher = Htpasswd.Md5.PATTERN.matcher(hash);
            final boolean matches;
            if (matcher.matches()) {
                final String salt = matcher.group(1);
                final String result = Md5Crypt.apr1Crypt(password, salt);
                matches = hash.equals(result);
            } else {
                matches = false;
            }
            return matches;
        }

        @Override
        public String encrypt(String password) {
            String salt = RandomStringUtils.randomAlphabetic(8);
            return Md5Crypt.apr1Crypt(password, salt);
        }
    }

    /**
     * SHA1 hash builder.
     */
    private static final class Sha implements Algorithm {
        /**
         * SHA1 pattern.
         */
        private static final Pattern PATTERN =
                Pattern.compile("\\{SHA\\}([a-zA-Z0-9/\\+]+=*)");
        @Override
        public boolean matches(final String hash, final String password) {
            final Matcher matcher = Htpasswd.Sha.PATTERN.matcher(hash);
            final boolean matches;
            if (matcher.matches()) {
                final String required = Base64.encodeBase64String(DigestUtils.sha1(password));
                matches = matcher.group(1).equals(required);
            } else {
                matches = false;
            }
            return matches;
        }

        @Override
        public String encrypt(String password) {
            return "{SHA}" + Base64.encodeBase64String(DigestUtils.sha1(password));
        }
    }

    /**
     * UNIX crypt.
     */
    private static final class UnixCrypt implements Algorithm {
        /**
         * Unix Crypt pattern.
         */
        private static final Pattern PATTERN =
                Pattern.compile("(\\$[156]\\$)?[a-zA-Z0-9./]+(\\$.*)*");

        @Override
        public boolean matches(final String hash, final String password) {
            return Htpasswd.UnixCrypt.PATTERN.matcher(hash).matches()
                    && hash.equals(Crypt.crypt(password, hash));
        }

        @Override
        public String encrypt(String password) {
            return Crypt.crypt(password);
        }
    }

    /**
     * Plain Text.
     */
    private static final class PlainText implements Algorithm {
        @Override
        public boolean matches(final String hash, final String password) {
            return password.equals(hash);
        }

        @Override
        public String encrypt(String password) {
            return password;
        }
    }

}