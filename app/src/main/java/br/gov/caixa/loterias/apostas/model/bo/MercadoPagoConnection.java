package br.gov.caixa.loterias.apostas.model.bo;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class MercadoPagoConnection {

	public interface MercadoPagoListener {
		void onSucesso(String cardId, String payment_method_id);
	}

	public static void callWebServicePayment_method(final String bin , String jsonObject, Context ctx, MercadoPagoListener listener) {
		DialogUtils.show(ctx);
		String	url = BuildConfig.MP_PAYMENT_URL + ctx.getResources().getString(R.string.bin) + bin +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;

		StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
			try {
				RequestQueue rQueue = Volley.newRequestQueue(ctx);
				JSONArray  jsonArray   = new JSONArray(response);
				JSONObject responseObj;
				responseObj = jsonArray.getJSONObject(0);
				String payment_method_id = responseObj.getString(ctx.getResources().getString(R.string.payment_method_id));

				String url2 = BuildConfig.MP_CREATE_TOKEN_URL +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;

				StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, response1 -> Log.i("VOLLEY", response1), error -> {
					Log.e("VOLLEY", error.toString());
					DialogUtils.dismiss();
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
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							CardTokenResponse cardResponse = new Gson().fromJson(body, CardTokenResponse.class);
							DialogUtils.dismiss();
							listener.onSucesso(cardResponse.getId(),payment_method_id);

						}
						return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
					}
				};
				rQueue.add(stringRequest);
			} catch (JSONException e) {
				DialogUtils.dismiss();
				e.printStackTrace();
			}
		}, volleyError -> {
			DialogUtils.dismiss();
			if(Bin.fromBytes(volleyError.networkResponse.data).toUtf8().contains(ctx.getResources().getString(R.string.not_found))){
				ViewUtils.alert(ctx,Bin.fromBytes(volleyError.networkResponse.data).toUtf8().toString());
			}
		});

		RequestQueue rQueue = Volley.newRequestQueue(ctx);
		rQueue.add(request);
	}
}