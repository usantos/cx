package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostas.controllers.DetalhesCarrinhoFavoritoActivity;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import info.hoang8f.android.segmented.SegmentedGroup;

import static br.gov.caixa.loterias.apostas.controllers.DetalhesCarrinhoFavoritoActivity.ID_CARRINHO;

public class CarrinhosFavoritosAdapter extends RecyclerView.Adapter<CarrinhosFavoritosAdapter.CarrinhosFavoritosHolder> {

	//region Constants
	private final static int AMBAS = 0;
	//endregion

	//region Variables
	private Long idCarrinhoAtual;
	private List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos;
	private List<ApostaCarrinhoFavoritoDTO> listaApostasCarrinho;
	private Context context;
	private CarrinhosFavoritosActivity activity;
	private LinearLayout layoutAtual;
	private Dialog dialogSegmented;
	private String modalidadeNormal, modalidadeEspecial;
	//endregion

	//region Life Cicle
	@NonNull
	@Override
	public CarrinhosFavoritosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View                      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_carrinho_favorito, null);
		RecyclerView.LayoutParams lp   = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);

		return new CarrinhosFavoritosHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CarrinhosFavoritosHolder holder, int position) {
		CarrinhoFavoritoDTO carrinhoAtual = listaCarrinhosFavoritos.get(position);
		holder.tvNomeCarrinho.setText(carrinhoAtual.getNome());
	}
	//endregion

	//region Initialization Methods
	public CarrinhosFavoritosAdapter(List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos, CarrinhosFavoritosActivity activity) {
		this.activity = activity;
		this.context = activity;
		this.listaCarrinhosFavoritos = listaCarrinhosFavoritos;
	}

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

	//region Network Methods
	private void chamaTransformarCarrinho(int opcaoSelecionada) {
		AlertDialogUtils.show(context);
		ApostaBO_.getInstance_(context).transformarCarrinho(idCarrinhoAtual, opcaoSelecionada, new RequestListener<CarrinhoDTOResponse>() {
			@Override
			public void onResponse(CarrinhoDTOResponse result) {
				salvarCarrinhoFavoritoLocal(idCarrinhoAtual);
				AlertDialogUtils.dismiss();
				if (result.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
				}
				if (dialogSegmented != null && dialogSegmented.isShowing()) {
					dialogSegmented.dismiss();
				}
				activity.startActivity(new Intent(activity, CarrinhoActivity.class));
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				AlertDialogUtils.dismiss();
				RedirectNetwork.checkRedirect(error, activity);
			}
		});
	}

	private void salvarCarrinhoFavoritoLocal(Long idCarrinhoAtual) {
		CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(context);
		crud.inserirCarrinhoFavorito(getCarrinhoById(idCarrinhoAtual));
	}

	private void buscaApostas(LinearLayout layout, ConstraintLayout card, boolean configLayout) {
		AlertDialogUtils.show(context);
		ApostaBO_.getInstance_(context).buscaApostasCarrinhoFavorito(idCarrinhoAtual, new RequestListener<ListApostaCarrinhoFavoritoDTO>() {
			@Override
			public void onResponse(ListApostaCarrinhoFavoritoDTO result) {
				AlertDialogUtils.dismiss();
				if (result.getRedirect() != null) {
					RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
				}
				if (configLayout) {
					configViewsAposta(result.getPayload(), layout, card);
				} else {
					listaApostasCarrinho = result.getPayload();
					if (temApostaEspecial()) {
						setaDialogSegmentedControl();
						dialogSegmented.show();
					} else {
						chamaTransformarCarrinho(AMBAS);
					}
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				AlertDialogUtils.dismiss();
				RedirectNetwork.checkRedirect(error, activity);
			}
		});
	}

	private void excluirCarrinho(Long idCarrinhoAtual) {
		ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.carrinho_fav_deseja_excluir), (dialogInterface, i) -> {
			AlertDialogUtils.show(context);
			ApostaBO_.getInstance_(context).deletaCarrinhoFavorito(idCarrinhoAtual.toString(), new RequestListener<ListCarrinhoFavoritoDTO>() {
				@Override
				public void onResponse(ListCarrinhoFavoritoDTO result) {
					AlertDialogUtils.dismiss();
					if (result.getRedirect() != null) {
						RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
					}
					listaCarrinhosFavoritos = result.getPayload();
					activity.configuraRecyclerView(result);
				}

				@Override
				public void onErrorResponse(VolleyError error) {
					AlertDialogUtils.dismiss();
					RedirectNetwork.checkRedirect(error, activity);
				}
			});
		});
	}
	//endregion

	//region Layout Configuration Methods
	private void setaDialogSegmentedControl() {
		dialogSegmented = new Dialog(activity);
		dialogSegmented.setContentView(R.layout.custom_dialog_segmented_control);

		TextView tituloDialog    = dialogSegmented.findViewById(R.id.tv_titulo);
		TextView descricaoDialog = dialogSegmented.findViewById(R.id.tv_descricao);

		SegmentedGroup sgOpcoes = dialogSegmented.findViewById(R.id.sg_opcoes);

		RadioButton opcao1      = dialogSegmented.findViewById(R.id.rb_1);
		RadioButton opcao2      = dialogSegmented.findViewById(R.id.rb_2);
		RadioButton opcao3      = dialogSegmented.findViewById(R.id.rb_3);
		Button      btnOk       = dialogSegmented.findViewById(R.id.btn_ok);
		Button      btnCancelar = dialogSegmented.findViewById(R.id.btn_cancelar);

		String descricao = activity.getString(R.string.carrinho_fav_segmented_desc);
		descricao = descricao.replace("{modalidade}", modalidadeNormal);
		descricaoDialog.setText(descricao);

		sgOpcoes.check(R.id.rb_1);
		opcao1.setText("Ambos");

		opcao2.setText(modalidadeNormal);
		opcao3.setText(modalidadeEspecial);
		tituloDialog.setText("Carrinho Favorito");

		btnOk.setOnClickListener(v -> {
			int opcaoSelecionada = sgOpcoes.indexOfChild(dialogSegmented.findViewById(sgOpcoes.getCheckedRadioButtonId()));
			chamaTransformarCarrinho(opcaoSelecionada);
		});

		btnCancelar.setOnClickListener(v -> dialogSegmented.dismiss());
	}

	public void preencheLayoutApostas(List<ApostaCarrinhoFavoritoDTO> listaApostas, LinearLayout layout) {
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

	private void configViewsAposta(List<ApostaCarrinhoFavoritoDTO> listaApostas, LinearLayout layout, ConstraintLayout card) {
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
		((TextView) v.findViewById(R.id.tv_nome_modalidade)).setText(aposta.getModalidade().getDescricao());

		if(aposta.getModalidade().getValor().equals(9)){
			((TextView) v.findViewById(R.id.tv_nome_modalidade)).setText("+Milionária");
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
	private void transformaCarrinho() {
		CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(context);
		if(!crud.existeCarrinho(idCarrinhoAtual)){
			buscaApostas(null, null, false);
		}else {
			ViewUtils.alertTitleCustomPositiveListener(
					context,
					context.getString(R.string.cancelar), context.getString(R.string.carrinho_fav_incluir_carrinho),context.getString(R.string.carrinho_favorito_ja_adicionado),
					(dialogInterface, i) ->
							buscaApostas(null, null, false)
													  );
		}

	}

	private boolean temApostaEspecial() {
		int                      countModalidade;
		List<ParametroSimulacao> parametros = SessaoUsuario.getInstance().getParametrosSimulacao().getParametros();
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
					return true;
				}
			}
		}
		return false;
	}

	private void detalhesCarrinho(LinearLayout layoutDetalhes) {
		this.layoutAtual = layoutDetalhes;
		Bundle args = new Bundle();
		args.putLong(ID_CARRINHO, idCarrinhoAtual);
		activity.startActivityForResult(new Intent(activity, DetalhesCarrinhoFavoritoActivity.class).putExtras(args), activity.RESPOSTAS_APOSTAS_CODIGO);
	}
	//endregion

	//region ViewHolder
	class CarrinhosFavoritosHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private TextView tvNomeCarrinho, tvValorTotalCarrinho;
		private ImageButton ibExcluir, ibDetalhes, ibTransformar;
		private ConstraintLayout clCard;
		private LinearLayout layoutDetalhes;

		public CarrinhosFavoritosHolder(View itemView) {
			super(itemView);
			tvNomeCarrinho = itemView.findViewById(R.id.tv_nome_carrinho);
			tvValorTotalCarrinho = itemView.findViewById(R.id.tv_valor_total_carrinho);
			ibExcluir = itemView.findViewById(R.id.ib_excluir_carrinho);
			ibDetalhes = itemView.findViewById(R.id.ib_lupa_detalhes);
			ibTransformar = itemView.findViewById(R.id.ib_transformar_carrinho);
			clCard = itemView.findViewById(R.id.cl_card_carrinho_favorito);
			layoutDetalhes = itemView.findViewById(R.id.ll_apostas_carrinho);

			clCard.setOnClickListener(this);
			ibExcluir.setOnClickListener(this);
			ibDetalhes.setOnClickListener(this);
			ibTransformar.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			idCarrinhoAtual = listaCarrinhosFavoritos.get(getAdapterPosition()).getId();
			switch (view.getId()) {
				case R.id.tv_nome_carrinho:
					break;
				case R.id.tv_valor_total_carrinho:
					break;
				case R.id.ib_excluir_carrinho:
					excluirCarrinho(idCarrinhoAtual);
					break;
				case R.id.ib_lupa_detalhes:
					detalhesCarrinho(layoutDetalhes);
					break;
				case R.id.ib_transformar_carrinho:
					transformaCarrinho();
					break;
				case R.id.cl_card_carrinho_favorito:
					buscaApostas(layoutDetalhes, clCard, true);
					break;
				default:
					break;

			}
		}
	}
	//endregion
}
