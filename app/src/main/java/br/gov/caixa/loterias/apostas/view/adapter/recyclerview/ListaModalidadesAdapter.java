package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;

public class ListaModalidadesAdapter extends RecyclerView.Adapter<ListaModalidadesAdapter.ViewHolder> {

	private List<ModalidadeDisponivelCota> listModalidadeDisponivel;
	//private OnItemBolaoListener listener;
	private Activity activity;

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private Activity activityHolder;
		private TextView textBottomFiltroModalidade;

		public ViewHolder(Activity activityHolder, View view) {
			super(view);

			this.activityHolder = activityHolder;
			textBottomFiltroModalidade = view.findViewById(R.id.textBottomFiltroModalidade);
		}

		public Activity getActivityHolder() {
			return activityHolder;
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
		return new ViewHolder(activity, view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ModalidadeDisponivelCota itemModalidade = listModalidadeDisponivel.get(position);
		ModalidadeEnum modalidade = itemModalidade.getModalidade();
		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);

		holder.textBottomFiltroModalidade.setText(ModalidadeEnum.fromString(modalidade).toLowerCase());
		holder.textBottomFiltroModalidade.setTextColor(estilo.getCorFonteFundoEscuro());
		GradientDrawable backgroud = (GradientDrawable) ContextCompat.getDrawable(holder.getActivityHolder(), R.drawable.custom_bottom_rouded);
		backgroud.setColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorEscura()));
		holder.textBottomFiltroModalidade.setBackground(backgroud);
	}

	@Override
	public int getItemCount() {
		return listModalidadeDisponivel.size();
	}
}
