package br.gov.caixa.loterias.apostas.view.listener;

public interface OnItemClickListener<T> {
	void click(T holder, int position);
}
