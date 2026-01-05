package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.view.holder.BolaoCarrosselHolder;

public interface OnItemCarrosselBolaoListener {

	void detalhes(int position);
	void adicionarCarrinho(int position);
	void diminui(BolaoCarrosselHolder holder, int position);
	void aumenta(BolaoCarrosselHolder holder, int position);
	void onFavoritarClick(int position);

}
