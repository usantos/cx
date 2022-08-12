package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class CartoesModel {
	private Activity activity;

	public CartoesModel(Activity activity) {
		this.activity = activity;
	}

	public void buscaCartoesMercadoPago(OnSilceListener<List<RetornoCartao>> listener) {
		DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(activity);
		dadosUsuarioBO.cartoes(new RequestListener<ListCartaoDTOResponse>() {
			@Override
			public void onResponse(ListCartaoDTOResponse response) {
				AppCenterManager.registraEvento(activity.getResources().getString(R.string.evento_entrou_formas_pagamento));
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso( response.getRedirect(), activity);
				}

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
