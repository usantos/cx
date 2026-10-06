package br.gov.caixa.loterias.apostas.view.holder;

import android.util.Pair;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BolaoViewHolder<T> extends RecyclerView.ViewHolder {

	public BolaoViewHolder(View itemView) {
		super(itemView);
	}

	public abstract void bind(Pair<String, String> item, int position, int corFonteFundoEscuro, int total);

}
