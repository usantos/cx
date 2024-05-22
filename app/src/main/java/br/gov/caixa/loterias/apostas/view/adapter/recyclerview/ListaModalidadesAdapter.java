package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;

public class ListaModalidadesAdapter extends RecyclerView.Adapter<ListaModalidadesAdapter.ViewHolder> {

	private List<ModalidadeDisponivelCota> listModalidadeDisponivel;
	//private OnItemBolaoListener listener;
	private Activity activity;

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View view) {
			super(view);
		}

	}

	//public ListaModalidadesAdapter(List<CotasBolaoDTO> bolaoList, OnItemBolaoListener listener, Activity activity){
	public ListaModalidadesAdapter(List<ModalidadeDisponivelCota> listModalidadeDisponivel, Activity activity){
		this.listModalidadeDisponivel = listModalidadeDisponivel;
		//this.listener = listener;
		this.activity = activity;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filtra_modalidade, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
////		CotasBolaoDTO cotaBolao = bolaoList.get(position);
////		holder.bind(cotaBolao, position);
	}

	@Override
	public int getItemCount() {
		return listModalidadeDisponivel.size();
	}
}
