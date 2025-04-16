package br.gov.caixa.loterias.apostas.view.holder;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.listener.ApostaFavoritaListener;

public class FavoritaHolder extends LoteriasHolder<ApostaFavoritaDTO> {
	private final Activity activity;
	private ApostaFavoritaListener listener;
	private FragmentManager fm;
	private TextView tituloApostaFavorita;
	private TextView tituloNomeApostaFavorita;
	private TextView numerosApostaFavorita;
	private TextView nomeApostaSalvoTextView;
	private RelativeLayout editarRelativeLayoutAddCarrinho;
	private RelativeLayout editarRelativeLayoutFavoritos;
	private RelativeLayout excluirRelativeLayoutFavoritos;
	private RelativeLayout containerFragment;
	private LinearLayout linearLayoutLayoutContentAlterarFavoritasSalvo;
	private RelativeLayout relativeLayoutContentAlterarFavoritasEditar;
	private EditText editTextNomeDaAposta;
	private RelativeLayout layoutSalvarNomeAposta;
	private LinearLayout maisInformacoesLayout;
	private LinearLayout maisInformacoesMaisMilionaria;
	private TextView labelInformacaoText;
	private TextView informacaoTextMaisMilionaria;
	private TextView informacaoText;
	public FavoritaHolder(View view, Activity activity, ApostaFavoritaListener listener, FragmentManager fm) {
		super(view);
		this.activity = activity;
		this.listener = listener;
		this.fm = fm;
		tituloApostaFavorita = itemView.findViewById(R.id.tituloApostaFavorita);
		tituloNomeApostaFavorita = itemView.findViewById(R.id.tituloNomeApostaFavorita);
		numerosApostaFavorita = itemView.findViewById(R.id.numerosApostaFavorita);
		nomeApostaSalvoTextView = itemView.findViewById(R.id.nomeApostaSalvoTextView);
		editarRelativeLayoutAddCarrinho = itemView.findViewById(R.id.editarRelativeLayoutAddCarrinho);
		editarRelativeLayoutFavoritos = itemView.findViewById(R.id.editarRelativeLayoutFavoritos);
		excluirRelativeLayoutFavoritos = itemView.findViewById(R.id.excluirRelativeLayoutFavoritos);
		linearLayoutLayoutContentAlterarFavoritasSalvo = itemView.findViewById(R.id.linearLayoutLayoutContentAlterarFavoritasSalvo);
		relativeLayoutContentAlterarFavoritasEditar = itemView.findViewById(R.id.relativeLayoutContentAlterarFavoritasEditar);
		editTextNomeDaAposta = itemView.findViewById(R.id.editTextNomeDaAposta);
		layoutSalvarNomeAposta = itemView.findViewById(R.id.layoutSalvarNomeAposta);
		maisInformacoesLayout = itemView.findViewById(R.id.maisInformacoesLayout);
		maisInformacoesMaisMilionaria = itemView.findViewById(R.id.maisInformacoesMaisMilionaria);
		labelInformacaoText = itemView.findViewById(R.id.labelInformacaoText);
		informacaoTextMaisMilionaria = itemView.findViewById(R.id.informacaoTextMaisMilionaria);
		informacaoText = itemView.findViewById(R.id.informacaoText);
		containerFragment = itemView.findViewById(R.id.rl_container_fragment);
	}

	@Override
	public void bind(ApostaFavoritaDTO aposta, int position) {
		final SwipeLayout swipeLayout = (SwipeLayout) itemView;
		swipeLayout.setClickToClose(true);
		swipeLayout.getDragEdgeMap().clear();
		swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
		swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
		//Retirando swipe
		swipeLayout.setSwipeEnabled(false);
		swipeLayout.addSwipeListener(onSwipeListener(aposta));

		swipeLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener(aposta, swipeLayout, position));

		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(ModalidadeEnum.toString(aposta.getModalidade().getDescricao().toLowerCase(new Locale(activity.getResources().getString(R.string.pt), activity.getResources().getString(R.string.br)))));
		tituloApostaFavorita.setText(aposta.getModalidade().getDescricao().toLowerCase());
		tituloApostaFavorita.setTextColor(activity.getResources().getColor(estilo.getCorLetraLista()));
		tituloNomeApostaFavorita.setText(activity.getResources().getString(R.string.espaco_barra_reta_espaco) + aposta.getNome());

		StringBuilder numeroStr = new StringBuilder();

		if(aposta.getModalidade().getValor().intValue() == 7) {
			//tituloApostaFavorita.setTextColor(activity.getResources().getColor(estilo.getCorEscura()));
			configuraFragment(aposta);
			containerFragment.setVisibility(View.VISIBLE);
		} else {
			containerFragment.setVisibility(View.GONE);
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
		}

		excluirRelativeLayoutFavoritos.setOnClickListener(v -> listener.onDeleta(position));

