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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;

public class MercadoPagoConnection {

	public interface MercadoPagoListener {
		void onSucesso(String cardId, String payment_method_id);
		void onTokenFailure(String msgError);
		void onPaymentFailure(String msgError);
	}

	public static void callWebServicePayment_method(final String bin , String jsonObject, Context ctx, MercadoPagoListener listener) {
		String	url = BuildConfig.MP_PAYMENT_URL + ctx.getResources().getString(R.string.bin) + bin +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;

		StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
			try {
				RequestQueue rQueue = Volley.newRequestQueue(ctx);
				JSONArray  jsonArray   = new JSONArray(response);
				JSONObject responseObj;
				responseObj = jsonArray.getJSONObject(0);
				String payment_method_id = responseObj.getString(ctx.getResources().getString(R.string.payment_method_id));



				callWebServiceCreateToken(jsonObject, ctx, listener, rQueue, payment_method_id);
			} catch (JSONException e) {
				listener.onTokenFailure(montarMensagem(""));
				e.printStackTrace();
			}
		}, volleyError -> {
			onErroPayment(ctx, listener, url, volleyError);
		});

		RequestQueue rQueue = Volley.newRequestQueue(ctx);
		rQueue.add(request);
	}

	private static void callWebServiceCreateToken(String jsonObject, Context ctx, MercadoPagoListener listener, RequestQueue rQueue, String payment_method_id) {
		String url2 = BuildConfig.MP_CREATE_TOKEN_URL +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;

		StringRequest stringRequest = new StringRequest(Request.Method.POST, url2,
														response1 -> Log.i("VOLLEY", response1),
														error -> {
															onErroCreateToken(error, ctx, url2, listener);
														}) {
			@Override
			public String getBodyContentType() {
				return "application/json; charset=utf-8";
			}

			@Override
			public byte[] getBody() throws AuthFailureError {
				try {
					return jsonObject == null ? null : jsonObject.getBytes("utf-8");
				} catch (UnsupportedEncodingException uee) {
					VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonObject, "utf-8");
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
						Log.d("'", "'");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					CardTokenResponse cardResponse = new Gson().fromJson(body, CardTokenResponse.class);

					listener.onSucesso(cardResponse.getId(),payment_method_id);

				}
				return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
			}
		};
		rQueue.add(stringRequest);
	}

	private static void onErroPayment(Context ctx, MercadoPagoListener listener, String url, VolleyError volleyError) {
		AppCenterManager.registraErro(String.valueOf(volleyError.networkResponse.statusCode), "MP_" + url);
		String msg;
		if(Bin.fromBytes(volleyError.networkResponse.data).toUtf8().contains(ctx.getResources().getString(R.string.not_found))){
			msg = montarMensagem("MP_PAYMENT_METHOD_NOT_FOUND");
		}else {
			msg = montarMensagem("");
		}
		listener.onPaymentFailure(msg);
	}

	private static void onErroCreateToken(VolleyError error, Context ctx, String url2, MercadoPagoListener listener) {
		Log.e("VOLLEY", error.toString());

		String codErro = "";
		if(error.networkResponse != null && error.networkResponse.data != null){
			NetworkResponse erroResponse = error.networkResponse;
			String          stringJson   = new String(erroResponse.data);
			try {
				JSONObject obj = new JSONObject(stringJson);
				obj.getJSONArray("cause");
				JSONArray  array = obj.getJSONArray("cause");
				JSONObject jobj  = (JSONObject) array.get(0);
				codErro = (String) jobj.get("code");
			} catch (JSONException e) {
				e.printStackTrace();
				AppCenterManager.registraErro(codErro, "MP_"+url2);
				listener.onTokenFailure(montarMensagem(codErro));
			}
		}
		AppCenterManager.registraErro(codErro, "MP_"+url2);
		listener.onTokenFailure(montarMensagem(codErro));
	}

	private static String montarMensagem (String codError) {
		String codErrorString = codError == null ? "" : codError;
		String mensagemCompleta = MercadoPagoUtil.getMessageFromCode(codErrorString) + "\n\n";
		if (codError != null && codError != "MP_PAYMENT_METHOD_NOT_FOUND") {
			mensagemCompleta += codErrorString + "\n";
		}
		mensagemCompleta += DateUtils.getDateTime();
		return mensagemCompleta;
	}
}