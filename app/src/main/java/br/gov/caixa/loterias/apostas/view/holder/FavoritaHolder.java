package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.DezenaUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaDezenaRecyclerView;
import br.gov.caixa.loterias.apostas.view.config.DezenaConfig;
import br.gov.caixa.loterias.apostas.view.config.ShapeConfig;
import br.gov.caixa.loterias.apostas.view.listener.ApostaFavoritaListener;

public class FavoritaHolder extends LoteriasHolder<ApostaFavoritaDTO> {
	private final Activity activity;
	private ApostaFavoritaListener listener;
	private FragmentManager fm;
	//private TextView tituloApostaFavorita;
	private LinearLayout linearLayoutTopModalidades;
	private TextView tituloNomeApostaFavorita;
	private TextView numerosApostaFavorita;
	//private TextView nomeApostaSalvoTextView;
	private RelativeLayout editarRelativeLayoutAddCarrinho;
	//private RelativeLayout editarRelativeLayoutFavoritos;
	private RelativeLayout excluirRelativeLayoutFavoritos;
	private RelativeLayout containerSuperSete;
	//private LinearLayout linearLayoutLayoutContentAlterarFavoritasSalvo;
	//private RelativeLayout relativeLayoutContentAlterarFavoritasEditar;
	//private EditText editTextNomeDaAposta;
	//private RelativeLayout layoutSalvarNomeAposta;
	private LinearLayout infoAdicionais;
	private LinearLayout maisInformacoesLayout;
	private LinearLayout maisInformacoesMaisMilionaria;
	private TextView labelInformacaoText;
	private TextView informacaoTextMaisMilionaria;
	private TextView informacaoText;
	private View linhaDivisoriaAposFragment;
	private View linhaDivisoria;
	private TextView modalidadeTitulo;
	private View titulo;
	private ImageView trevo;
	private RelativeLayout trevoFundo;

	public FavoritaHolder(View view, Activity activity, ApostaFavoritaListener listener, FragmentManager fm) {
		super(view);
		this.activity = activity;
		this.listener = listener;
		this.fm = fm;
		//tituloApostaFavorita = itemView.findViewById(R.id.tituloApostaFavorita);
		linearLayoutTopModalidades = itemView.findViewById(R.id.linearLayoutTopModalidades);
		modalidadeTitulo = itemView.findViewById(R.id.textViewCardTitle);
		titulo = itemView.findViewById(R.id.linearLayoutTopModalidades);
		trevo = itemView.findViewById(R.id.trevoImagem);
		trevoFundo = itemView.findViewById(R.id.RelativeLayoutTrevo);

		tituloNomeApostaFavorita = itemView.findViewById(R.id.tituloNomeApostaFavorita);
		numerosApostaFavorita = itemView.findViewById(R.id.numerosApostaFavorita);
		//nomeApostaSalvoTextView = itemView.findViewById(R.id.nomeApostaSalvoTextView);
		editarRelativeLayoutAddCarrinho = itemView.findViewById(R.id.editarRelativeLayoutAddCarrinho);
		//editarRelativeLayoutFavoritos = itemView.findViewById(R.id.editarRelativeLayoutFavoritos);
		excluirRelativeLayoutFavoritos = itemView.findViewById(R.id.excluirRelativeLayoutFavoritos);
		//linearLayoutLayoutContentAlterarFavoritasSalvo = itemView.findViewById(R.id.linearLayoutLayoutContentAlterarFavoritasSalvo);
		//relativeLayoutContentAlterarFavoritasEditar = itemView.findViewById(R.id.relativeLayoutContentAlterarFavoritasEditar);
		//editTextNomeDaAposta = itemView.findViewById(R.id.editTextNomeDaAposta);
		//layoutSalvarNomeAposta = itemView.findViewById(R.id.layoutSalvarNomeAposta);
		infoAdicionais = itemView.findViewById(R.id.infoAdicionais);
		maisInformacoesLayout = itemView.findViewById(R.id.maisInformacoesLayout);
		maisInformacoesMaisMilionaria = itemView.findViewById(R.id.maisInformacoesMaisMilionaria);
		labelInformacaoText = itemView.findViewById(R.id.labelInformacaoText);
		informacaoTextMaisMilionaria = itemView.findViewById(R.id.informacaoTextMaisMilionaria);
		informacaoText = itemView.findViewById(R.id.informacaoText);
		containerSuperSete = itemView.findViewById(R.id.container_super_sete);
		linhaDivisoriaAposFragment = itemView.findViewById(R.id.linhaDivisoriaAposFragment);
		linhaDivisoria = itemView.findViewById(R.id.linhaDivisoria);
	}


	@Override
	public void bind(ApostaFavoritaDTO aposta, int position) {
		bind(aposta, position, false);
	}