		editarRelativeLayoutAddCarrinho.setOnClickListener(view -> {
			listener.onAdd(position);
		});

		layoutSalvarNomeAposta.setOnClickListener(v -> {
			if (editTextNomeDaAposta.getText().toString().equals(activity.getResources().getString(R.string.string_vazia))) {
				ViewUtils.alertTitleButton(activity, R.string.label_atencao,
						activity.getResources().getString(R.string.MA002) + activity.getResources().getString(R.string.espaco_nome_da_aposta),
						activity.getResources().getString(R.string.ok));
			} else {
				AppUtils.fechaTeclado(activity);
				listener.onAltera(position, this);
			}
		});

		boolean isTimeCoracao = aposta.getTimeDoCoracao() != null && StringUtils.isNotEmpty(aposta.getTimeDoCoracao().getNome());
		boolean isMesSorte = aposta.getMesDeSorte() != null &&StringUtils.isNotEmpty(aposta.getMesDeSorte().getNome());
		if (isTimeCoracao || isMesSorte) {
			maisInformacoesLayout.setVisibility(View.VISIBLE);
			labelInformacaoText.setText(isTimeCoracao ? R.string.label_time_coracao_dois_pontos : R.string.label_mes_sorte_dois_pontos);
			informacaoText.setText(isTimeCoracao ? aposta.getTimeDoCoracao().getNome()  + "/" + aposta.getTimeDoCoracao().getUf()
					: aposta.getMesDeSorte().getNome());
		}else{
			maisInformacoesLayout.setVisibility(View.GONE);
		}

		ModalidadeEnum modalidadeEnum = ModalidadeEnum.toString(aposta.getModalidade().getDescricao().toLowerCase(new Locale(activity.getResources().getString(R.string.pt), activity.getResources().getString(R.string.br))));
		if (modalidadeEnum.equals(ModalidadeEnum.MAIS_MILIONARIA)){
			maisInformacoesMaisMilionaria.setVisibility(View.VISIBLE);
			tituloApostaFavorita.setText(R.string.label_mais_milionaria);

			String trevos = " ";
			for (Object i:  aposta.getParametroTrevo().getTrevosSelecionados()) {
				trevos += i.toString() + " - ";
			}
			informacaoTextMaisMilionaria.setText(trevos.substring(0, trevos.length() - 2));
		}else{
			maisInformacoesMaisMilionaria.setVisibility(View.GONE);
		}
	}

	public String getNomeAlterado(){
		return editTextNomeDaAposta.getText().toString();
	}

	private void configuraFragment(ApostaFavoritaDTO aposta) {
		int             containerId = containerFragment.getId();
		Fragment oldFragment = fm.findFragmentById(containerId);
		if(oldFragment != null) {
			fm.beginTransaction().remove(oldFragment).commit();
		}

		int newContainerId = (int) Utils.getIdUnico();
		containerFragment.setId(newContainerId);

		NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados());
		fm.beginTransaction().replace(newContainerId, fragment).commit();

	}

	private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener(ApostaFavoritaDTO aposta, SwipeLayout swipeLayout, int position) {
		return () -> {
			if (listener.onTemSwipe(position)) {
				if (aposta.getOpenSwipeLayout() == null) {
					aposta.setOpenSwipeLayout(false);
				} else {
					if (aposta.getOpenSwipeLayout()) {
						swipeLayout.open(false);
					}
				}
			}
		};
	}

	private SwipeLayout.SwipeListener onSwipeListener(ApostaFavoritaDTO aposta) {
		return new SimpleSwipeListener() {
			@Override
			public void onStartOpen(SwipeLayout layout) {
				aposta.setOpenSwipeLayout(true);
			}

			@Override
			public void onStartClose(SwipeLayout layout) {
				relativeLayoutContentAlterarFavoritasEditar.setVisibility(View.VISIBLE);
				linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility(View.GONE);
				aposta.setOpenSwipeLayout(false);
			}
		};
	}

	public void nomeAtualizado(ApostaFavoritaDTO apostaFavoritaDTO) {
		apostaFavoritaDTO.setOpenSwipeLayout(true);
		apostaFavoritaDTO.setNome(editTextNomeDaAposta.getText().toString());
		tituloNomeApostaFavorita.setText(activity.getResources().getString(R.string.espaco_barra_reta_espaco) + editTextNomeDaAposta.getText().toString());
		nomeApostaSalvoTextView.setText(activity.getResources().getString(R.string.barrra_aspas) +
				editTextNomeDaAposta.getText().toString() + activity.getResources().getString(R.string.barrra_aspas));
		editTextNomeDaAposta.setText(activity.getResources().getString(R.string.string_vazia));
		linearLayoutLayoutContentAlterarFavoritasSalvo.setVisibility(View.VISIBLE);
		relativeLayoutContentAlterarFavoritasEditar.setVisibility(View.GONE);
	}
}
