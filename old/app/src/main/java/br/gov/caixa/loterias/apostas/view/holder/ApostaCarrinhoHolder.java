package br.gov.caixa.loterias.apostas.view.holder;

import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IndicadorSurpresinha;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.TimerSingleton;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.DateUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ListaUtils;
import br.gov.caixa.loterias.apostas.utils.SwipeLayout;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.VolanteUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaLotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaLotogolAdapter;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoSwipeList;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.fragment.NumerosSuperSeteFragment;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;
import br.gov.caixa.loterias.apostas.view.listener.ApostaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.BolaoCarrinhoListener;

public class ApostaCarrinhoHolder extends LoteriasHolder<IdentificaoDeUmaApostaDas8Modalidades> {
	private TextView textViewTipoAposta;
	private TextView numerosAposta;
	private TextView trevosTimeCoracaoMesSorte;
	private TextView valorAposta;
	private TextView textContadorBolao;
	private TextView textCotas;
	private TextView textQtdCotas;
	private ExpandableHeightRecyclerView recycleViewDetalheAposta;
	private RelativeLayout containerFragment;
	private RelativeLayout layoutCabecalhoLoteca;
	private LinearLayout dadosApostaCarrinho;
	private LinearLayout lnrLegendaApostaCarrinho;
	private ImageButton iconLegenda;
	private View vwLinhaAzulLoteca;
	private LinearLayout dadosCarrinho;
	private TextView textViewNumeroConcurso, tvNumeroConcursoNovo;
	private ImageView iconTimer, imageDetalheBoloes;


	private Context context;
	private AppCompatActivity activity;
	private boolean animacao, isBolao;
	private ApostaCarrinhoListener listener;
	private BolaoCarrinhoListener listenerBolao;

	private TipoApostaLinhaView tipoApostaLinhaView;

	public ApostaCarrinhoHolder(View view, AppCompatActivity activity, boolean animacao, boolean isBolao, ApostaCarrinhoListener listener, BolaoCarrinhoListener listenerBolao) {
		super(view);

		this.animacao = animacao;
		this.isBolao = isBolao;

		this.context = view.getContext();
		this.activity = activity;
		this.listener = listener;
		this.listenerBolao = listenerBolao;

		textViewTipoAposta = view.findViewById(R.id.tipoAposta);
		numerosAposta = view.findViewById(R.id.numerosText);
		trevosTimeCoracaoMesSorte = view.findViewById(R.id.trevosTimeCoracaoMesSorte);
		valorAposta = view.findViewById(R.id.valorAposta);
		recycleViewDetalheAposta = view.findViewById(R.id.recycleViewDetalheAposta);
		layoutCabecalhoLoteca = view.findViewById(R.id.layoutCabecalhoLoteca);
		lnrLegendaApostaCarrinho = view.findViewById(R.id.lnrLegendaApostaCarrinho);
		iconLegenda = view.findViewById(R.id.iconLegenda);
		vwLinhaAzulLoteca = view.findViewById(R.id.vwLinhaAzulLoteca);
		dadosApostaCarrinho = view.findViewById(R.id.dadosApostaCarrinho);
		dadosCarrinho = view.findViewById(R.id.dadosCarrinho);
		textViewNumeroConcurso = view.findViewById(R.id.numeroConcurso);
		tvNumeroConcursoNovo = view.findViewById(R.id.tv_numero_concurso_novo);
		containerFragment = view.findViewById(R.id.rl_container_fragment);
		textContadorBolao = view.findViewById(R.id.contadorBolao);
		imageDetalheBoloes = view.findViewById(R.id.imageViewDetalheBoloes);
		iconTimer = view.findViewById(R.id.imageViewRelogioBoloes);
		textCotas = view.findViewById(R.id.textCotas);
		textQtdCotas = view.findViewById(R.id.textQtdCotas);
		tipoApostaLinhaView = view.findViewById(R.id.tipo_aposta_linha);
	}

	@Override
	public void bind(IdentificaoDeUmaApostaDas8Modalidades aposta, int position) {
		EstiloModalidadeMKP estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade());
		if (EspecialUtils.isParametrosOutubroRosa() && aposta.getModalidade() == ModalidadeEnum.MEGA_SENA){
			estiloMKP.alteraOutubroRosaPorTela();
		}

		if (position >= 4) {
			animacao = false;
		}

