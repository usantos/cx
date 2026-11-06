package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.holder.DezenaComboHolder;

public class ListaDezenaComboRecyclerView extends RecyclerView.Adapter<DezenaComboHolder>{
	private List<String> numeros;
	private Boolean isTrevo;

	public ListaDezenaComboRecyclerView(List<String> numeros, Boolean isTrevo) {
		this.numeros = numeros;
		this.isTrevo = isTrevo;
	}

	@NonNull
	@Override
	public DezenaComboHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dezena_combo_detalhe,
																		parent, false);
		return new DezenaComboHolder(view, isTrevo);
	}

	@Override
	public void onBindViewHolder(@NonNull DezenaComboHolder holder, int position) {
		holder.bind(numeros.get(position), position);
	}

	@Override
	public int getItemCount() {
		return numeros.size();
	}
}
