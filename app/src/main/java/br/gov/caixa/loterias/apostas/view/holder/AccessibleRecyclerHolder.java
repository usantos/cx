package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;

public class AccessibleRecyclerHolder extends BolaoViewHolder<Pair<String, String>> {

	private final TextView tvLabel;
	private final TextView tvValue;
	private final Context context;

	public AccessibleRecyclerHolder(View itemView) {
		super(itemView);
		tvLabel = itemView.findViewById(R.id.tv_label);
		tvValue = itemView.findViewById(R.id.tv_value);
		context = itemView.getContext();
	}

	public void bind(Pair<String, String> item, int position, int corFonteFundoEscuro, int total) {
		tvLabel.setText(item.first);
		tvValue.setText(item.second);
		itemView.setFocusable(true);
		tvLabel.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
		tvValue.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
		itemView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
		tvValue.setTextColor(ContextCompat.getColor(context, corFonteFundoEscuro));
		tvLabel.setTextColor(ContextCompat.getColor(context, corFonteFundoEscuro));
		itemView.setContentDescription("Item :" + (position+1) + " de " + total + " " + item.first + " " + item.second);
	}

}
