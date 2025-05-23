package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativos;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosNuvemBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacaoResponse;
import br.gov.caixa.loterias.apostas.utils.ServicoNuvemUtil;

public class ParametrosSimulacaoModel {

	private Activity activity;

	public ParametrosSimulacaoModel(Activity activity) {
		this.activity = activity;
	}

	public void buscaParametroSiumulacao(OnSilceListener<ParametrosSimulacao> listener) {
		DadosCorporativos dadosCorporativos;
		if (ServicoNuvemUtil.isServicoNuvem()) {
			dadosCorporativos = DadosCorporativosNuvemBO.getInstance();
		} else {
			dadosCorporativos = DadosCorporativosBO.getInstance();
		}
		dadosCorporativos.parametrosSimulacao(new RequestListener<ParametrosSimulacaoResponse>() {
			@Override
			public void onResponse(ParametrosSimulacaoResponse response) {
				listener.success(response.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, activity);
				listener.error(error);
			}
		});
	}
}