		final SwipeLayout swipeLayout = (SwipeLayout) this.itemView;
		swipeLayout.setClickToClose(true);
		swipeLayout.getDragEdgeMap().clear();
		swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.dadosApostaCarrinho));
		swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

		this.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
		this.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
		this.valorAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
		this.textViewTipoAposta.setText(ViewUtils.getNomeModalidadePorApostaDuasLinas(aposta).toLowerCase());
		if (aposta.getModalidade() == ModalidadeEnum.LOTOFACIL && aposta.getTipoConcurso().getDescricao().equalsIgnoreCase("ESPECIAL")){
			TextViewUtils.mudarTamanhoPorPorcentagem(this.textViewTipoAposta, -10f);
		}
		this.textViewNumeroConcurso.setText(itemView.getContext().getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
		this.textViewNumeroConcurso.setContentDescription("Concurso " + ViewUtils.getConcursoAtualPorAposta(aposta));
		this.dadosApostaCarrinho.setBackgroundResource(estiloMKP.getCorEscura());

		if (isBolao) {
			this.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
			this.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
			this.valorAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));

			swipeLayout.setSwipeEnabled(false);
			iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.bolao_barra_titulo));
			textQtdCotas.setText(String.format("%02d/%02d",
					aposta.getReservaCotaBolao().getNumeroCotaReservada(),
					aposta.getReservaCotaBolao().getQtdCotaTotalBolao()));
			String dataHoraExpiracao = aposta.getReservaCotaBolao().getDataHoraExpiracaoReserva();
			if (dataHoraExpiracao == null || dataHoraExpiracao.length() == 0) {
				//TODO - Silce deveria tratar
				dataHoraExpiracao = "2023-01-01T00:00:00-03:00";
			}
			startTimer(dataHoraExpiracao);
		}


		TextView labelQuantidadeApostadas = this.dadosApostaCarrinho.findViewById(R.id.labelQuantidadeApostadas);
		TextView labelQuantidadeSorteios = this.dadosApostaCarrinho.findViewById(R.id.labelQuantidadeSorteios);
		TextView     quantidadeApostadas = this.dadosApostaCarrinho.findViewById(R.id.quantidadeApostadas);
		TextView     quantidadeSorteios  = this.dadosApostaCarrinho.findViewById(R.id.quantidadeSorteios);
		//Button       buttonApostaEspelho = this.dadosApostaCarrinho.findViewById(R.id.buttonApostaEspelho);
		LinearLayout layoutTimeCoracao   = this.dadosApostaCarrinho.findViewById(R.id.layoutTimeCoracao);
		TextView     labelTimeCoracao    = this.dadosApostaCarrinho.findViewById(R.id.labelTimeCoracaoTxt);
		TextView     textViewTimeCoracao = this.dadosApostaCarrinho.findViewById(R.id.textViewTimeCoracao);
		ImageButton excluirApostaCarrinho = this.itemView.findViewById(R.id.excluirApostaCarrinho);

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

		labelQuantidadeApostadas.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorFonteFundoEscuro()));
		labelQuantidadeSorteios.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorFonteFundoEscuro()));

		quantidadeApostadas.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorFonteFundoEscuro()));
		quantidadeApostadas.setText(String.format(Locale.getDefault(), context.getResources().getString(R.string.percent_d_percent_s), quantidadeNumeros, quantidadeApostadasString));
		quantidadeSorteios.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorFonteFundoEscuro()));
		quantidadeSorteios.setText(String.format(Locale.getDefault(), context.getResources().getString(R.string.percent_d_percent_s), quantidadeTeimosinhas, quantidadeSorteiosString));