	public void bind(ApostaFavoritaDTO aposta, int position, boolean mostraBanner) {
//		final SwipeLayout swipeLayout = (SwipeLayout) itemView;
//		swipeLayout.setClickToClose(true);
//		swipeLayout.getDragEdgeMap().clear();
//		swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
//		swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
//		//Retirando swipe
//		swipeLayout.setSwipeEnabled(false);
//		swipeLayout.addSwipeListener(onSwipeListener(aposta));
//		swipeLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener(aposta, swipeLayout, position));

		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()));
		//tituloApostaFavorita.setText(aposta.getModalidade().getDescricao().toLowerCase());
		//tituloApostaFavorita.setTextColor(activity.getResources().getColor(estilo.getCorLetraLista()));
		if (mostraBanner) {
			linearLayoutTopModalidades.setVisibility(View.VISIBLE);
			String descricao = ModalidadeEnum.fromString(ModalidadeEnum.fromInteger(aposta.getModalidade().getValor()));
			modalidadeTitulo.setText(descricao.toLowerCase());
			modalidadeTitulo.setTextColor(activity.getResources().getColor(estilo.getCorFonteFundoClaro()));
			modalidadeTitulo.setHint("Título");
			titulo.setBackgroundColor(activity.getResources().getColor(estilo.getCorClara()));
			trevo.setImageDrawable(activity.getResources().getDrawable(estilo.getTrevoFundoEscuro()));
			trevoFundo.setBackground(activity.getResources().getDrawable(estilo.getTrapezio()));
		} else {
			linearLayoutTopModalidades.setVisibility(View.GONE);
		}

		tituloNomeApostaFavorita.setText(aposta.getNome());
		tituloNomeApostaFavorita.setHint("Título");

		StringBuilder numeroStr = new StringBuilder();

		if(aposta.getModalidade().getValor().intValue() == 7) {
			containerSuperSete.setVisibility(View.VISIBLE);
			configuraSuperSete(aposta);
			if (position > 0) {
				linhaDivisoriaAposFragment.setVisibility(View.VISIBLE);
			}
			linhaDivisoria.setVisibility(View.GONE);
		} else {
			containerSuperSete.setVisibility(View.GONE);
			List<Integer> numSelecionados = aposta.getListaNumerosSelecionados();
			for (Integer num : numSelecionados) {
				if (num.equals(numSelecionados.get(aposta.getListaNumerosSelecionados().size() - 1))) {
					numeroStr.append(String.format(activity.getResources().getString(R.string.percent_zero_dois_d), num));
				} else {
					numeroStr.append(String.format(activity.getResources().getString(R.string.percent_zero_dois_d_traco), num));
				}
			}
		}
		if (numeroStr.toString().contains("100")){
			numerosApostaFavorita.setText(numeroStr.toString().replace("100","00"));
		} else {
			numerosApostaFavorita.setText(numeroStr.toString());
			numerosApostaFavorita.setContentDescription(numeroStr.toString().replace("-",","));
		}

		excluirRelativeLayoutFavoritos.setOnClickListener(v -> listener.onDeleta(position));

		editarRelativeLayoutAddCarrinho.setOnClickListener(view -> {
			listener.onAdd(position);
		});

//		layoutSalvarNomeAposta.setOnClickListener(v -> {
//			if (editTextNomeDaAposta.getText().toString().equals(activity.getResources().getString(R.string.string_vazia))) {
//				ViewUtils.alertTitleButton(activity, R.string.label_atencao,
//						activity.getResources().getString(R.string.MA002) + activity.getResources().getString(R.string.espaco_nome_da_aposta),
//						activity.getResources().getString(R.string.ok));
//			} else {
//				AppUtils.fechaTeclado(activity);
//				listener.onAltera(position, this);
//			}
//		});

		boolean isTimeCoracao = aposta.getTimeDoCoracao() != null && StringUtils.isNotEmpty(aposta.getTimeDoCoracao().getNome());
		boolean isMesSorte = aposta.getMesDeSorte() != null && StringUtils.isNotEmpty(aposta.getMesDeSorte().getNome()) && aposta.getMesDeSorte().getNumero().intValue() > 0;
		ModalidadeEnum modalidadeEnum = ModalidadeEnum.toString(aposta.getModalidade().getDescricao().toLowerCase(new Locale(activity.getResources().getString(R.string.pt), activity.getResources().getString(R.string.br))));
		if (isTimeCoracao || isMesSorte || modalidadeEnum.equals(ModalidadeEnum.MAIS_MILIONARIA)) {
			if (isTimeCoracao || isMesSorte) {
				infoAdicionais.setVisibility(View.VISIBLE);
				maisInformacoesLayout.setVisibility(View.VISIBLE);
				labelInformacaoText.setText(isTimeCoracao ? R.string.label_time_coracao_dois_pontos : R.string.label_mes_sorte_dois_pontos);
				informacaoText.setText(isTimeCoracao ? aposta.getTimeDoCoracao().getNome() + "/" + aposta.getTimeDoCoracao().getUf()
						: aposta.getMesDeSorte().getNome());
			}
			if (modalidadeEnum.equals(ModalidadeEnum.MAIS_MILIONARIA)) {
				infoAdicionais.setVisibility(View.VISIBLE);
				maisInformacoesMaisMilionaria.setVisibility(View.VISIBLE);

				String trevos = " ";
				if (aposta.getParametroTrevo() != null) {
					for (Object i : aposta.getParametroTrevo().getTrevosSelecionados()) {
						trevos += i.toString() + " - ";
					}
					informacaoTextMaisMilionaria.setText(trevos.substring(0, trevos.length() - 2));
				}
			}
		} else {
			infoAdicionais.setVisibility(View.GONE);
			maisInformacoesLayout.setVisibility(View.GONE);
			maisInformacoesMaisMilionaria.setVisibility(View.GONE);
		}
	}

