package a;

import java.security.KeyStore;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/** Binary-compatible repair for FilaBRA 2.6.5.1's permissive certificate validator. */
public final class d implements X509TrustManager {
    private final X509TrustManager platform;

    public d() {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init((KeyStore) null);
            X509TrustManager found = null;
            for (TrustManager manager : factory.getTrustManagers()) {
                if (manager instanceof X509TrustManager) {
                    found = (X509TrustManager) manager;
                    break;
                }
            }
            if (found == null) throw new IllegalStateException("Platform TLS trust manager unavailable");
            platform = found;
        } catch (GeneralSecurityException failure) {
            throw new IllegalStateException("Platform TLS trust manager unavailable", failure);
        }
    }

    @Override public final X509Certificate[] getAcceptedIssuers() { return platform.getAcceptedIssuers(); }
    @Override public final void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        platform.checkClientTrusted(chain, authType);
    }
    @Override public final void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        platform.checkServerTrusted(chain, authType);
    }
}
