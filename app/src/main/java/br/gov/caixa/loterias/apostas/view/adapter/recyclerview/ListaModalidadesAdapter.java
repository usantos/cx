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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
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

		public ViewHolder(Activity activityHolder, View view) {
			super(view);

			this.activityHolder = activityHolder;
			viewTopFiltroModalidade = view.findViewById(R.id.viewTopFiltroModalidade);
			textBottomFiltroModalidade = view.findViewById(R.id.textBottomFiltroModalidade);
			textCenterFiltroModalidade = view.findViewById(R.id.textCenterFiltroModalidade);
			imgTrevoMkp = view.findViewById(R.id.imgTrevoMkp);
			imgCheckMkp = view.findViewById(R.id.imgCheckMkp);

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
		return new ViewHolder(activity, view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ModalidadeDisponivelCota itemModalidade = listModalidadeDisponivel.get(position);
		ModalidadeEnum modalidade = itemModalidade.getModalidade();
		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
		boolean isEspecial;
		if (itemModalidade.getTipoConcurso() != null && itemModalidade.getTipoConcurso().equals(TipoConcursoEnum.ESPECIAL)) {
			isEspecial = true;
		} else {
			isEspecial = false;
		}

		if (itemClicked.get(position,false)) {
			//Selecionado
			if (position == 0) {
				holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.blue_caixa));
				holder.imgCheckMkp.setVisibility(View.VISIBLE);
				holder.imgTrevoMkp.setVisibility(View.GONE);
				holder.textBottomFiltroModalidade.setVisibility(View.GONE);
				holder.textCenterFiltroModalidade.setVisibility(View.VISIBLE);
				holder.textCenterFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(holder.getActivityHolder(), holder.getActivityHolder().getString(R.string.todas_modalidades)));
				holder.textCenterFiltroModalidade.setTextColor(ContextCompat.getColor(holder.getActivityHolder(), R.color.branco));
			} else {
				if (isEspecial) {
					holder.viewTopFiltroModalidade.setBackground(ContextCompat.getDrawable(holder.getActivityHolder(), fundoImagemEspecial(modalidade)));
				} else {
					holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(holder.getActivityHolder(), estilo.getCorClara()));
				}
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
		//Desselecionado
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

	private int fundoImagemEspecial(ModalidadeEnum modalidade) {
		if (modalidade == ModalidadeEnum.DUPLA_SENA) {
			return R.drawable.fundo_especial_dupla_sena_de_pascoa;
		}
		if (modalidade == ModalidadeEnum.QUINA) {
			return R.drawable.fundo_especial_quina_de_sao_joao;
		}
		if (modalidade == ModalidadeEnum.LOTOFACIL) {
			return R.drawable.fundo_especial_lotofacil_da_independencia;
		}
		if (modalidade == ModalidadeEnum.MEGA_SENA) {
			return R.drawable.fundo_especial_mega_da_virada;
		}
		return R.color.branco;
	}

}
