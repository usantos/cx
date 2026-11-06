package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UltimaCompraDTO;
import br.gov.caixa.loterias.apostas.model.dao.crud.UltimaCompraCRUD;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;

public class CompraModel extends AppModel{


	public CompraModel(Activity activity) {
		super(activity);
	}

	public void salvaUltimaCompra(BigDecimal valor, Date date){
		UltimaCompraCRUD crud            = new UltimaCompraCRUD(getActivity());
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
		DadosUsuarioBO.getInstance().cartoes(MeioPagamentoUtils.MP_MERCADO_PAGO, new RequestListener<ListCartaoDTOResponse>() {
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
}
