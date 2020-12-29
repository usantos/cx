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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.BuildConfig;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

class MercadoPagoConnection {

	public static void callWebServicePayment_method(final String bin, String jsonObject, Context ctx) {
		String url = BuildConfig.MP_PAYMENT_URL + ctx.getResources().getString(R.string.bin) + bin +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;
		StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
			try {
				RequestQueue rQueue = Volley.newRequestQueue(ctx);
					JSONArray  jsonArray   = new JSONArray(response);
					JSONObject responseObj = new JSONObject();
					responseObj = jsonArray.getJSONObject(0);
					String payment_method_id = responseObj.getString(ctx.getResources().getString(R.string.payment_method_id));
					String thumbnail = (responseObj.getJSONObject(ctx.getResources().getString(R.string.issuer))).getString(ctx.getResources().getString(R.string.thumbnail));

					String url2 = BuildConfig.MP_CREATE_TOKEN_URL +  ctx.getResources().getString(R.string.public_key) + BuildConfig.PUBLIC_KEY_MP;

				StringRequest stringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.i("VOLLEY", response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("VOLLEY", error.toString());
					}
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
							responseString = String.valueOf(response.statusCode);
							// can get more details such as response.headers
						}
						return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
					}
				};
				rQueue.add(stringRequest);
				} catch (JSONException e) {
				e.printStackTrace();

			}
		}, volleyError -> {
			//alertDialog.dismiss();
			Log.d(ctx.getResources().getString(R.string.volley_error), ctx.getResources().getString(R.string.volleyError_dois_pontos) + volleyError.toString());
			if(Bin.fromBytes(volleyError.networkResponse.data).toUtf8().contains(ctx.getResources().getString(R.string.not_found))){
				ViewUtils.alert(ctx, ctx.getString(R.string.mercado_pago_numero_cartao_Incorreto));
			}
		});

		RequestQueue rQueue = Volley.newRequestQueue(ctx);
		rQueue.add(request);
	}
}
