package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.MeioPagamentoPix;
import br.gov.caixa.loterias.apostas.model.bo.ApostaSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixResponse;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.CompraPixUtil;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

public class PixModel extends AppModel {

	public PixModel(Activity activity) {
		super(activity);
	}

	public void gerarCodigoPix(OnSilceListener<GerarPixDTO> listener) {
		MeioPagamentoPix meioPagamento = new MeioPagamentoPix(6, null, false, "");

		//ServicoFactoryUtil.getApostaService().gerarCodigoPix(meioPagamento, new RequestListener<GerarPixResponse>() {
		ServicoFactoryUtil.getComprasService().gerarCodigoPix(meioPagamento, new RequestListener<GerarPixResponse>() {
			@Override
			public void onResponse(GerarPixResponse response) {
				AppCenterManager.registraEvento(getActivity().getResources().getString(R.string.evento_entrou_formas_pagamento));
				if (response.getRedirect() != null){
					RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
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

	public void verificaStatusPixPeriodicamente(GerarPixDTO pix, OnCompraAsync listener){
		CompraPixUtil.checkStatusCompraPeriodicamente(getActivity(), pix, pix.getIdCompra(), listener);
	}

	public void verificaStatusPix(GerarPixDTO pix, OnCompraAsync listener) {
		CompraPixUtil.checkStatusCompra(getActivity(), pix, pix.getIdCompra(), listener);
	}
}
