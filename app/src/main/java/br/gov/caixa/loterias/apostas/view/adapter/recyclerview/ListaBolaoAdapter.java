package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Bolao;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;

public class ListaBolaoAdapter extends RecyclerView.Adapter<BolaoHolder>{

	private List<Bolao> bolaoList;
	private OnItemBolaoListener listener;
	private Activity activity;

	public ListaBolaoAdapter(List<Bolao> bolaoList, OnItemBolaoListener listener, Activity activity){
		this.bolaoList = bolaoList;
		this.listener = listener;
		this.activity = activity;
	}

	@NonNull
	@Override
	public BolaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_bolao, parent, false);
		return new BolaoHolder(activity, view, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull BolaoHolder holder, int position) {
		Bolao bolao = bolaoList.get(position);
		holder.bind(bolao, position);
	}

	@Override
	public int getItemCount() {
		return bolaoList.size();
	}
}
