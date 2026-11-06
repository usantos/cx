package br.gov.caixa.loterias.apostas.view.listener;

public interface OnMensagemPushListener {

	void clickItem(int position);
	void delete(int position);
	void read(int position);
	void share(int position);

}
