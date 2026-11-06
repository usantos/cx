package br.gov.caixa.loterias.apostas.view.listener;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import br.gov.caixa.loterias.apostas.view.holder.HomeViewHolder;

public interface OnHomeListener {
	void callWebserviceResultadoConcurso(RelativeLayout relativeLayoutTopResultados, LinearLayout linearLayoutResultadosApostas,
										 String modalidade, int concurso, int position,
										 RelativeLayout setaEsquerdaAcaoRelativeLayout, RelativeLayout setaDireitaAcaoRelativeLayout,
										 boolean animarView, HomeViewHolder holder);

	void setLayoutResultado(LinearLayout layout, int position);
}
