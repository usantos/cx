package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bean.Paginacao;

public interface OnPaginacaoListener {

	Paginacao getPaginacao();
	void proximaPagina();
	void voltaPagina();
}
