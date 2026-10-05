package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDisponivelCota;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class ListaModalidadesAdapter extends RecyclerView.Adapter<ListaModalidadesAdapter.ModalidadeViewHolder> {

	private List<ModalidadeDisponivelCota> listModalidadeDisponivel;
	private List<ModalidadeDisponivelCota> listModalidadeComSemTodas;
	private boolean todasModalidadesAtiva = false;

	private RecyclerView recyclerView;
	private OnItemClickListener listener;
	private Activity activity;
	private SparseBooleanArray itemClicked;
	private int posicaoModalidadeSelecionada;
	private int posicaoModalidadeOriginal;

	public static class ModalidadeViewHolder extends RecyclerView.ViewHolder {

		private View viewTopFiltroModalidade;
		private TextView textBottomFiltroModalidade;
		private TextView textCenterFiltroModalidade;
		private ImageView imgTrevoMkp;
		private ImageView imgCheckMkp;

		public ModalidadeViewHolder(View view) {
			super(view);
			viewTopFiltroModalidade = view.findViewById(R.id.viewTopFiltroModalidade);
			textBottomFiltroModalidade = view.findViewById(R.id.textBottomFiltroModalidade);
			textCenterFiltroModalidade = view.findViewById(R.id.textCenterFiltroModalidade);
			imgTrevoMkp = view.findViewById(R.id.imgTrevoMkp);
			imgCheckMkp = view.findViewById(R.id.imgCheckMkp);
		}

	}

	public ListaModalidadesAdapter(Activity activity,List<ModalidadeDisponivelCota> listModalidadeDisponivel, OnItemClickListener listener, int posicaoModalidadeSelecionada, boolean todasModalidadesAtiva) {
		this.listModalidadeDisponivel = listModalidadeDisponivel;
		this.listModalidadeComSemTodas = new ArrayList<>(listModalidadeDisponivel);
		this.listener = listener;
		this.activity = activity;
		this.posicaoModalidadeSelecionada = posicaoModalidadeSelecionada;
		this.posicaoModalidadeOriginal = posicaoModalidadeSelecionada;
		this.todasModalidadesAtiva = todasModalidadesAtiva;
		this.itemClicked = new SparseBooleanArray();
		montaListComTodaModalidades(todasModalidadesAtiva);
	}

	@NonNull
	@Override
	public ModalidadeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filtra_modalidade, parent, false);
		return new ModalidadeViewHolder(view);
	}

	@Override
	public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		this.recyclerView = recyclerView;
	}

	@Override
	public void onBindViewHolder(@NonNull ModalidadeViewHolder holder, int position) {
		//ModalidadeDisponivelCota itemModalidade = listModalidadeDisponivel.get(position);
		ModalidadeDisponivelCota itemModalidade = listModalidadeComSemTodas.get(position);
		ModalidadeEnum modalidade = itemModalidade.getModalidade();
		//TODO: MEGA 30 ANOS//
		Boolean isMega30 = EspecialUtils.isMega30(itemModalidade.getModalidade(), itemModalidade.getConcurso(),itemModalidade.getTipoConcurso());
		//TODO: LOTECA PAIS//
		Boolean isLotecaPais = EspecialUtils.isLotecaPais(itemModalidade.getModalidade(), itemModalidade.getConcurso(),itemModalidade.getTipoConcurso());
		//EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(itemModalidade.getModalidade(),isMega30);
		EstiloModalidadeMKP estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(itemModalidade.getModalidade(),
				itemModalidade.getConcurso(), itemModalidade.getTipoConcurso());

		boolean isEspecial;
		if (itemModalidade.getTipoConcurso() != null && itemModalidade.getTipoConcurso().equals(TipoConcursoEnum.ESPECIAL)) {
			isEspecial = true;
		} else {
			isEspecial = false;
		}
		//selecionaModalidadeInicial
		if (position == posicaoModalidadeSelecionada) {
			itemClicked.put(position, true);
			holder.itemView.requestFocus();
		}

		if (itemClicked.get(position,false)) {
			//Selecionado
			if (todasModalidadesAtiva && position == 0) {
				mostraTodaModalidades(holder, true);
			} else {
				if (isEspecial) {
					if(isMega30) {
						holder.viewTopFiltroModalidade.setBackground(ContextCompat.getDrawable(activity, fundoImagemEspecial(estilo)));
						holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS,"").toLowerCase()));
					} else if(isLotecaPais) {
						holder.viewTopFiltroModalidade.setBackground(ContextCompat.getDrawable(activity, fundoImagemEspecial(estilo)));
						holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS,"").toLowerCase()));
					} else {
						holder.viewTopFiltroModalidade.setBackground(ContextCompat.getDrawable(activity, fundoImagemEspecial(estilo, false)));
						holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, ModalidadeEnum.getDescricaoEspecialDuasLinhas(modalidade).toLowerCase()));
					}
				} else {
					holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(activity, estilo.getCorClara()));
					holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, ModalidadeEnum.getDescricao(modalidade).toLowerCase()));
				}
				holder.imgTrevoMkp.setVisibility(View.VISIBLE);
				holder.textBottomFiltroModalidade.setVisibility(View.VISIBLE);
				holder.textCenterFiltroModalidade.setVisibility(View.GONE);
				if (EspecialUtils.isParametrosOutubroRosa() && modalidade == ModalidadeEnum.MEGA_SENA){
					holder.imgTrevoMkp.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.trevo_megasena_outubro_rosa_ativado));
				} else {
					holder.imgTrevoMkp.setImageDrawable(ContextCompat.getDrawable(activity, estilo.getTrevoFundoEscuro()));
				}
				holder.textBottomFiltroModalidade.setTextColor(ContextCompat.getColor(activity, estilo.getCorFonteFundoEscuro()));
				GradientDrawable backgroud = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.custom_bottom_rouded);
				backgroud.setColor(ContextCompat.getColor(activity, estilo.getCorEscura()));
				holder.textBottomFiltroModalidade.setBackground(backgroud);
				holder.imgCheckMkp.setImageDrawable(VectorUtils.getShape(R.drawable.check_mkp, estilo.getCorFonteFundoClaro()));
				holder.imgCheckMkp.setVisibility(View.VISIBLE);
			}
		//Desselecionado
		} else {
			if (todasModalidadesAtiva && position == 0) {
				mostraTodaModalidades(holder, false);
			} else {
				if (isEspecial) {
					if(isMega30){
						holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS,"").toLowerCase()));
					} else if (isLotecaPais) {
						holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS,"").toLowerCase()));
					} else {
						holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, ModalidadeEnum.getDescricaoEspecialDuasLinhas(modalidade).toLowerCase()));
					}
				} else {
					holder.textBottomFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, ModalidadeEnum.getDescricao(modalidade).toLowerCase()));
				}
				holder.viewTopFiltroModalidade.setBackgroundColor(ContextCompat.getColor(activity, R.color.branco));
				holder.imgCheckMkp.setVisibility(View.GONE);
				holder.imgTrevoMkp.setVisibility(View.GONE);
				holder.imgTrevoMkp.setVisibility(View.VISIBLE);
				holder.textBottomFiltroModalidade.setVisibility(View.VISIBLE);
				holder.textCenterFiltroModalidade.setVisibility(View.GONE);
				holder.imgTrevoMkp.setImageDrawable(ContextCompat.getDrawable(activity, estilo.getTrevoFundoClaro()));
				holder.textBottomFiltroModalidade.setTextColor(ContextCompat.getColor(activity, estilo.getCorFonteFundoEscuro()));
				GradientDrawable backgroud = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.custom_bottom_rouded);
				backgroud.setColor(ContextCompat.getColor(activity, estilo.getCorEscura()));
				holder.textBottomFiltroModalidade.setBackground(backgroud);
			}
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int adapterPosition = holder.getAbsoluteAdapterPosition();
				if (adapterPosition == RecyclerView.NO_POSITION) return;
				posicaoModalidadeSelecionada = adapterPosition;
				posicaoModalidadeOriginal = adapterPosition;
				itemClicked.clear();
				itemClicked.put(posicaoModalidadeSelecionada, true);

				notifyDataSetChanged();

				if (todasModalidadesAtiva) {
					listener.itemClick(holder, posicaoModalidadeSelecionada-1);
				} else {
					listener.itemClick(holder, posicaoModalidadeSelecionada);
				}
			}
		});


	}

	@Override
	public int getItemCount() {
		//return listModalidadeDisponivel.size();
		return listModalidadeComSemTodas.size();
	}

	public void limpaItemClicked() {
		itemClicked.clear();
		notifyDataSetChanged();

		listener.itemClick(null, 0);
	}

