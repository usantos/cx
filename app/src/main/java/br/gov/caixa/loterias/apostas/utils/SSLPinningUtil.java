package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;

public class SSLPinningUtil {

//    public static void pinagem(){
//        if (BuildVersionUtil.isPRD()) {
//            Toast.makeText(Aplicacao.application.getApplicationContext(), "PRD", Toast.LENGTH_LONG).show();
//            pinagemPRD();
//        } else {
//            Toast.makeText(Aplicacao.application.getApplicationContext(), "TQS", Toast.LENGTH_LONG).show();
//            pinagemTQS();
//        }
//    }

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

    public static RequestQueue testePinagem(Context ctx) {
        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory(ctx));
                    httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };

        return Volley.newRequestQueue(ctx, hurlStack);
    }

    private static HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> {
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            return hv.verify("www.loteriasonline.caixa.gov.br", session);
        };
    }

    private static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0){
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    private static SSLSocketFactory getSSLSocketFactory(Context ctx)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = ctx.getResources().openRawResource(R.raw.certificado);

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }

    public static RequestQueue getRequestQueue(Context ctx) {
        // Carregar o certificado do recurso raw
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.cert_invalido);
        RequestQueue requestQueue = null;
        try {
            // Carregar o certificado SSL
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(inputStream);

            // Configurar o trust manager para confiar apenas neste certificado
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("cert", cert);
            trustManagerFactory.init(keyStore);

            // Configurar o contexto SSL com o trust manager personalizado
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            // Configurar o HurlStack com o contexto SSL personalizado
            HurlStack hurlStack = new HurlStack(null, sslContext.getSocketFactory()) {
                @Override
                protected HttpsURLConnection createConnection(java.net.URL url) throws java.io.IOException {
                    HttpsURLConnection connection = (HttpsURLConnection) super.createConnection(url);
                    connection.setSSLSocketFactory(sslContext.getSocketFactory());
                    connection.setHostnameVerifier((hostname, session) -> {
                        // Verificar se o certificado do servidor coincide com o certificado que incluímos no aplicativo
                        try {
                            return cert.getSubjectX500Principal().getName().equals(session.getPeerPrincipal().getName());
                        } catch (SSLPeerUnverifiedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    return connection;
                }
            };
            requestQueue = Volley.newRequestQueue(ctx, hurlStack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestQueue;
    }

}
