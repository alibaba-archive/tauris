package com.aliyun.tauris.plugins.http;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ray Chaung<rockis@gmail.com>
 */
public class HtpasswdTest {

    String password = "abc";

    @Test
    public void testPlain() {
        String hash = "abc";
        Assert.assertEquals(hash, Htpasswd.plaintext.encrypt(password));
        Assert.assertTrue(Htpasswd.plaintext.verify(hash, password));
    }

    @Test
    public void testCrypt() {
        String hash = Htpasswd.bcrypt.encrypt(password);
        Assert.assertTrue(Htpasswd.bcrypt.verify(hash, password));
    }

    @Test
    public void testSha() {
        String hash = "{SHA}qZk+NkcGgWq6PiVxeFDCbJzQ2J0=";
        Assert.assertEquals(hash, Htpasswd.sha.encrypt(password));
        Assert.assertTrue(Htpasswd.sha.verify(hash, password));
    }

    @Test
    public void testMd5() {
        String hash = "$apr1$6PCRB9ev$YT/lpLUHUzBWZgCRyvPFY0";
        Assert.assertTrue(Htpasswd.md5.verify(hash, password));

        hash = Htpasswd.md5.encrypt(password);
        Assert.assertTrue(Htpasswd.md5.verify(hash, password));
    }

    @Test
    public void testVerify() {
        String md5 = "$apr1$ei9vdpgC$diUgIV6n0t95YfH8Xrf3S.";
        String sha = "{SHA}mpAPU4llpCaZTh6QYAkgr/C06NI=";
//        String bcrypt = "$2y$05$eCOZ1D2M3.OxkbgaG2uTNOxoXTkpCmNIdk2eC75JVFux/viDMnAlq";
        String crypt = "3xBJMUiPy0ShM";
        String plain = "bb";
        Assert.assertTrue(Htpasswd.verify(plain, md5, false));
        Assert.assertTrue(Htpasswd.verify(plain, sha, false));
        Assert.assertTrue(Htpasswd.verify(plain, crypt, false));
//        Assert.assertTrue(Htpasswd.verify(plain, bcrypt, false));
        Assert.assertTrue(Htpasswd.verify(plain, plain, true));
    }
}
