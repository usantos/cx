package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeiosPagamentosResponse;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class MeioPagamentoModel extends AppModel{

	public MeioPagamentoModel(Activity activity) {
		super(activity);
	}

	public void buscaMeiosPagamentos(OnSilceListener<List<MeioPagamentoDTO>> listener) {
		DadosCorporativosBO_ service = DadosCorporativosBO_.getInstance_(getActivity());
		service.buscaMeiosPagamentos(new RequestListener<MeiosPagamentosResponse>() {
			@Override
			public void onResponse(MeiosPagamentosResponse result) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_entrou_formas_pagamento));
				if (result.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), getActivity());
				}

				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
			}
		});
	}
}
