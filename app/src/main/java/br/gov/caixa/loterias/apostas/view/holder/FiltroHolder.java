package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;

public abstract class FiltroHolder<T> extends RecyclerView.ViewHolder {

	public FiltroHolder(View itemView) {
		super(itemView);
	}

	public abstract void bind(T item, int position, FiltroAplicadoMarketplace filtro);

}
