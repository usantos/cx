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
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;

public class CartoesModel extends AppModel{

	public CartoesModel(Activity activity) {
		super(activity);
	}

	public void buscaCartoesMercadoPago(OnSilceListener<List<RetornoCartao>> listener) {
		DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(getActivity());
		dadosUsuarioBO.cartoes(new RequestListener<ListCartaoDTOResponse>() {
			@Override
			public void onResponse(ListCartaoDTOResponse response) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_entrou_formas_pagamento));
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso( response.getRedirect(), getActivity());
				}

				listener.success(response.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		});
	}

	public void deletaCartaoMercadoPago(Long idCartao, OnSilceListener<List<RetornoCartao>> listener) {
		DadosUsuarioBO_.getInstance_(getActivity().getApplicationContext()).deleteCartao(idCartao, new RequestListener<ListCartaoDTOResponse>() {
			@Override
			public void onResponse(ListCartaoDTOResponse result) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_efetuou_exclusao_forma_pagamento_sucesso));
				if(result != null && result.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), getActivity());
				}

				listener.success(result.getPayload());
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				RedirectNetwork.checkRedirect(error, getActivity());
				listener.error(error);
			}
		});
	}
}
