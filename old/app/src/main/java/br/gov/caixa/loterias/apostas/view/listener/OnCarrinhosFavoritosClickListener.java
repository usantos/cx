package br.gov.caixa.loterias.apostas.view.listener;

import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

public interface OnCarrinhosFavoritosClickListener {

	void onExcluirCarrinho(int position);
	void onDetalhesCarrinho(LinearLayout layoutDetalhes, int position);
	void onTransformaCarrinho(int position);
	void onBuscaApostas(LinearLayout layoutDetalhes, ConstraintLayout clCard, boolean configLayout, int position);
}
