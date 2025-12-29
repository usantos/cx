package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.view.holder.LotericasFavoritasHolder;

public interface OnItemLotericaFavoritaListener {

	void verMais(LotericasFavoritasHolder holder, int position);
    void excluir(LotericasFavoritasHolder holder, int position);

}
