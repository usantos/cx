package br.gov.caixa.loterias.apostas.view.listener;

public interface OnApostaConfirmadaListener {
	void onReload(int position);

	void onItemClick(int position);

	void onEmptyCellClick();
}
