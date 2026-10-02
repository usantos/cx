package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoMicroServicoSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoNovaAPISingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoNuvemSingleton;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CertificadoSilceSingleton;

public class SSLPinningUtil {

    public static void pinagemTQS(){
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
        }
    }

    public static RequestQueue pinagemPRD(Context ctx, ServicoEnum servico) {
        InputStream inputStream;
        switch (servico) {
            case SILCE:
                inputStream = ctx.getResources().openRawResource(CertificadoSilceSingleton.getInstance().getCertificadoAtual());
                break;
            case MICROSERVICO:
                inputStream = ctx.getResources().openRawResource(CertificadoMicroServicoSingleton.getInstance().getCertificadoAtual());
                break;
            default:
                //Se ServicoEnum for NUVEM, APOSTADOR ou BFF, não existe pinagem.
                return new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        }

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
        }

        return requestQueue;
    }

}
