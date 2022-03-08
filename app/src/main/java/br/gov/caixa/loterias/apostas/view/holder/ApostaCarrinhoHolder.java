package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.swipe.SwipeLayout;

import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaLotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaLotogolAdapter;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoSwipeList;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.listener.ApostaCarrinhoListener;

public class ApostaCarrinhoHolder extends LoteriasHolder<IdentificaoDeUmaApostaDas8Modalidades> {
	private TextView textViewTipoAposta;
	private TextView numerosAposta;
	private TextView trevosAposta;
	private TextView valorAposta;
	private ExpandableHeightRecyclerView recycleViewDetalheAposta;
	private RelativeLayout containerFragment;
	private RelativeLayout layoutCabecalhoLoteca;
	private LinearLayout dadosApostaCarrinho;
	private LinearLayout dadosCarrinho;
	private TextView textViewNumeroConcurso, tvNumeroConcursoNovo;


	private Context context;
	private AppCompatActivity activity;
	private boolean animacao;
	private ApostaCarrinhoListener listener;


	public ApostaCarrinhoHolder(View view, AppCompatActivity activity, boolean animacao, ApostaCarrinhoListener listener) {
		super(view);

		this.animacao = animacao;
		this.context = view.getContext();
		this.activity = activity;
		this.listener = listener;

		textViewTipoAposta = view.findViewById(R.id.tipoAposta);
		numerosAposta = view.findViewById(R.id.numerosText);
		trevosAposta = view.findViewById(R.id.trevos);
		valorAposta = view.findViewById(R.id.valorAposta);
		recycleViewDetalheAposta = view.findViewById(R.id.recycleViewDetalheAposta);
		layoutCabecalhoLoteca = view.findViewById(R.id.layoutCabecalhoLoteca);
		dadosApostaCarrinho = view.findViewById(R.id.dadosApostaCarrinho);
		dadosCarrinho = view.findViewById(R.id.dadosCarrinho);
		textViewNumeroConcurso = view.findViewById(R.id.numeroConcurso);
		tvNumeroConcursoNovo = view.findViewById(R.id.tv_numero_concurso_novo);
		containerFragment = view.findViewById(R.id.rl_container_fragment);
	}

