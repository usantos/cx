package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ListaModalidadesAdapter extends RecyclerView.Adapter<ListaModalidadesAdapter.ViewHolder> {

	private List<ModalidadeDisponivelCota> listModalidadeDisponivel;
	private OnItemClickListener listener;
	private Activity activity;
	private SparseBooleanArray itemClicked;

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private Activity activityHolder;
		private View viewTopFiltroModalidade;
		private TextView textBottomFiltroModalidade;
		private TextView textCenterFiltroModalidade;
		private ImageView imgTrevoMkp;
		private ImageView imgCheckMkp;

		public ViewHolder(Activity activityHolder, View view, OnItemClickListener onItemClickListener) {
			super(view);

			this.activityHolder = activityHolder;
			viewTopFiltroModalidade = view.findViewById(R.id.viewTopFiltroModalidade);
			textBottomFiltroModalidade = view.findViewById(R.id.textBottomFiltroModalidade);
			textCenterFiltroModalidade = view.findViewById(R.id.textCenterFiltroModalidade);
			imgTrevoMkp = view.findViewById(R.id.imgTrevoMkp);
			imgCheckMkp = view.findViewById(R.id.imgCheckMkp);

//			view.setOnClickListener(v -> {
//				if (onItemClickListener != null) {
//					int position = getAbsoluteAdapterPosition();
//					if (position != RecyclerView.NO_POSITION) {
//						onItemClickListener.itemClick(this, getAbsoluteAdapterPosition());
//					}
//				}
//			});
		}

		public Activity getActivityHolder() {
			return activityHolder;
		}
	}

	public ListaModalidadesAdapter(List<ModalidadeDisponivelCota> listModalidadeDisponivel, OnItemClickListener listener, Activity activity){
		this.listModalidadeDisponivel = listModalidadeDisponivel;
		this.listener = listener;
		this.activity = activity;
		this.itemClicked = new SparseBooleanArray();
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

		if (itemClicked.get(position,false)) {
			if (position == 0) {
				holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.blue_caixa));
				holder.imgCheckMkp.setVisibility(View.VISIBLE);
				holder.imgTrevoMkp.setVisibility(View.GONE);
				holder.textBottomFiltroModalidade.setVisibility(View.GONE);
				holder.textCenterFiltroModalidade.setVisibility(View.VISIBLE);
				holder.textCenterFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(holder.getActivityHolder(), holder.getActivityHolder().getString(R.string.todas_modalidades)));
				holder.textCenterFiltroModalidade.setTextColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.branco));
			} else {
				holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorClara()));
				holder.imgCheckMkp.setVisibility(View.VISIBLE);
				holder.imgTrevoMkp.setVisibility(View.VISIBLE);
				holder.textBottomFiltroModalidade.setVisibility(View.VISIBLE);
				holder.textCenterFiltroModalidade.setVisibility(View.GONE);
				holder.imgTrevoMkp.setImageDrawable(ContextCompat.getDrawable(holder.getActivityHolder(), estilo.getTrevoFundoEscuro()));
				holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(holder.getActivityHolder(), ModalidadeEnum.fromString(modalidade).toLowerCase()));
				holder.textBottomFiltroModalidade.setTextColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorFonteFundoEscuro()));
				GradientDrawable backgroud = (GradientDrawable) ContextCompat.getDrawable(holder.getActivityHolder(), R.drawable.custom_bottom_rouded);
				backgroud.setColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorEscura()));
				holder.textBottomFiltroModalidade.setBackground(backgroud);
			}
		} else {
			if (position == 0) {
				holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.branco));
				holder.imgCheckMkp.setVisibility(View.GONE);
				holder.imgTrevoMkp.setVisibility(View.GONE);
				holder.imgTrevoMkp.setVisibility(View.GONE);
				holder.textBottomFiltroModalidade.setVisibility(View.GONE);
				holder.textCenterFiltroModalidade.setVisibility(View.VISIBLE);
				holder.textCenterFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(holder.getActivityHolder(), holder.getActivityHolder().getString(R.string.todas_modalidades)));
				holder.textCenterFiltroModalidade.setTextColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.blue_caixa));
			} else {
				holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.branco));
				holder.imgTrevoMkp.setVisibility(View.GONE);
				holder.imgTrevoMkp.setVisibility(View.VISIBLE);
				holder.textBottomFiltroModalidade.setVisibility(View.VISIBLE);
				holder.textCenterFiltroModalidade.setVisibility(View.GONE);
				holder.imgTrevoMkp.setImageDrawable(ContextCompat.getDrawable(holder.getActivityHolder(), estilo.getTrevoFundoClaro()));
				holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(holder.getActivityHolder(), ModalidadeEnum.fromString(modalidade).toLowerCase()));
				holder.textBottomFiltroModalidade.setTextColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorFonteFundoEscuro()));
				GradientDrawable backgroud = (GradientDrawable) ContextCompat.getDrawable(holder.getActivityHolder(), R.drawable.custom_bottom_rouded);
				backgroud.setColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorEscura()));
				holder.textBottomFiltroModalidade.setBackground(backgroud);
			}
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int adapterPosition = holder.getAbsoluteAdapterPosition();
				if (adapterPosition == RecyclerView.NO_POSITION) return;;

				itemClicked.clear();
				itemClicked.put(adapterPosition, true);

				notifyDataSetChanged();

				listener.itemClick(holder, adapterPosition);
			}
		});
	}

	@Override
	public int getItemCount() {
		return listModalidadeDisponivel.size();
	}

//	public void bindSelecionado(@NonNull ViewHolder holder, int position) {
//		ModalidadeDisponivelCota itemModalidade = listModalidadeDisponivel.get(position);
//		ModalidadeEnum modalidade = itemModalidade.getModalidade();
//		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
//
//		if (position == 0) {
//			holder.cardFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.blue_caixa));
//			holder.imgTrevoMkp.setVisibility(View.GONE);
//			holder.textBottomFiltroModalidade.setVisibility(View.GONE);
//			holder.textCenterFiltroModalidade.setVisibility(View.VISIBLE);
//			holder.textCenterFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(holder.getActivityHolder(), holder.getActivityHolder().getString(R.string.todas_modalidades)));
//		} else {
//			holder.cardFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorClara()));
//			holder.imgTrevoMkp.setVisibility(View.VISIBLE);
//			holder.textBottomFiltroModalidade.setVisibility(View.VISIBLE);
//			holder.textCenterFiltroModalidade.setVisibility(View.GONE);
//			holder.cardFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorClara()));
//			holder.imgTrevoMkp.setImageDrawable(ContextCompat.getDrawable(holder.getActivityHolder(), estilo.getTrevoFundoEscuro()));
//			holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(holder.getActivityHolder(), ModalidadeEnum.fromString(modalidade).toLowerCase()));
//			holder.textBottomFiltroModalidade.setTextColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorFonteFundoEscuro()));
//			GradientDrawable backgroud = (GradientDrawable) ContextCompat.getDrawable(holder.getActivityHolder(), R.drawable.custom_bottom_rouded);
//			backgroud.setColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorEscura()));
//			holder.textBottomFiltroModalidade.setBackground(backgroud);
//		}
//	}
}
