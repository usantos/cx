package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.ChavePix;
import br.gov.caixa.loterias.apostas.view.holder.ChavePixFavoritaHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnChaveFavoritaClickListener;

public class ListaChavePixFavoritaRecyclerView extends RecyclerView.Adapter<ChavePixFavoritaHolder>{

	private ArrayList<ChavePix> chaves;
	private OnChaveFavoritaClickListener listener;

	public ListaChavePixFavoritaRecyclerView(ArrayList<ChavePix> chaves, OnChaveFavoritaClickListener listener) {
		this.chaves = chaves;
		this.listener = listener;
	}

	@NonNull
	@Override
	public ChavePixFavoritaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chave_favorita,
																		parent, false);
		return new ChavePixFavoritaHolder(view, parent.getContext(), listener);
	}

	@Override
	public void onBindViewHolder(@NonNull ChavePixFavoritaHolder holder, int position) {
		ChavePix chave = chaves.get(position);
		holder.bind(chave, position);
	}

	@Override
	public int getItemCount() {
		return chaves.size();
	}

	public void atualizaLista(ArrayList<ChavePix> chaves) {
		this.chaves = chaves;
		notifyDataSetChanged();
	}
}
