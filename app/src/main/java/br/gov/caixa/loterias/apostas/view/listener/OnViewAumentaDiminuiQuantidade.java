package br.gov.caixa.loterias.apostas.view.listener;

public interface OnViewAumentaDiminuiQuantidade {

	void atualizaQuantidadeTextView(int quantidade, int maxPermitido);
	void atualizaBotoesQtdCotas(int quantidade, int maxPermitido);
}
