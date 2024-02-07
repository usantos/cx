package br.gov.caixa.loterias.apostas.utils;

import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;

public class SSLPiniingUtil {

    public static void pinagem(){
        if (BuildVersionUtil.isPRD()) {
            pinagemPRD();
        } else {
            pinagemTQS();
        }
    }

    private static void pinagemPRD(){
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = new BufferedInputStream(Aplicacao.application.getResources().openRawResource(R.raw.certificado));
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore trusted = KeyStore.getInstance(keyStoreType);
            trusted.load(null, null);
            trusted.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trusted);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory sf = context.getSocketFactory();
            Volley.newRequestQueue(Aplicacao.application.getApplicationContext(), new HurlStack(null, sf));
        } catch (CertificateException e){
            e.printStackTrace();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException |
                 KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static void pinagemTQS(){
        try {
            SSLContext context1 = SSLContext.getInstance("TLS");
            context1.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context1.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((arg0, arg1) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
