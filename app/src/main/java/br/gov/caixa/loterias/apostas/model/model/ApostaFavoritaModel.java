package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;

public class ApostaFavoritaModel {

	private Activity activity;

	public ApostaFavoritaModel(Activity activity) {
		this.activity = activity;
	}

	public void validaAposta(ApostaFavoritaDTO apostaFavoritaDTO, OnSilceListener<NetworkResponse> listener){
		ServicoFactoryUtil.getDadosCorporativoService().validarApostaFavorita(apostaFavoritaDTO, new RequestListener<NetworkResponse>() {
			@Override
			public void onResponse(NetworkResponse result) {
				listener.success(result);
			}
			@Override
			public void onErrorResponse(VolleyError error) {
				listener.error(error);
			}
		});
	}

	public void salvaAposta(ApostaFavoritaDTO apostaFavoritaDTO, OnSilceListener<NetworkResponse> listener){
		ServicoFactoryUtil.getDadosCorporativoService().salvarApostaFavorita(apostaFavoritaDTO, new RequestListener<NetworkResponse>() {
			@Override
			public void onResponse(NetworkResponse result) {
				listener.success(result);
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, activity);
				listener.error(error);
			}
		});
	}
}
