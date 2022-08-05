package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UltimaCompraDTO;
import br.gov.caixa.loterias.apostas.model.dao.crud.UltimaCompraCRUD;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DateUtils;

public class CompraModel {

	private Activity activity;

	public CompraModel(Activity activity) {
		this.activity = activity;
	}

	public void salvaUltimaCompra(BigDecimal valor, Date date){
		UltimaCompraCRUD crud            = new UltimaCompraCRUD(activity);
		String           stringDate      = DateUtils.getDateToString(date, DateUtils.PATTERN_DD_MM_YYYY_HH_MM);
		List<UltimaCompraDTO>             list = crud.lerUltimaCompraPorValor(valor);
		if (list != null && !list.isEmpty()){
			for (UltimaCompraDTO ultimaCompraDTO: list){
				crud.deletaUltimaCompra(ultimaCompraDTO.getId());
			}
		}
		crud.inserirUltimaCompra(new UltimaCompraDTO(valor, stringDate));
	}

	public void buscaCartoes(OnSilceListener<List<RetornoCartao>> listener){
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
