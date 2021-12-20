package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity;

public abstract class LoteriasHolder<T> extends RecyclerView.ViewHolder {

	public LoteriasHolder(View itemView) {
		super(itemView);
	}

	public abstract void bind(T item, MinhasApostasActivity parentActivity);

}
