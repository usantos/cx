package br.gov.caixa.loterias.apostas.view.listener;

public interface OnMeusCartoesListener<T> {
	void favoritar(T holder, int position);
	void deletar(T holder, int position);
}
