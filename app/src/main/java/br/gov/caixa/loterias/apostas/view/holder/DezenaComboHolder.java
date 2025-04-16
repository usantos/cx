package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;

public class DezenaComboHolder extends LoteriasHolder<String> {
	private TextView txtNumero;
	private TextView txtTrevo;
	private Boolean isTrevo;

	public DezenaComboHolder(View itemView, Boolean isTrevo) {
		super(itemView);

		txtNumero = itemView.findViewById(R.id.id_item_numero);
		txtTrevo = itemView.findViewById(R.id.id_item_trevo);
		this.isTrevo = isTrevo;
	}

	@Override
	public void bind(String item, int position) {
		if (isTrevo){
			txtTrevo.setVisibility(View.VISIBLE);
			txtNumero.setVisibility(View.GONE);
			txtTrevo.setText(item);
		} else {
			txtTrevo.setVisibility(View.GONE);
			txtNumero.setVisibility(View.VISIBLE);
			txtNumero.setText(item);
		}
	}

}
