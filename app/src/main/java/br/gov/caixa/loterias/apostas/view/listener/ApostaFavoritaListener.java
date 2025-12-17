package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.view.holder.FavoritaHolder;

public interface ApostaFavoritaListener {

	void onDeleta(int position);

	//void onAltera(int position, FavoritaHolder holder);

	void onAdd(int position);

	//boolean onTemSwipe(int position);

}
