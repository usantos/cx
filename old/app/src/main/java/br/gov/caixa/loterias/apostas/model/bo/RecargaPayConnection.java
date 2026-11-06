package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CardHolderRecargaPay;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CartaoRecargaPayDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CartaoRecargaPayResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DataExpiracao;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;

public class RecargaPayConnection {

	public static final String CARTAO_TYPE = "CREDIT";
	public static final String CARDHOLDER_TYPE = "NATURAL";

	public interface RecargaPayListener {
		void onSucesso(String customerCardToken, String payment_method_id);
		void onErro(String msgError);
	}

	private static void callRecargaPayService(String URL, int method, String tokenApostador, Context ctx, JSONObject jsonBody, RecargaPayListener listener){
		CartaoRecargaPayResponse[] cardResponse = {null};
		RequestQueue requestQueue = Volley.newRequestQueue(ctx);
		final String requestBody = jsonBody.toString();

		StringRequest stringRequest = new StringRequest(method, URL, response -> {
			if (cardResponse != null && cardResponse[0] != null){
				listener.onSucesso(cardResponse[0].getCustomerCardToken(), cardResponse[0].getBrand().toLowerCase());
			}
		}, error -> {
			listener.onErro(onCheckError(error));
		}) {
			@Override
			public String getBodyContentType() {
				return "application/json; charset=utf-8";
			}

			@Override
			public byte[] getBody() {
				try {
					return requestBody == null ? null : requestBody.getBytes("utf-8");
				} catch (UnsupportedEncodingException uee) {
					VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
					return null;
				}
			}

			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				String responseString = "";
				if (response != null) {
					String body = null;
					try {
						body = new String(response.data,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					cardResponse[0] = new Gson().fromJson(body, CartaoRecargaPayResponse.class);
				}
				return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
			}

			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> params = new HashMap<String, String>();

				params.put("accept", "application/json");
				params.put("x-api-version", "20220405");
				params.put("Content-Type", "application/json");
				params.put("Authorization", "Bearer " + tokenApostador);

				return params;
			}
		};

		requestQueue.add(stringRequest);
	}

	public static void callRecargaPayNovoCartao(String tokenApostador, Context ctx, CartaoRecargaPayDTO cartao, RecargaPayListener listener){
		String URL = BuildConfigManager.getVariavel("RP_CREATE_TOKEN_URL") + ServerMethods.CARDS_RECARGAPAY;

		JSONObject   jsonBody     = null;
		try {
			jsonBody = getJsonObject(cartao);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		callRecargaPayService(URL, Request.Method.PUT, tokenApostador, ctx, jsonBody, listener);
	}

	public static void callRecargaPayCartaoSalvo(String tokenApostador, Context ctx,
												 String cardToken, String securityCode,
												 RecargaPayListener listener){

		String URL = BuildConfigManager.getVariavel("RP_CREATE_TOKEN_URL") + ServerMethods.CARDS_RECARGAPAY + "/" + cardToken;
		JSONObject jsonBody = new JSONObject();
		try {
			jsonBody.put("securityCode", securityCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		callRecargaPayService(URL, Request.Method.PATCH, tokenApostador, ctx, jsonBody, listener);
	}

	private static String onCheckError(VolleyError error) {
		return MensagensNetwork.setMensagemRecargaPay(error, Aplicacao.application.getApplicationContext());
	}

	@NonNull
	private static JSONObject getJsonObject(CartaoRecargaPayDTO cartao) throws JSONException {
		JSONObject jsonBody = new JSONObject();
		jsonBody.put("type", cartao.getType());
		jsonBody.put("brand", cartao.getBrand());
		jsonBody.put("number", cartao.getNumber().replace(" ", ""));
		jsonBody.put("cardHolder", getCardHolderJsonObject(cartao.getCardHolder()));
		jsonBody.put("expirationDate", getExpirationDateJsonObject(cartao.getDataExpiracao()));
		jsonBody.put("securityCode", cartao.getSecurityCode());
		jsonBody.put("shouldSave", cartao.isShouldSave());
		return jsonBody;
	}

	private static JSONObject getCardHolderJsonObject(CardHolderRecargaPay cartao) throws JSONException {
		JSONObject jsonBody = new JSONObject();
		jsonBody.put("type", cartao.getType());
		jsonBody.put("name", cartao.getName());
		jsonBody.put("email", cartao.getEmail());
		jsonBody.put("document", cartao.getDocument());
		jsonBody.put("birthdate", cartao.getBirthdate());
		JSONArray jsonArray = new JSONArray(cartao.getPhones());
		jsonBody.put("phones", jsonArray);
		return jsonBody;
	}

	private static JSONObject getExpirationDateJsonObject(DataExpiracao data) throws JSONException {
		JSONObject jsonBody = new JSONObject();
		jsonBody.put("month", data.getMes());
		jsonBody.put("year", data.getAno());
		return jsonBody;
	}

}