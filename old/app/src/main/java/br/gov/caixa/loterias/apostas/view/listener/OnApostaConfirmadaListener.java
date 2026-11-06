package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.view.holder.ApostaConfirmadaHolder;

public interface OnApostaConfirmadaListener {
	void onReload(int position);

	void onItemClick(int position, ApostaConfirmadaHolder holder);

	void onEmptyCellClick();
	void onItemReload(int position, ApostaConfirmadaHolder holder);
}
