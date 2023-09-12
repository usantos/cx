package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnPublicKeyListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CardHolderRecargaPay;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CartaoRecargaPayDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CartaoRecargaPayResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MessagingResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.ConfiguracaoAppSingleton;
import br.gov.caixa.loterias.apostas.model.dao.crud.MercadoPagoCRUD;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;

public class MessagingConnection {

	public static final String APIKEY = "l789056c7986214044953e7efe92f483fd";

	public interface MessagingListener {
		void onSucesso(String codigo, String mensagem, String tokenDispositivo);
		void onErro(String msgError);
	}

	private static void trustEveryone() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(
					context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}

	public static void callMessagingCaixaSalvaToken(Context ctx, String cpf, String tokenDispositivo, MessagingConnection.MessagingListener listener) {
		String url = BuildConfig.URL_BASE_API_NOTIFICACOES + ServerMethods.NOTIFICACAO_REGISTRAR_DISPOSITIVO_PATH;

		KeycloakBO keycloakBO = KeycloakBO_.getInstance_(Aplicacao.application.getApplicationContext());
		String tokenApostador = keycloakBO.getAccessToken();

		if (!BuildVersionUtil.isPRD()) {
			//NAO Pode ser chamado em PRD
			trustEveryone();
		}

		JSONObject postData = new JSONObject();
		try {
			postData.put("plataforma", "android");
			postData.put("canal", "loterias");
			postData.put("token", tokenDispositivo);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(Long.valueOf(cpf));
			postData.put("identificadores", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					if (response != null) {
						String body = response.toString();
						MessagingResponse messagingResponses = new Gson().fromJson(body, MessagingResponse.class);
						if (messagingResponses != null){
							listener.onSucesso(messagingResponses.getCodigo(), messagingResponses.getMensagem(), tokenDispositivo);
							return;
						}
					}
					listener.onSucesso("", "INESPERADO", "");
				}
			},
			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					String responseBody = "";
					try {
						if (error.networkResponse != null && error.networkResponse.data != null) {
							responseBody = new String(error.networkResponse.data, "utf-8");
							JSONObject jsonObject = new JSONObject(responseBody);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Log ou mostra a resposta de erro
					Log.e("VolleyError", "Erro: " + responseBody);
					listener.onErro("Erro: " + responseBody);
				}
			}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<>();
				headers.put("Authorization", "Bearer " + tokenApostador);
				headers.put("Accept", "*/*");
				headers.put("apikey", APIKEY);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};

		//Criar uma RequestQueue
		RequestQueue queue = Volley.newRequestQueue(ctx);
		queue.add(jsonObjectRequest);
	}

}