//	private int fundoImagemEspecial(ModalidadeEnum modalidade, boolean isMega30) {
//		if (modalidade == ModalidadeEnum.DUPLA_SENA) {
//			return R.drawable.dupla_pascoa;
//		}
//		if (modalidade == ModalidadeEnum.QUINA) {
//			return R.drawable.quina_sao_joao;
//		}
//		if (modalidade == ModalidadeEnum.LOTOFACIL) {
//			return R.drawable.lotofacil_independencia;
//		}
//		if (modalidade == ModalidadeEnum.MEGA_SENA) {
//			if (isMega30){
//				return R.drawable.cabecalho_mega_trinta_anos_carrossel;
//			} else {
//				return R.drawable.cabecalho_mega_virada_carrossel;
//			}
//		}
//		return R.color.branco;
//	}

	private int fundoImagemEspecial(EstiloModalidadeMKP estilo, boolean isMega30) {

		if (isMega30) {
			return R.drawable.mega_30_quadrado;
		}

		if (estilo.getImagemEspecialQuadrada() != -1) {
			return estilo.getImagemEspecialQuadrada();
		} else {
			return R.color.branco;
		}
	}
	private int fundoImagemEspecial(EstiloModalidadeMKP estilo) {

		if (estilo.getImagemEspecialQuadrada() != -1) {
			return estilo.getImagemEspecialQuadrada();
		} else {
			return R.color.branco;
		}
	}

	private void mostraTodaModalidades(ModalidadeViewHolder holder, boolean selecionado) {
		holder.viewTopFiltroModalidade.setBackground(ContextCompat.getDrawable(activity, R.color.branco));

		holder.textCenterFiltroModalidade.setVisibility(View.VISIBLE);
		holder.textCenterFiltroModalidade.setText(ViewUtils.textFuturaAndFuturaBold(activity, activity.getString(R.string.todas_modalidades)));

		holder.imgTrevoMkp.setVisibility(View.GONE);
		holder.textBottomFiltroModalidade.setVisibility(View.GONE);

//		GradientDrawable backgroud = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.custom_bottom_rouded);
//		//backgroud.setColor(ContextCompat.getColor(activity, estilo.getCorEscura()));
//		holder.textBottomFiltroModalidade.setBackground(backgroud);

		if (selecionado) {
			holder.viewTopFiltroModalidade.setBackground(ContextCompat.getDrawable(activity, R.color.blue_caixa));
			holder.textCenterFiltroModalidade.setTextColor(ContextCompat.getColor(activity, R.color.branco));
			holder.imgCheckMkp.setImageDrawable(VectorUtils.getShape(R.drawable.check_mkp, R.color.branco));
			holder.imgCheckMkp.setVisibility(View.VISIBLE);
		} else {
			holder.viewTopFiltroModalidade.setBackground(ContextCompat.getDrawable(activity, R.color.branco));
			holder.textCenterFiltroModalidade.setTextColor(ContextCompat.getColor(activity, R.color.blue_caixa));
			holder.imgCheckMkp.setVisibility(View.GONE);
		}
	}

	public void montaListComTodaModalidades(boolean montaTodasModalidades) {
		listModalidadeComSemTodas.clear();
		listModalidadeComSemTodas.addAll(listModalidadeDisponivel);

		todasModalidadesAtiva = false;
		if (posicaoModalidadeOriginal == -1) {
			posicaoModalidadeOriginal = 0;
		}
		posicaoModalidadeSelecionada = posicaoModalidadeOriginal;

		if (montaTodasModalidades) {
			todasModalidadesAtiva = true;
			//Insere "Todas as Modalidades"
			ModalidadeDisponivelCota modalidadeDisponivelTodas = new ModalidadeDisponivelCota();
			ModalidadeEnum modalidadeTodas = ModalidadeEnum.BOLAO;
			modalidadeDisponivelTodas.setModalidade(modalidadeTodas);
			listModalidadeComSemTodas.add(0, modalidadeDisponivelTodas);

			posicaoModalidadeSelecionada = 0;
		}

		//força o foco no item 0 Todas modalidades
		itemClicked.clear();
		itemClicked.put(posicaoModalidadeSelecionada, true);
		if (recyclerView !=null && getItemCount()>0) {
			recyclerView.post(() -> recyclerView.scrollToPosition(posicaoModalidadeSelecionada));
		}
		if (todasModalidadesAtiva) {
			listener.itemClick(recyclerView, posicaoModalidadeSelecionada-1);
		} else {
			listener.itemClick(recyclerView, posicaoModalidadeSelecionada);
		}

		notifyDataSetChanged();
	}
}
