package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationService;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import br.gov.caixa.loterias.apostas.BuildConfig;

public final class AuthorizationServiceFactory {

	private AuthorizationServiceFactory() { }

	public static AuthorizationService getAuthorizationService(Context context) {
		AuthorizationService authorizationService;
		if (BuildConfig.DEBUG && !(BuildConfig.FLAVOR.equalsIgnoreCase("prd"))) {
			AppAuthConfiguration appAuthConfig = new AppAuthConfiguration.Builder().setConnectionBuilder(uri -> {
				URL url = new URL(uri.toString());
				HttpURLConnection connection =
						(HttpURLConnection) url.openConnection();
				if (connection instanceof HttpsURLConnection) {
					HttpsURLConnection connectionHttps = (HttpsURLConnection) connection;
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
						connectionHttps.setSSLSocketFactory(context1.getSocketFactory());
						return connectionHttps;
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				return null;
			}).build();
			authorizationService = new AuthorizationService(context, appAuthConfig);
		} else {
			authorizationService = new AuthorizationService(context);

		}
		return authorizationService;
	}

}
