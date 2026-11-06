package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class LoteriasHolder<T> extends RecyclerView.ViewHolder {

	public LoteriasHolder(View itemView) {
		super(itemView);
	}

	public abstract void bind(T item, int position);

}
