package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ListaModalidadesAdapter extends RecyclerView.Adapter<ListaModalidadesAdapter.ViewHolder> {

	private List<ModalidadeDisponivelCota> listModalidadeDisponivel;
	private OnItemClickListener listener;
	private Activity activity;

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private Activity activityHolder;
		private TextView textBottomFiltroModalidade;
		private ImageView imgTrevoMkp;

		public ViewHolder(Activity activityHolder, View view, OnItemClickListener onItemClickListener) {
			super(view);

			this.activityHolder = activityHolder;
			textBottomFiltroModalidade = view.findViewById(R.id.textBottomFiltroModalidade);
			imgTrevoMkp = view.findViewById(R.id.imgTrevoMkp);

			view.setOnClickListener(v -> {
				if (onItemClickListener != null) {
					int position = getAbsoluteAdapterPosition();
					if (position != RecyclerView.NO_POSITION) {
						onItemClickListener.itemClick(this, getAbsoluteAdapterPosition());
					}
				}
			});
		}

		public Activity getActivityHolder() {
			return activityHolder;
		}
	}

	public ListaModalidadesAdapter(List<ModalidadeDisponivelCota> listModalidadeDisponivel, OnItemClickListener listener, Activity activity){
		this.listModalidadeDisponivel = listModalidadeDisponivel;
		this.listener = listener;
		this.activity = activity;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filtra_modalidade, parent, false);
		return new ViewHolder(activity, view, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ModalidadeDisponivelCota itemModalidade = listModalidadeDisponivel.get(position);
		ModalidadeEnum modalidade = itemModalidade.getModalidade();
		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);

		holder.imgTrevoMkp.setImageDrawable(ContextCompat.getDrawable(holder.getActivityHolder(), estilo.getTrevoFundoClaro()));
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
