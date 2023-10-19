package br.gov.caixa.loterias.apostas.view.listener;

public interface OnViewAumentaDiminuiQuantidade {

	public void atualizaQuantidadeTextView(int quantidade, int maxPermitido);
	public void atualizaBotoesQtdCotas(int quantidade, int maxPermitido);
}
