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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnPublicKeyListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Bin;
import br.gov.caixa.loterias.apostas.model.dao.crud.MercadoPagoCRUD;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.BuildConfigManager;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.MercadoPagoUtil;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesDefaultEnum;
import br.gov.caixa.loterias.apostas.utils.ConfiguracoesEnum;
import br.gov.caixa.loterias.apostas.utils.TestVisao;

public class MercadoPagoConnection {

	private static final String MSG_ERRO_BUSCA_KEY = "Não foi possível realizar o pagamento agora. tente novamente mais tarde!";

	public interface MercadoPagoListener {
		void onSucesso(String cardId, String payment_method_id, boolean luhn_validation);
		void onTokenFailure(String msgError);
		void onPaymentFailure(String msgError);
	}

	public static void callWebServicePayment_method(final String bin , String jsonObject, Context ctx, MercadoPagoListener listener) {
		String publicKey = MercadoPagoUtil.getPublicKeyMercadoPago();

		if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_ATUALIZA_PUBLIC_KEY.get(),
				ConfiguracoesDefaultEnum.IS_ATUALIZA_PUBLIC_KEY.asBoolean()) || publicKey == null || publicKey.isEmpty()){
			silceBuscaKeyPublic(onSilceKeyPaymentMethodListener(bin, jsonObject, ctx, listener));
		}else {
			String	url = BuildConfigManager.getVariavel("MP_PAYMENT_URL") + ctx.getResources().getString(R.string.bin) +
					bin +  ctx.getResources().getString(R.string.public_key) + publicKey;

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
			}, error -> {
				apagaPublicKey();
				onErroPayment(ctx, listener, url, error);
			});

			RequestQueue rQueue = Volley.newRequestQueue(ctx);
			rQueue.add(request);
		}
	}

	@NotNull
	private static OnPublicKeyListener onSilceKeyPaymentMethodListener(String bin, String jsonObject, Context ctx, MercadoPagoListener listener) {
		return new OnPublicKeyListener() {
			@Override
			public void success() {
				callWebServicePayment_method(bin, jsonObject, ctx, listener);
			}

			@Override
			public void failed(String url, VolleyError volleyError) {
				if (volleyError == null){
					listener.onPaymentFailure(MSG_ERRO_BUSCA_KEY);
				} else {
					onErroPayment(ctx, listener, url, volleyError);
				}
				apagaPublicKey();
			}
		};
	}

	private static void apagaPublicKey() {
		new MercadoPagoCRUD(Aplicacao.application.getApplicationContext()).deletaTodos();
	}

	private static void silceBuscaKeyPublic(OnPublicKeyListener keyListener) {
		Context ctx = Aplicacao.application.getApplicationContext();
		Long                 id      = new Long(1);
		DadosCorporativosSilceBO.getInstance().buscaKeyMercadoPago(id, new RequestListener<MeioPagamentoResponse>() {
			@Override
			public void onResponse(MeioPagamentoResponse result) {
				if (result != null && result.getPayload() != null) {
					Long inserir = insereBanco(result, ctx, id);
					if (inserir > 0){
						SharedPreferencesUtils.setValor(ConfiguracoesEnum.IS_ATUALIZA_PUBLIC_KEY.get(), false);
						if (keyListener != null){
							keyListener.success();
						}
						TestVisao.toast(result.getPayload().getPublicKey());
					} else {
						if (keyListener != null){
							keyListener.failed("", null);
						}
					}
				} else {
					if (keyListener != null){
						keyListener.failed("", null);
					}
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				keyListener.failed("meios-pagamento/", error);
			}
		});
	}

	private static Long insereBanco(MeioPagamentoResponse result, Context ctx, Long id) {
		return new MercadoPagoCRUD(ctx).atualiza(id, result.getPayload().getPublicKey());
	}

	private static void callWebServiceCreateToken(String jsonObject, Context ctx, MercadoPagoListener listener, RequestQueue rQueue, String payment_method_id) {
		String publicKey = MercadoPagoUtil.getPublicKeyMercadoPago();

		final CardTokenResponse[] cardResponse = {null};

		if (SharedPreferencesUtils.getValorBoolean(ConfiguracoesEnum.IS_ATUALIZA_PUBLIC_KEY.get(),
				ConfiguracoesDefaultEnum.IS_ATUALIZA_PUBLIC_KEY.asBoolean()) || publicKey == null || publicKey.isEmpty()){
			silceBuscaKeyPublic(onBuscaSilceCreateTokenListener(jsonObject, ctx, listener, rQueue, payment_method_id));
		} else {
			String url2 = BuildConfigManager.getVariavel("MP_CREATE_TOKEN_URL") +  ctx.getResources().getString(R.string.public_key) +
					publicKey;

			StringRequest stringRequest = new StringRequest(Request.Method.POST, url2,
															response1 -> {
																Log.i("VOLLEY", response1);
																if (cardResponse != null && cardResponse[0] != null){
																	listener.onSucesso(cardResponse[0].getId(),payment_method_id, cardResponse[0].getLuhn_validation());
																}
															},
															error -> {
																apagaPublicKey();
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
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						cardResponse[0] = new Gson().fromJson(body, CardTokenResponse.class);

						//listener.onSucesso(cardResponse.getId(),payment_method_id, cardResponse.getLuhn_validation());
					}
					return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
				}
			};
			rQueue.add(stringRequest);
		}
	}

	@NotNull
	private static OnPublicKeyListener onBuscaSilceCreateTokenListener(String jsonObject, Context ctx, MercadoPagoListener listener, RequestQueue rQueue, String payment_method_id) {
		return new OnPublicKeyListener() {
			@Override
			public void success() {
				callWebServiceCreateToken(jsonObject, ctx, listener, rQueue, payment_method_id);
			}

			@Override
			public void failed(String url, VolleyError volleyError) {
				if (volleyError == null){
					listener.onTokenFailure(MSG_ERRO_BUSCA_KEY);
				} else {
					onErroPayment(ctx, listener, url,volleyError);
				}
				apagaPublicKey();
			}
		};
	}

	private static void onErroPayment(Context ctx, MercadoPagoListener listener, String url, VolleyError volleyError) {
		if(isVolleyErrorValid(volleyError)){
			AppCenterManager.registraErro(String.valueOf(volleyError.networkResponse.statusCode), "MP_" + url);
		} else {
			AppCenterManager.registraErro("STATUS_CODE_NAO_IDENTIFICADO", "MP_" + url);
		}

		String msg;
		if(isVolleyErrorValid(volleyError) && Bin.fromBytes(volleyError.networkResponse.data).toUtf8().contains(ctx.getResources().getString(R.string.not_found))){
			msg = montarMensagem("MP_PAYMENT_METHOD_NOT_FOUND");
		}else {
			msg = montarMensagem("");
		}
		listener.onPaymentFailure(msg);
	}

	private static boolean isVolleyErrorValid(VolleyError volleyError) {
		return volleyError != null && volleyError.networkResponse != null;
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

	private static String montarMensagem(String codError) {
		String codErrorString = codError == null ? "" : codError;
		String mensagemCompleta = MercadoPagoUtil.getMessageFromCode(codErrorString) + "\n\n";
		if (codError != null && codError != "MP_PAYMENT_METHOD_NOT_FOUND") {
			mensagemCompleta += codErrorString + "\n";
		}
		mensagemCompleta += DateUtils.getDateTime();
		return mensagemCompleta;
	}
}