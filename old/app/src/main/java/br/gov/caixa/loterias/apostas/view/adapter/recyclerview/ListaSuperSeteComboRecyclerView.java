package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.holder.DezenaSuperSeteComboHolder;

public class ListaSuperSeteComboRecyclerView extends RecyclerView.Adapter<DezenaSuperSeteComboHolder>{
	private List<List<String>> colunas;

	public ListaSuperSeteComboRecyclerView(List<List<String>> numeros) {
		this.colunas = numeros;
	}

	@NonNull
	@Override
	public DezenaSuperSeteComboHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_super_sete_combo_detalhe,
																		parent, false);
		return new DezenaSuperSeteComboHolder(view, parent.getContext());
	}

	@Override
	public void onBindViewHolder(@NonNull DezenaSuperSeteComboHolder holder, int position) {
		holder.bind(colunas.get(position), position);
	}

	@Override
	public int getItemCount() {
		return colunas.size();
	}
}
