package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class JogosComboHolder extends LoteriasHolder<IdentificaoDeUmaApostaDas8Modalidades> {
	private TextView modalidade, concurso, valor;

	public JogosComboHolder(View itemView) {
		super(itemView);
		modalidade = itemView.findViewById(R.id.id_modalidade);
		concurso = itemView.findViewById(R.id.id_concurso);
		valor = itemView.findViewById(R.id.id_valor);
	}

	@Override
	public void bind(IdentificaoDeUmaApostaDas8Modalidades aposta, int position) {
		modalidade.setText(ViewUtils.getNomeModalidadePorAposta(aposta).toLowerCase());
		modalidade.setTextColor(itemView.getContext().getColor(new EstiloModalidadeMKP(aposta.getModalidade()).getCorClara()));
		concurso.setText(itemView.getContext().getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
		ViewUtils.setMoedaFormatHtml(aposta.getValor(), this.valor);
	}
}