//		if (aposta.getEspelho() != null && aposta.getEspelho()) {
//			buttonApostaEspelho.setVisibility(View.VISIBLE);
//		}

		excluirApostaCarrinho.setOnClickListener(view -> listener.onDeletaAposta(aposta));
		excluirApostaCarrinho.setContentDescription("Excluir");

		if (isBolao) {
			imageDetalheBoloes.setOnClickListener(view -> listenerBolao.onCotaClick(aposta));
		}

		if (aposta.getPartidasLoteca() != null && !aposta.getPartidasLoteca().isEmpty()) {
			DetalheApostaLotecaAdapter detalheApostaLotecaAdapter = new DetalheApostaLotecaAdapter(aposta.getPartidasLoteca());
			this.recycleViewDetalheAposta.setLayoutManager(new LinearLayoutManager(context));
			ViewGroup.MarginLayoutParams params =
					(ViewGroup.MarginLayoutParams)
							this.recycleViewDetalheAposta.getLayoutParams();

			int marginTop = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					24,
					context.getResources().getDisplayMetrics());

			params.topMargin = marginTop;

			this.recycleViewDetalheAposta.setLayoutParams(params);
			this.recycleViewDetalheAposta.setAdapter(detalheApostaLotecaAdapter);
			this.recycleViewDetalheAposta.setExpanded(true);
			animacao = Boolean.FALSE;
			swipeLayout.setSwipeEnabled(animacao);

			this.layoutCabecalhoLoteca.setVisibility(VISIBLE);
			excluirApostaCarrinho.setBackground(null);
			excluirApostaCarrinho.setImageResource(R.drawable.ic_lixeira_nova_mkp);
			excluirApostaCarrinho.setColorFilter(ContextCompat.getColor(context, estiloMKP.getCorEscura()));
			this.lnrLegendaApostaCarrinho.setVisibility(VISIBLE);
			this.vwLinhaAzulLoteca.setVisibility(VISIBLE);
			this.iconLegenda.setOnClickListener(view -> {
				Dialog dialog = DialogUtils.buildCustomDialog(
						context,
						R.layout.dialog_legenda_loteca_novo,
						true,
						R.id.btnFechar,
						d -> d.dismiss()
				);

				if (dialog != null) {
					dialog.show();
				}
            });
		} else if (aposta.getPartidasLotogol() != null && !aposta.getPartidasLotogol().isEmpty()) {
			DetalheApostaLotogolAdapter detalheApostaLotogolAdapter = new DetalheApostaLotogolAdapter(aposta.getPartidasLotogol());
			this.recycleViewDetalheAposta.setLayoutManager(new LinearLayoutManager(context));
			this.recycleViewDetalheAposta.setAdapter(detalheApostaLotogolAdapter);
			this.recycleViewDetalheAposta.setExpanded(true);

			this.layoutCabecalhoLoteca.setVisibility(VISIBLE);
			this.layoutCabecalhoLoteca.getLayoutParams().height = 0;
			RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			relativeLayoutParams.addRule(RelativeLayout.START_OF, R.id.valorAposta);
			relativeLayoutParams.addRule(RelativeLayout.BELOW, R.id.layoutCabecalhoLoteca);
			relativeLayoutParams.addRule(RelativeLayout.ALIGN_START, R.id.layoutCabecalhoLoteca);
			this.recycleViewDetalheAposta.setLayoutParams(relativeLayoutParams);
			animacao = Boolean.FALSE;
			swipeLayout.setSwipeEnabled(animacao);
		}

	if(ModalidadeEnum.MAIS_MILIONARIA == aposta.getModalidade()){
			layoutTimeCoracao.setVisibility(VISIBLE);

			int quantidade = 0;
			if (aposta.getTrevosSelecionados() != null && !((List<Integer>)aposta.getTrevosSelecionados()).isEmpty()){
				List<Integer> trevos = (List<Integer>) aposta.getTrevosSelecionados();
				quantidade = trevos.size();
			} else if (aposta.getQuantidadeTrevos() != null){
				quantidade = aposta.getQuantidadeTrevos();
			}
			String                 qtd     = "_"+quantidade+"_";
			SpannableStringBuilder qtdBold = ViewUtils.textCaixaSTDBold(context, qtd);
			SpannableStringBuilder trevosBold = ViewUtils.textCaixaSTDBold(context, context.getResources().getString(R.string.underline_trevos_underline));

			labelTimeCoracao.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorFonteFundoEscuro()));
			labelTimeCoracao.setText(qtdBold);
			textViewTimeCoracao.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorFonteFundoEscuro()));
			textViewTimeCoracao.setText(" " + trevosBold);
		}

		StringBuilder numeroStr = new StringBuilder();
		StringBuilder trevosStr = new StringBuilder();
		if (aposta != null && aposta.getIndicadorSurpresinha() != null && aposta.getIndicadorSurpresinha().getValor() != null) {
			switch (aposta.getIndicadorSurpresinha().getValor()) {
				case IndicadorSurpresinha.NAO_SURPRESINHA:
					if (aposta.getModalidade() == ModalidadeEnum.SUPER_7) {
						if (!isBolao){
							configuraFragment(aposta, this);
							//this.tvNumeroConcursoNovo.setVisibility(View.VISIBLE);
							//this.textViewNumeroConcurso.setVisibility(View.GONE);
							this.tvNumeroConcursoNovo.setVisibility(View.GONE);
							this.textViewNumeroConcurso.setVisibility(VISIBLE);
						}

						//this.tvNumeroConcursoNovo.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
						//this.tvNumeroConcursoNovo.setText(context.getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
						this.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
						this.textViewNumeroConcurso.setText(context.getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
						this.textViewNumeroConcurso.setContentDescription("Concurso " + ViewUtils.getConcursoAtualPorAposta(aposta));
						this.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
						this.valorAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
					} else {
						containerFragment.setVisibility(View.GONE);
						IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> apostaNumerica = aposta;
						if (apostaNumerica.getListaNumerosSelecionados() != null) {
							for (Integer num : apostaNumerica.getListaNumerosSelecionados()) {
								if (isUltimoNumero(num, apostaNumerica)) {
									numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d),
																   VolanteUtils.getLabelPrognostico(num, aposta.getModalidade())));
								} else {
									numeroStr.append(String.format(context.getResources().getString(R.string.percent_zero_dois_d_traco),
																   VolanteUtils.getLabelPrognostico(num, aposta.getModalidade())));
								}
							}
						}

						if (apostaNumerica.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && apostaNumerica.getTrevosSelecionados() != null){
							trevosStr.append(context.getResources().getString(R.string.trevos_dois_pontos));
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
					this.tvNumeroConcursoNovo.setText(context.getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
					this.tvNumeroConcursoNovo.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
					this.textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
					this.textViewTipoAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));
					this.valorAposta.setTextColor(ContextCompat.getColor(context, estiloMKP.getCorLetraLista()));

					for (int i = 0; i < aposta.getQuantidadeNumeros(); i++) {
						if (i == aposta.getQuantidadeNumeros() - 1) {
							numeroStr.append(context.getResources().getString(R.string.x_x));
						} else {
							numeroStr.append(context.getResources().getString(R.string.x_x_traco));
						}
					}

					if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA){
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

		if(aposta.getModalidade() == ModalidadeEnum.DIA_DE_SORTE && !aposta.getIndicadorCotaBolao()){
			String mesDeSorte = aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNome() != null ? aposta.getMesDeSorte().getNome() : context.getString(R.string.label_tres_interrogacoes);
			String label = context.getString(R.string.label_dia_sorte_dois_pontos) + mesDeSorte;

			if (aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA) {
				label = context.getString(R.string.label_dia_sorte_dois_pontos) +	context.getResources().getString(R.string.tres_interrogacoes);
			}
			this.trevosTimeCoracaoMesSorte.setText(label);
		}else if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA && !aposta.getIndicadorCotaBolao()){
			String timeCoracao = "-";
			if (aposta.getTimeDoCoracao() != null && aposta.getTimeDoCoracao().getNome() != null){
				timeCoracao = aposta.getTimeDoCoracao().getNome();
			}
			if (aposta.getTimeDoCoracao() != null && aposta.getTimeDoCoracao().getUf() != null){
				timeCoracao = timeCoracao + "/" + aposta.getTimeDoCoracao().getUf();
			}
			String label = context.getResources().getString(R.string.label_time_coracao_dois_pontos) + "\n" + timeCoracao;
			if (aposta.getIndicadorSurpresinha().getValor() == IndicadorSurpresinha.SURPRESINHA) {
				label = context.getResources().getString(R.string.label_time_coracao_dois_pontos)	+ context.getResources().getString(R.string.tres_interrogacoes);
			}
			this.trevosTimeCoracaoMesSorte.setText(label);
		}else if (aposta.getModalidade() == ModalidadeEnum.MAIS_MILIONARIA && !aposta.getIndicadorCotaBolao()){
			this.trevosTimeCoracaoMesSorte.setText(removeUltimoTraco(trevosStr.toString() + "\n"));
		} else if (aposta.getIndicadorCotaBolao() != null && !aposta.getIndicadorCotaBolao()){
            this.trevosTimeCoracaoMesSorte.setVisibility(View.GONE);
		}

		if (isBolao) {
			ViewUtils.setMoedaFormatHtml(aposta.getReservaCotaBolao().getVrTotalCota(), this.valorAposta);
		} else {
			if (aposta.getPartidasLoteca() == null || aposta.getPartidasLoteca().isEmpty()) {
				ViewUtils.setMoedaFormatHtml(aposta.getValor(), this.valorAposta);
			}

			String numeros = numeroStr != null
					? removeUltimoTraco(numeroStr.toString())
					: "";

			numerosAposta.setText(numeros);

			numerosAposta.setContentDescription(
					numeros.isEmpty()
							? ""
							: numeros.replace("-", ",")
							.replace(" ", "")
							.replace(",",", ")
			);

		}

		if (animacao) {
			AnimacaoSwipeList.animacaoSwipeList(context, position, swipeLayout);
		}

		List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

		if (Boolean.TRUE.equals(aposta.getTroca())) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
		}
		if (isBolao) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.BOLAO);
		}
		if (Boolean.TRUE.equals(aposta.getCombo())) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
		}
		if (Boolean.TRUE.equals(aposta.getEspelho())) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
		}
		if (Boolean.TRUE.equals(aposta.getSurpresinha())) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
		}
		if (aposta.getQuantidadeTeimosinhas() > 0) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
		}

		if (listTipoApostaLinha.size() > 0) {
			tipoApostaLinhaView.setVisibility(VISIBLE);
			tipoApostaLinhaView.setupView(listTipoApostaLinha, R.color.bolao_linha_clara, R.color.cinzaescuro, estiloMKP.getCorLetraLista(), TipoApostaLinhaView.TriangleDirection.UP);
		}
	}

	private void preencheNumeroConcurso(IdentificaoDeUmaApostaDas8Modalidades aposta, TextView textView) {
		this.textViewNumeroConcurso.setText(itemView.getContext().getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
		this.textViewNumeroConcurso.setContentDescription("Concurso " + ViewUtils.getConcursoAtualPorAposta(aposta));

		if (aposta.getIndicadorCotaBolao() != null && aposta.getIndicadorCotaBolao()){
			textView.setText(context.getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorApostaBolao(aposta)));
		} else {
			textView.setText(context.getString(R.string.label_aposta_concurso, ViewUtils.getConcursoAtualPorAposta(aposta)));
		}
	}

	private boolean isUltimoNumero(Integer num, IdentificaoDeUmaApostaDas8Modalidades<List<Integer>> apostaNumerica){
		return num.equals(apostaNumerica.getListaNumerosSelecionados().get(apostaNumerica.getListaNumerosSelecionados().size() - 1));
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


	private void startTimer(String dataHoraExpiracaoReserva) {
		long tempo_inicial_milesec = DateUtils.diffMillisSecondsTimerZone(dataHoraExpiracaoReserva);

		ContagemRegressiva contDown = new ContagemRegressiva(textContadorBolao, tempo_inicial_milesec, 1000) {
			@Override
			public void onFinish() {
				try {
					textViewTipoAposta.setTextColor(ContextCompat.getColor(context, R.color.cor_bola_unselected));
					textViewNumeroConcurso.setTextColor(ContextCompat.getColor(context, R.color.cor_bola_unselected));
					imageDetalheBoloes.setBackground(VectorUtils.getShape(R.drawable.ic_trevo_carrinho_bolao, R.color.cor_bola_unselected));
					textQtdCotas.setTextColor(ContextCompat.getColor(context, R.color.cor_bola_unselected));
					valorAposta.setTextColor(ContextCompat.getColor(context, R.color.cor_bola_unselected));
					textContadorBolao.setTextColor(ContextCompat.getColor(context, R.color.cor_bola_unselected));
					textContadorBolao.setText(R.string.tempo_zerado);
					iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.cor_bola_unselected));
					textCotas.setText(R.string.cota_expirada);
					textCotas.setTextColor(ContextCompat.getColor(context, R.color.vermelho_cancelada));
				} catch (Exception e){}
			}
		};

		contDown.setListener(onContagemListener(contDown));
		contDown.start();
		TimerSingleton.getInstance().setContagemRegressiva(contDown);
	}

	@NonNull
	private ContagemRegressiva.ContagemRegressivaListener onContagemListener(ContagemRegressiva contDown) {
		return millisUntilFinish -> {
			if (contDown.estaFaltandoXMin(millisUntilFinish, 1)){
				textContadorBolao.setTextColor(ContextCompat.getColor(context, R.color.jr_amarelo));
				iconTimer.setBackground(VectorUtils.getShape(R.drawable.ic_timer, R.color.jr_amarelo));
				textCotas.setTextColor(ContextCompat.getColor(context, R.color.jr_amarelo));
			}
		};
	}


}