	@Override
	public void bind(IdentificaoDeUmaApostaDas8Modalidades aposta, int position) {
		EstiloModalidade estilo = new EstiloModalidade(aposta.getModalidade());

		if (position >= 4) {
			animacao = false;
		}

		final SwipeLayout swipeLayout = (SwipeLayout) this.itemView;
		swipeLayout.setClickToClose(true);
		swipeLayout.getDragEdgeMap().clear();
		swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
		swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

		this.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
		this.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estilo.getCorClara()));
		this.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
		this.textViewTipoAposta.setText(ViewUtils.getNomeModalidadePorAposta(aposta));
		this.textViewNumeroConcurso.setText(itemView.getContext().getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
		this.dadosApostaCarrinho.setBackgroundResource(estilo.getCorEscura());

		TextView     quantidadeApostadas = this.dadosApostaCarrinho.findViewById(R.id.quantidadeApostadas);
		TextView     quantidadeSorteios  = this.dadosApostaCarrinho.findViewById(R.id.quantidadeSorteios);
		Button       buttonApostaEspelho = this.dadosApostaCarrinho.findViewById(R.id.buttonApostaEspelho);
		LinearLayout layoutTimeCoracao   = this.dadosApostaCarrinho.findViewById(R.id.layoutTimeCoracao);
		TextView     labelTimeCoracao    = this.dadosApostaCarrinho.findViewById(R.id.labelTimeCoracaoTxt);
		TextView     textViewTimeCoracao = this.dadosApostaCarrinho.findViewById(R.id.textViewTimeCoracao);


		String quantidadeApostadasString;
		String quantidadeSorteiosString;

		int quantidadeNumeros     = aposta.getQuantidadeNumeros() != null ? aposta.getQuantidadeNumeros() : 0;
		int quantidadeTeimosinhas = aposta.getQuantidadeTeimosinhas() == null || aposta.getQuantidadeTeimosinhas() == 0 ? 1 : aposta.getQuantidadeTeimosinhas();

		if (quantidadeNumeros > 1) {
			quantidadeApostadasString = context.getString(R.string.label_numeros);
		} else {
			quantidadeApostadasString = context.getString(R.string.label_numero);
		}

		if (quantidadeTeimosinhas > 1) {
			quantidadeSorteiosString = context.getString(R.string.label_sorteios);
		} else {
			quantidadeSorteiosString = context.getString(R.string.label_sorteio);
		}

		quantidadeApostadas.setText(String.format(Locale.getDefault(), context.getResources().getString(R.string.percent_d_percent_s), quantidadeNumeros, quantidadeApostadasString));
		quantidadeSorteios.setText(String.format(Locale.getDefault(), context.getResources().getString(R.string.percent_d_percent_s), quantidadeTeimosinhas, quantidadeSorteiosString));

		if (aposta.getEspelho()) {
			buttonApostaEspelho.setVisibility(View.VISIBLE);
		}

		this.itemView.findViewById(R.id.excluirApostaCarrinho).setOnClickListener(view -> listener.onDeletaAposta(aposta));

		if (aposta.getPartidasLoteca() != null && !aposta.getPartidasLoteca().isEmpty()) {
			DetalheApostaLotecaAdapter detalheApostaLotecaAdapter = new DetalheApostaLotecaAdapter(aposta.getPartidasLoteca());
			this.recycleViewDetalheAposta.setLayoutManager(new LinearLayoutManager(context));
			this.recycleViewDetalheAposta.setAdapter(detalheApostaLotecaAdapter);
			this.recycleViewDetalheAposta.setExpanded(true);
			animacao = Boolean.FALSE;
			swipeLayout.setSwipeEnabled(animacao);

			this.layoutCabecalhoLoteca.setVisibility(View.VISIBLE);
		} else if (aposta.getPartidasLotogol() != null && !aposta.getPartidasLotogol().isEmpty()) {
			DetalheApostaLotogolAdapter detalheApostaLotogolAdapter = new DetalheApostaLotogolAdapter(aposta.getPartidasLotogol());
			this.recycleViewDetalheAposta.setLayoutManager(new LinearLayoutManager(context));
			this.recycleViewDetalheAposta.setAdapter(detalheApostaLotogolAdapter);
			this.recycleViewDetalheAposta.setExpanded(true);

			this.layoutCabecalhoLoteca.setVisibility(View.VISIBLE);
			this.layoutCabecalhoLoteca.getLayoutParams().height = 0;
			RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			relativeLayoutParams.addRule(RelativeLayout.START_OF, R.id.valorAposta);
			relativeLayoutParams.addRule(RelativeLayout.BELOW, R.id.layoutCabecalhoLoteca);
			relativeLayoutParams.addRule(RelativeLayout.ALIGN_START, R.id.layoutCabecalhoLoteca);
			this.recycleViewDetalheAposta.setLayoutParams(relativeLayoutParams);
			animacao = Boolean.FALSE;
			swipeLayout.setSwipeEnabled(animacao);
		}

		if (ModalidadeEnum.TIMEMANIA == aposta.getModalidade()) {
			layoutTimeCoracao.setVisibility(View.VISIBLE);
			if (aposta != null && aposta.getIndicadorSurpresinha() != null && aposta.getIndicadorSurpresinha().getValor() != null) {
				textViewTimeCoracao.setText(aposta.getTimeDoCoracao().getNome());
				if (aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA) {
					textViewTimeCoracao.setText(context.getResources().getString(R.string.tres_interrogacoes));
				}
			}
		} else if (ModalidadeEnum.DIA_DE_SORTE == aposta.getModalidade()) {
			layoutTimeCoracao.setVisibility(View.VISIBLE);
			labelTimeCoracao.setText(R.string.label_mes_sorte);
			textViewTimeCoracao.setText(aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNome() != null ? aposta.getMesDeSorte().getNome() : context.getString(R.string.label_tres_interrogacoes));
		} else if(ModalidadeEnum.MAIS_MILIONARIA == aposta.getModalidade()){
			layoutTimeCoracao.setVisibility(View.VISIBLE);

			int quantidade = 0;
			if (aposta.getTrevosSelecionados() != null && !((List<Integer>)aposta.getTrevosSelecionados()).isEmpty()){
				List<Integer> trevos = (List<Integer>) aposta.getTrevosSelecionados();
				quantidade = trevos.size();
			} else {
				quantidade = aposta.getQuantidadeTrevos();
			}
			String                 qtd     = "_"+quantidade+"_";
			SpannableStringBuilder qtdBold = ViewUtils.textFuturaAndFuturaBold(context, qtd);
			SpannableStringBuilder trevosBold = ViewUtils.textFuturaAndFuturaBold(context, context.getResources().getString(R.string.underline_trevos_underline));

			labelTimeCoracao.setText(qtdBold);
			textViewTimeCoracao.setText(" " + trevosBold);
		}

		StringBuilder numeroStr = new StringBuilder();
		StringBuilder trevosStr = new StringBuilder();
		if (aposta != null && aposta.getIndicadorSurpresinha() != null && aposta.getIndicadorSurpresinha().getValor() != null) {
			switch (aposta.getIndicadorSurpresinha().getValor()) {
				case IndicadorSurpresinha.NAO_SURPRESINHA:
					if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
						configuraFragment(aposta, this);
						this.tvNumeroConcursoNovo.setVisibility(View.VISIBLE);
						this.textViewNumeroConcurso.setVisibility(View.GONE);
						this.tvNumeroConcursoNovo.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
						this.tvNumeroConcursoNovo.setText(context.getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
						this.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
						this.valorAposta.setTextColor(ContextCompat.getColor(context, estilo.getCorEscura()));
					} else {
						IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> apostaNumerica = aposta;
						if (apostaNumerica.getListaNumerosSelecionados() != null) {
							List<Integer> numerosSelecionados = ListaUtils.transformaListDoubleEmListInteger(aposta.getNumerosSelecionados());

							for(int num = 0; num < numerosSelecionados.size(); num++){
								if (num == numerosSelecionados.size() -1){
									numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d), numerosSelecionados.get(num)));
								} else {
									numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d_traco), numerosSelecionados.get(num)));
								}
							}
						}

						if (apostaNumerica.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && apostaNumerica.getTrevosSelecionados() != null){
							trevosStr.append("\n" + context.getResources().getString(R.string.trevos_dois_pontos));
							List<Integer> trevosSelecionados = ListaUtils.transformaListDoubleEmListInteger(aposta.getTrevosSelecionados());

							for (int num = 0; num < trevosSelecionados.size(); num++) {
								if (num == trevosSelecionados.size() - 1) {
									trevosStr.append(String.format(context.getResources().getString(R.string.percent_d), trevosSelecionados.get(num)));
								} else {
									trevosStr.append(String.format(context.getResources().getString(R.string.percent_d_traco), trevosSelecionados.get(num)));
								}
							}
						}
					}

					break;
				case IndicadorSurpresinha.SURPRESINHA:
				case IndicadorSurpresinha.SURPRESINHA_NUMERICA:
					for (int i = 0; i < aposta.getQuantidadeNumeros(); i++) {
						if (i == aposta.getQuantidadeNumeros() - 1) {
							numeroStr.append(context.getResources().getString(R.string.x_x));
						} else {
							numeroStr.append(context.getResources().getString(R.string.x_x_traco));
						}
					}

					if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
						trevosStr.append("\n");
						trevosStr.append(context.getResources().getString(R.string.trevos_dois_pontos));
						for (int i = 0; i < aposta.getQuantidadeTrevos(); i++){
							if (i == aposta.getQuantidadeTrevos() - 1) {
								trevosStr.append(context.getResources().getString(R.string.x_x));
							} else {
								trevosStr.append(context.getResources().getString(R.string.x_x_traco));
							}
						}
					}
					break;
				default:
					break;
			}
		}

		if (ModalidadeEnum.DIA_DE_SORTE == aposta.getModalidade() || aposta.getModalidade() == ModalidadeEnum.TIMEMANIA){
			numeroStr.append("\n\n");
		}

		this.numerosAposta.setText(removeUltimoTraco(numeroStr.toString()));
		if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
			this.trevosAposta.setText(removeUltimoTraco(trevosStr.toString() + "\n"));
		} else {
			this.trevosAposta.setVisibility(View.GONE);
		}
		ViewUtils.setMoedaFormatHtml(aposta.getValor(), this.valorAposta);

		if (animacao) {
			AnimacaoSwipeList.animacaoSwipeList(context, position, swipeLayout);
		}
	}

	private String removeUltimoTraco(String str) {
		if (str.endsWith("-")){
			str = str.substring(0, (str.length() - 1));
		} else if (str.endsWith("- ")){
			str = str.substring(0, (str.length() - 2));
		}

		return str;
	}

	private void configuraFragment(IdentificaoDeUmaApostaDas8Modalidades aposta, ApostaCarrinhoHolder holder) {
		FragmentManager fm          = activity.getSupportFragmentManager();
		int             containerId = holder.containerFragment.getId();
		Fragment        oldFragment = fm.findFragmentById(containerId);
		if (oldFragment != null) {
			fm.beginTransaction().remove(oldFragment).commit();
		}

		int newContainerId = (int) Utils.getIdUnico();
		holder.containerFragment.setId(newContainerId);

		NumerosSuperSeteFragment fragment = NumerosSuperSeteFragment.newInstance(aposta.getMatrizNumerosSelecionados());
		fm.beginTransaction().replace(newContainerId, fragment).commit();

	}
}