//	public String getNomeAlterado(){
//		return editTextNomeDaAposta.getText().toString();
//	}

	private void configuraSuperSete(ApostaFavoritaDTO aposta) {
		ArrayList<RecyclerView> listaGridsSuperSete = new ArrayList<>();
		listaGridsSuperSete.add(itemView.findViewById(R.id.rv_super_sete_primeiro));
		listaGridsSuperSete.add(itemView.findViewById(R.id.rv_super_sete_segundo));
		listaGridsSuperSete.add(itemView.findViewById(R.id.rv_super_sete_terceiro));
		listaGridsSuperSete.add(itemView.findViewById(R.id.rv_super_sete_quarto));
		listaGridsSuperSete.add(itemView.findViewById(R.id.rv_super_sete_quinto));
		listaGridsSuperSete.add(itemView.findViewById(R.id.rv_super_sete_sexto));
		listaGridsSuperSete.add(itemView.findViewById(R.id.rv_super_sete_setimo));

		ArrayList<ListaDezenaRecyclerView> listaAdaptersSuperSete = new ArrayList<>();
		if (BuildVersionUtil.isAutomacao()){
			int index = 0;
			for (Object lista: aposta.getMatrizNumerosSelecionados()) {
				List<Integer> listaNova = (List<Integer>) lista;
				listaAdaptersSuperSete.add(new ListaDezenaRecyclerView(DezenaUtils.getDezenasOrdenadas(listaNova), new ArrayList<>(), getDezenaConfigSuper7(), null));
				//listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.super_sete_claro_mkp, itemView.getContext(), listaNova, AutomacaoUtils.getSuperSeteEnumPorColuna(index)));
				index++;
			}
		} else {
			for (Object lista: aposta.getMatrizNumerosSelecionados()) {
				List<Integer> listaNova = (List<Integer>) lista;
				listaAdaptersSuperSete.add(new ListaDezenaRecyclerView(DezenaUtils.getDezenasOrdenadas(listaNova), new ArrayList<>(), getDezenaConfigSuper7(), null));
				//listaAdaptersSuperSete.add(new NumerosSuperSeteAdapter(R.color.super_sete_claro_mkp, itemView.getContext(), listaNova));
			}
		}

		for(int i = 0; i < listaAdaptersSuperSete.size(); i++) {
			listaGridsSuperSete.get(i).setAdapter(listaAdaptersSuperSete.get(i));
			listaGridsSuperSete.get(i).setLayoutManager(new GridLayoutManager(itemView.getContext(), 1));
		}

	}

	private DezenaConfig getDezenaConfigSuper7() {
		ShapeConfig shapeConfig = new ShapeConfig(R.color.branco, R.color.super_sete_escuro_mkp);

		return new DezenaConfig(false, R.color.super_sete_letra_mkp,
				R.layout.item_dezena_detalhe_super7,
				shapeConfig,false);
	}

//	private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener(ApostaFavoritaDTO aposta, SwipeLayout swipeLayout, int position) {
//		return () -> {
//			if (listener.onTemSwipe(position)) {
//				if (aposta.getOpenSwipeLayout() == null) {
//					aposta.setOpenSwipeLayout(false);
//				} else {
//					if (aposta.getOpenSwipeLayout()) {
//						swipeLayout.open(false);
//					}
//				}
//			}
//		};
//	}

//	private SwipeLayout.SwipeListener onSwipeListener(ApostaFavoritaDTO aposta) {
//		return new SimpleSwipeListener() {
//			@Override
//			public void onStartOpen(SwipeLayout layout) {
//				aposta.setOpenSwipeLayout(true);
//			}
//
//			@Override
//			public void onStartClose(SwipeLayout layout) {
//				relativeLayoutContentAlterarFavoritasEditar.setVisibility(View.VISIBLE);
//				linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility(View.GONE);
//				aposta.setOpenSwipeLayout(false);
//			}
//		};
//	}

//	public void nomeAtualizado(ApostaFavoritaDTO apostaFavoritaDTO) {
//		apostaFavoritaDTO.setOpenSwipeLayout(true);
//		apostaFavoritaDTO.setNome(editTextNomeDaAposta.getText().toString());
//		tituloNomeApostaFavorita.setText(activity.getResources().getString(R.string.espaco_barra_reta_espaco) + editTextNomeDaAposta.getText().toString());
//		nomeApostaSalvoTextView.setText(activity.getResources().getString(R.string.barrra_aspas) +
//				editTextNomeDaAposta.getText().toString() + activity.getResources().getString(R.string.barrra_aspas));
//		editTextNomeDaAposta.setText(activity.getResources().getString(R.string.string_vazia));
//		linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility(View.VISIBLE);
//		relativeLayoutContentAlterarFavoritasEditar.setVisibility(View.GONE);
//	}
}
