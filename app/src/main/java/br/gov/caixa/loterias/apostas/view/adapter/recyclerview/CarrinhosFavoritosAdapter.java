package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.view.holder.CarrinhosFavoritosHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnCarrinhosFavoritosClickListener;

public class CarrinhosFavoritosAdapter extends RecyclerView.Adapter<CarrinhosFavoritosHolder> {
	//region Variables
	private List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos;
	private List<ApostaCarrinhoFavoritoDTO> listaApostasCarrinho;
	private Context context;
	private CarrinhosFavoritosActivity activity;
	private LinearLayout layoutAtual;
	private String modalidadeNormal, modalidadeEspecial;
	private OnCarrinhosFavoritosClickListener listener;

	public CarrinhosFavoritosAdapter(List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos, CarrinhosFavoritosActivity activity,
									 OnCarrinhosFavoritosClickListener listener) {
		this.activity = activity;
		this.context = activity;
		this.listaCarrinhosFavoritos = listaCarrinhosFavoritos;
		this.listener = listener;
	}
	//endregion

	//region Life Cicle
	@NonNull
	@Override
	public CarrinhosFavoritosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View                      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_carrinho_favorito, null);
		RecyclerView.LayoutParams lp   = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);

		return new CarrinhosFavoritosHolder(view, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull CarrinhosFavoritosHolder holder, int position) {
		CarrinhoFavoritoDTO carrinhoAtual = listaCarrinhosFavoritos.get(position);
		holder.bind(carrinhoAtual, position);
	}
	//endregion

	//region Initialization Methods
	private CarrinhoFavoritoDTO getCarrinhoById(Long id){
		CarrinhoFavoritoDTO carrinhoFavorito = null;
		for (CarrinhoFavoritoDTO carrinho : listaCarrinhosFavoritos) {
			if (carrinho.getId() == id){
				carrinhoFavorito = carrinho;
				break;
			}
		}
		return carrinhoFavorito;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return listaCarrinhosFavoritos.size();
	}
	//endregion

	//region Layout Configuration Methods
	private void preencheLayoutApostas(List<ApostaCarrinhoFavoritoDTO> listaApostas, LinearLayout layout) {
		if (layout.findViewById(R.id.tv_nome_modalidade) == null) {
			for (ApostaCarrinhoFavoritoDTO aposta : listaApostas) {
				layout.addView(configViewAposta(aposta));
			}
		}
	}

	public void recarregaCelula(List<ApostaCarrinhoFavoritoDTO> listaApostas, LinearLayout layout) {
		layout.removeAllViews();
		preencheLayoutApostas(listaApostas, layout);
	}

	public LinearLayout getLayoutAtual() {
		return layoutAtual;
	}

	public void configViewsAposta(List<ApostaCarrinhoFavoritoDTO> listaApostas, LinearLayout layout, ConstraintLayout card) {
		preencheLayoutApostas(listaApostas, layout);
		configuraAnimacao(layout, card);
	}


	private void configuraAnimacao(LinearLayout layoutDetalhes, ConstraintLayout clCard) {
		if (layoutDetalhes.getVisibility() == View.GONE) {
			layoutDetalhes.animate()
						  .setDuration(100)
						  .alpha(1)
						  .setListener(new AnimatorListenerAdapter() {
							  @Override
							  public void onAnimationEnd(Animator animation) {
								  super.onAnimationEnd(animation);
								  layoutDetalhes.setVisibility(View.VISIBLE);
								  clCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.azul_card));

							  }
						  });
		} else {
			layoutDetalhes.animate()
						  .alpha(0)
						  .setDuration(100)
						  .setListener(new AnimatorListenerAdapter() {
							  @Override
							  public void onAnimationEnd(Animator animation) {
								  super.onAnimationEnd(animation);
								  layoutDetalhes.setVisibility(View.GONE);
								  clCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.verde_card));

							  }
						  });
		}
	}

	private View configViewAposta(ApostaCarrinhoFavoritoDTO aposta) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflater.inflate(R.layout.row_aposta_carrinho_favorito, null);
		((TextView) v.findViewById(R.id.tv_nome_modalidade)).setText(aposta.getModalidade().getDescricao().toLowerCase());

		if(aposta.getModalidade().getValor().equals(9)){
			((TextView) v.findViewById(R.id.tv_nome_modalidade)).setText(R.string.label_mais_milionaria);
			((TextView) v.findViewById(R.id.tv_qtd_numeros)).setText(aposta.getQuantidadeNumeros() + " num(s) " + aposta.getParametroTrevo().getTrevosSelecionados().size() +" trev(s)");
		} else {
			((TextView) v.findViewById(R.id.tv_qtd_numeros)).setText(aposta.getQuantidadeNumeros() + " num(s)");
		}

		//((TextView)v.findViewById(R.id.tv_valor_total_carrinho)).setText("R$10.00");
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			(v.findViewById(R.id.iv_teimosinha)).setVisibility(View.VISIBLE);
		}

		return v;
	}
	//endregion

	//region Support Methods
	public int temApostaEspecial() {
		int countModalidade = 0;
		List<ParametroSimulacao> parametros = null;
		SessaoUsuario sessao = SessaoUsuario.getInstance();
		if (sessao != null && sessao.getParametrosSimulacao() != null) {
			parametros = sessao.getParametrosSimulacao().getParametros();
		}
		if (parametros == null || parametros.isEmpty()) {
			return countModalidade;
		}

		if (listaApostasCarrinho != null) {
			for (ApostaCarrinhoFavoritoDTO aposta : listaApostasCarrinho) {
				countModalidade = 0;
				for (int param = 0; param < parametros.size() - 1; param++) {
					ModalidadeEnum modalidade = parametros.get(param).getParametroJogo().getConcurso().getModalidade();
					if (ModalidadeEnum.fromModalidadeDTO(aposta.getModalidade()) == modalidade) {
						countModalidade++;
					}
				}
				if (countModalidade >= 2) {
					modalidadeNormal = aposta.getModalidade().getDescricao();
					modalidadeEspecial = aposta.getModalidade().getDescricaoEspecial();
				}
			}
		}
		return countModalidade;
	}

	public void setLayoutAtual(LinearLayout layoutDetalhes) {
		this.layoutAtual = layoutDetalhes;
	}

	public void setListaApostasCarrinho(List<ApostaCarrinhoFavoritoDTO> listaApostasCarrinho) {
		this.listaApostasCarrinho = listaApostasCarrinho;
	}

	public String getModadlidadeNormal() {
		return this.modalidadeNormal;
	}

	public String getModalidadeEspecial(){
		return this.modalidadeEspecial;
	}
	//endregion
}
