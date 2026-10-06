package br.gov.caixa.loterias.apostas.view.listener;

public interface OnCartaoMeioPagamentoListener<T> {
	void itemClick(T holder, int position);
	void favoritar(T holder, int position);
	void deletar(T holder, int position);
}
