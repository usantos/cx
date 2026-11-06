package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.OrientacaoPix;

public class OrientacaoPixHolder extends LoteriasHolder<OrientacaoPix> implements  View.OnClickListener {

	private TextView txtNumero;
	private TextView txtDescricao;

	private Context context;

	public OrientacaoPixHolder(View itemView) {
		super(itemView);
		this.context = context;

		txtNumero = itemView.findViewById(R.id.tv_item_numero);
		txtDescricao = itemView.findViewById(R.id.tv_item_descricao);
	}

	@Override
	public void bind(OrientacaoPix orientacaoPix, int position) {
		txtNumero.setText(orientacaoPix.getNumero()+ ". ");
		txtDescricao.setText(orientacaoPix.getDescricao());
	}

	@Override
	public void onClick(View v) {

	}
}
