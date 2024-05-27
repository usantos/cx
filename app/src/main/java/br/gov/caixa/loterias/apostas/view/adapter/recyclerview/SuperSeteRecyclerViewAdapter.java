package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.holder.SuperSeteHolder;

public class SuperSeteRecyclerViewAdapter extends RecyclerView.Adapter<SuperSeteHolder>{
	private List<List<List<Integer>>> matriz;
	private Context context;

	public SuperSeteRecyclerViewAdapter(List<List<List<Integer>>> matriz, Context context) {
		this.matriz = matriz;
		this.context = context;
	}

	@NonNull
	@Override
	public SuperSeteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partida_loteca,
																		parent, false);
		return new SuperSeteHolder(view, context);
	}

	@Override
	public void onBindViewHolder(@NonNull SuperSeteHolder holder, int position) {
		holder.bind(matriz.get(position), position);
	}

	@Override
	public int getItemCount() {
		return matriz.size();
	}
}
