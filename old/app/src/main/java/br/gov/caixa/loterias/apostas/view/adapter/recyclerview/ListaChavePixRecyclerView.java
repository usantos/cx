package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.holder.ChavePixHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ListaChavePixRecyclerView extends RecyclerView.Adapter<ChavePixHolder>{

	private List<String> chaves;
	private OnItemClickListener listener;

	public ListaChavePixRecyclerView(List<String> chaves, OnItemClickListener listener) {
		this.chaves = chaves;
		this.listener = listener;
	}

	@NonNull
	@Override
	public ChavePixHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chave,
																		parent, false);
		return new ChavePixHolder(view, parent.getContext(), listener);
	}

	@Override
	public void onBindViewHolder(@NonNull ChavePixHolder holder, int position) {
		String chave = chaves.get(position);
		holder.bind(chave, position);
	}
	@Override
	public int getItemCount() {
		return chaves.size();
	}

}
