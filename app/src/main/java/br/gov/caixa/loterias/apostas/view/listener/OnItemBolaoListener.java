package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;

public interface OnItemBolaoListener {

	void verMais(BolaoHolder holder, int position);
	void detalhes(int position);
	void adicionarCarrinho(int position);
	void diminui(BolaoHolder holder, int position);
	void aumenta(BolaoHolder holder, int position);
	void onFavoritarClick(int position);

}
