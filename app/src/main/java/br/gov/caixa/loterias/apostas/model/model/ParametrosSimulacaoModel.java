package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;

public class ParametrosSimulacaoModel {

	private Activity activity;

	public ParametrosSimulacaoModel(Activity activity) {
		this.activity = activity;
	}

	public void buscaParametroSiumulacao(OnSilceListener<ParametrosSimulacaoResponse> listener) {
		final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(activity);
		dadosCorporativosBO.parametrosSimulacao(new RequestListener<ParametrosSimulacaoResponse>() {
			@Override
			public void onResponse(ParametrosSimulacaoResponse response) {
				listener.success(response);
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, activity);
				listener.error(error);
			}
		});
	}
}
