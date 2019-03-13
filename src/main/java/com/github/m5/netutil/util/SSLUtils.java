package com.github.m5.netutil.util;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author xiaoyu
 */
public class SSLUtils {
    /**
     * TLS,TLSv1.2
     */
    private static final String SSL_TYPE = "TLSv1.2";
    /**
     * JKS,PKCS12
     */
    private static final String KS_TYPE = "PKCS12";

    public static SSLContext createSSLContext() {
        return createSSLContext(null, null, null);
    }

    /**
     * 创建加密上下文（请使用pfx类型证书）
     *
     * @param key      密钥
     * @param trust    信任证书，如果不传，将使用当前计算机中自带的信任证书
     * @param password 密码
     * @return 加密上下文
     */
    public static SSLContext createSSLContext(InputStream key, InputStream trust, String password) {
        try {
            KeyManagerFactory kmf = null;
            if (key != null) {
                KeyStore ks = KeyStore.getInstance(KS_TYPE);
                ks.load(key, password == null ? null : password.toCharArray());
                kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(ks, password == null ? null : password.toCharArray());
            }
            TrustManagerFactory tmf = null;
            if (trust != null) {
                KeyStore ks = KeyStore.getInstance(KS_TYPE);
                ks.load(trust, password == null ? null : password.toCharArray());
                tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);
            }

            SSLContext sslContext = SSLContext.getInstance(SSL_TYPE);
            sslContext.init(kmf == null ? null : kmf.getKeyManagers(), tmf == null ? null : tmf.getTrustManagers(), null);
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
