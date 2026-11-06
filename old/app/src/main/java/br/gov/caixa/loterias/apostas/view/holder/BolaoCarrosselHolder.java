package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.BotaoFavoritar;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDePalpites;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

public class BolaoCarrosselHolder extends LoteriasHolder<CotasBolaoDTO> implements View.OnClickListener, OnViewAumentaDiminuiQuantidade {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView lotericaNome, lotericaCidade;
	private TextView textQtdDezenas;
	private TextView textQtdCotas;
	private TextView valorCota;
	private TextView qtdCotas;
	private Button btnDetalhes;
	private Button btnDiminui;
	private Button btnAumenta;
	private Button btnAddCarrinho;
	private ConstraintLayout cabecalho, cabecalhoFiltrado;
	private View linhaNumero;
	private FrameLayout circuloFavoritar;
	private EstiloModalidadeMKP estilo;
	private BotaoFavoritar botaoFavoritar;
	private OnItemCarrosselBolaoListener listener;
	private FiltroAplicadoMarketplace filtro;
	private TextView modalidade;
	private TextView valorPremio;
	private TextView valorPremioPorExtenso;
	private TextView concurso;
	private TextView loterica;
	private RelativeLayout trapezio;
	private ImageView icTrevoBolao, headerEspeciais;
	private LinearLayout layoutLoerica, layoutPremio;
    private Boolean isMega30 = false;
    private Boolean isLotecaPais = false;



	public BolaoCarrosselHolder(Activity activity, View itemView, OnItemCarrosselBolaoListener listener, FiltroAplicadoMarketplace filtro) {
		super(itemView);
		this.activity 	= activity;
		this.filtro = filtro;

		cabecalho		  = itemView.findViewById(R.id.cabecalho_bolao);
		cabecalhoFiltrado = itemView.findViewById(R.id.cabecalho_bolao_filtrado);

		layoutLoerica = itemView.findViewById(R.id.layoutLoterica);
		layoutPremio = itemView.findViewById(R.id.layoutPremio);

		modalidade = itemView.findViewById(R.id.textViewCardBolaoTitle);
		valorPremio = itemView.findViewById(R.id.valor_estimado_premio_card_bolao);
		valorPremioPorExtenso = itemView.findViewById(R.id.valor_estimado_premio_card_bolao_por_extenso);
		concurso 		= itemView.findViewById(R.id.descricao_numero_concurso);
		loterica 		= itemView.findViewById(R.id.loterica_cidade_uf);
		trapezio		= itemView.findViewById(R.id.layout_trevo);
		headerEspeciais = itemView.findViewById(R.id.header_especiais_bolao);
		icTrevoBolao	= itemView.findViewById(R.id.trevoImagem);

		lotericaNome	= itemView.findViewById(R.id.loterica_nome);
		lotericaCidade = itemView.findViewById(R.id.loterica_uf);
		evitarCortarTexto(lotericaNome, 18, 15);
		evitarCortarTexto(lotericaCidade, 18, 15);
		circuloFavoritar = itemView.findViewById(R.id.layoutFavoritarCabecalho);
		botaoFavoritar = itemView.findViewById(R.id.btn_favoritar_mkp);
		textQtdDezenas	= itemView.findViewById(R.id.descricao_qtd_dezenas);
		textQtdCotas	= itemView.findViewById(R.id.descricao_qtd_cotas_disponiveis);
		valorCota 		= itemView.findViewById(R.id.valor_cota);
		btnDetalhes 	= itemView.findViewById(R.id.detalhes);
		btnDiminui 		= itemView.findViewById(R.id.diminui_cota);
		btnAumenta 		= itemView.findViewById(R.id.aumenta_cota);
		qtdCotas 		= itemView.findViewById(R.id.quantidade_cotas);
		btnAddCarrinho 	= itemView.findViewById(R.id.btn_add_carrinho);
		linhaNumero		= itemView.findViewById(R.id.linha_numero);

		this.listener = listener;
		btnDetalhes.setOnClickListener(this);
		btnDiminui.setOnClickListener(this);
		btnAumenta.setOnClickListener(this);
		btnAddCarrinho.setOnClickListener(this);
	}

	@Override
	public void bind(CotasBolaoDTO bolao, int position) {
        isMega30 = EspecialUtils.isMega30(bolao.getModalidade(), bolao.getConcurso(), bolao.getTipoConcurso().toString().equals(TipoConcursoEnum.ESPECIAL.getValor()));
        isLotecaPais = EspecialUtils.isLotecaPais(bolao.getModalidade(), bolao.getConcurso(), bolao.getTipoConcurso().toString().equals(TipoConcursoEnum.ESPECIAL.getValor()));
		//estilo = new EstiloModalidadeMKP(bolao.getModalidade(), isMega30);
		estilo = EstiloModalidadeMKP.createModalidadeConcursoEsp(bolao.getModalidade(),bolao.getConcurso(), bolao.getTipoConcurso().toString().equals(TipoConcursoEnum.ESPECIAL.getValor()));
		aplicaEstilo(bolao);
		preencheConteudo(bolao);
		atualizaQuantidadeTextView(bolao.getQtdCotasBolao(), bolao.getQtdCotaDisponivel());
		btnAddCarrinho.setText(FonteUtils.textCaixaSTDBold(activity,activity.getResources().getString(R.string.adicionarAoCarrinho)));

		botaoFavoritar.setOnStateChangeListener(isFavoritado -> {
			if(listener != null){
				listener.onFavoritarClick(position);
			}
		});
	}

	private void aplicaEstilo(CotasBolaoDTO bolao) {
		int corClara = ContextCompat.getColor(activity, estilo.getCorClara());
		int corEscura = ContextCompat.getColor(activity, estilo.getCorEscura());
		int corLetraLista = ContextCompat.getColor(activity, estilo.getCorLetraLista());
		int corFonteFundoClaro = ContextCompat.getColor(activity, estilo.getCorFonteFundoClaro());
		int corFonteFundoEscuro = ContextCompat.getColor(activity, estilo.getCorFonteFundoEscuro());

		if (isFiltradoLoterica()) {
			if (isEspecial(bolao.getTipoConcurso())) {
				trapezio.setVisibility(View.INVISIBLE);
				icTrevoBolao.setVisibility(View.GONE);
				headerEspeciais.setVisibility(View.VISIBLE);
				headerEspeciais.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), estilo.getImagemEspecialCarrossel()));
				if (isLotecaPais) {
					headerEspeciais.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),R.drawable.cabecalho_loteca_pais_carrossel));
				}
			} else {
				cabecalhoFiltrado.setBackgroundColor(corClara);
				trapezio.setBackground(ContextCompat.getDrawable(activity, estilo.getTrapezio()));
				icTrevoBolao.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrevoFundoEscuro()));
			}
			modalidade.setTextColor(corFonteFundoClaro);
			valorPremio.setTextColor(corLetraLista);
			valorPremioPorExtenso.setTextColor(corLetraLista);
			concurso.setTextColor(corLetraLista);
		} else {
			if (EspecialUtils.isParametrosOutubroRosa() && bolao.getModalidade() == ModalidadeEnum.MEGA_SENA){
				cabecalho.setBackground(VectorUtils.getShape(R.drawable.rounded_card_cabecalho_bolao, estilo.getCorEscura()));
			} else {
				cabecalho.setBackground(VectorUtils.getShape(R.drawable.rounded_card_cabecalho_bolao, estilo.getCorClara()));
			}
		}

		btnDetalhes.setTextColor(corLetraLista);
		btnDetalhes.setBackground(ContextCompat.getDrawable(activity, estilo.getShapeBtnDetalhes()));
		btnDiminui.setBackgroundColor(corEscura);
		btnDiminui.setTextColor(corFonteFundoEscuro);
		btnAumenta.setBackgroundColor(corEscura);
		btnAumenta.setTextColor(corFonteFundoEscuro);
		btnAddCarrinho.setBackground(VectorUtils.getShape(R.drawable.rounded_card, estilo.getCorEscura()));
		btnAddCarrinho.setTextColor(corFonteFundoEscuro);
		linhaNumero.setBackgroundColor(corEscura);
		textQtdCotas.setTextColor(ContextCompat.getColor(activity, R.color.cinza90));
		textQtdDezenas.setTextColor(ContextCompat.getColor(activity, R.color.cinza90));

		preencheCorCirculoFavoritar(corClara);

	}

	private void preencheConteudo(CotasBolaoDTO bolao) {

		String cidadeUf = StringUtils.capitalizerNovo(bolao.getMunicipio().getNome()) + " - " + bolao.getUf().getSigla();

		if (isFiltradoLoterica()) {
			cabecalho.setVisibility(View.GONE);
			cabecalhoFiltrado.setVisibility(View.VISIBLE);
			lotericaNome.setVisibility(View.GONE);
			lotericaCidade.setVisibility(View.GONE);
			layoutLoerica.setVisibility(View.GONE);
			layoutPremio.setVisibility(View.VISIBLE);

			if (isEspecial(bolao.getTipoConcurso())){
				//modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
				if (bolao.getModalidade() == ModalidadeEnum.LOTECA) {
					if (isLotecaPais) {
						//modalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_LINHA, ""));
						modalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS, ""));
					} else {
						modalidade.setText(ModalidadeEnum.getDescricaoEspecial(bolao.getModalidade()).toLowerCase());
					//TextViewUtils.mudarTamanhoPorPorcentagem(modalidade, -20f);
					}
				}else {
					modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
				}
			} else {
				modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()).toLowerCase());
			}

			valorPremio.setText(ViewUtils.getMoedaFormatComCentavos(bolao.getVrPremioEstimado(), 2));
			String valorEstimadoPorExtenso = ViewUtils.getMoedaFormatPorExtenso(bolao.getVrPremioEstimado());
			if (!valorEstimadoPorExtenso.isEmpty()){
				valorPremioPorExtenso.setText(String.format("(%s)",valorEstimadoPorExtenso));
				valorPremioPorExtenso.setVisibility(View.VISIBLE);
			} else {
				valorPremioPorExtenso.setVisibility(View.GONE);
			}
			concurso.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(),
					itemView.getContext().getString(R.string.label_premio_estimado_concurso_numero, bolao.getConcurso())));

		} else {
			cabecalho.setVisibility(View.VISIBLE);
			cabecalhoFiltrado.setVisibility(View.GONE);
			lotericaNome.setText(StringUtils.capitalizerNovo(bolao.getNomeFantasia()));
			ViewCompat.setAccessibilityHeading(lotericaNome,true);
			lotericaCidade.setText(cidadeUf);
		}

		botaoFavoritar.setState(bolao.isLotericaFavorita());

		textQtdDezenas.setText(ViewUtils.infoPrognosticosMkp(itemView.getContext(),
				bolao.getModalidade().equals(ModalidadeEnum.LOTECA) ?
						getApostasDePalpites(bolao.getQtdApostas(), bolao.getQtdNumeros()) :
						getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros()), 18, 16));

		textQtdCotas.setText(ViewUtils.infoPrognosticosMkp(itemView.getContext(),
								getCotaDeTotal(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal()), 18, 18));

		atualizaValorTotal(bolao);
	}

	private boolean isFiltradoLoterica() {
		return (filtro != null && filtro.getLotericaDTO() != null && filtro.getLotericaDTO().getCodigo() != null);
	}

	@NonNull
	private static String getMoedaFormat(CotasBolaoDTO bolao) {
		Double qtdApostas = Double.valueOf(bolao.getQtdCotasBolao() - 1);
		BigDecimal valor = bolao.getVrUltimaCotaComTarifa().add(BigDecimal.valueOf(bolao.getVrCotaComTarifa().doubleValue() * qtdApostas));

		return ViewUtils.getMoedaFormat(valor);
	}

	private void preencheCabecalho(CotasBolaoDTO bolao) {
		if (isEspecial(bolao.getTipoConcurso())){
			headerEspeciais.setVisibility(View.VISIBLE);
			trapezio.setVisibility(View.INVISIBLE);
			icTrevoBolao.setVisibility(View.INVISIBLE);
			if (estilo.getImagemEspecialCarrossel() > 0){
				this.headerEspeciais.setBackground(itemView.getContext().getDrawable(estilo.getImagemEspecialCarrossel()));
			}
			if (bolao.getModalidade() == ModalidadeEnum.LOTOFACIL){
				modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
			} else {
				if(isMega30){
					modalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS,""));
				} else if (isLotecaPais) {
					modalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.LOTECA_PAIS_DUAS_LINHAS,""));
				} else {
					modalidade.setText(ModalidadeEnum.getDescricaoEspecial(bolao.getModalidade()).toLowerCase());
				}
			}
		} else {
			modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()).toLowerCase());
			headerEspeciais.setVisibility(View.GONE);
			trapezio.setVisibility(View.VISIBLE);
			icTrevoBolao.setVisibility(View.VISIBLE);
		}
	}

	private boolean isEspecial(int tipoConcurso){
		return tipoConcurso == 2;
	}

	public void atualizaValorTotal(CotasBolaoDTO bolao){
		valorCota.setText(getMoedaFormat(bolao));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.detalhes:
				listener.detalhes(getAbsoluteAdapterPosition());
				break;
			case R.id.diminui_cota:
				listener.diminui(this, getAbsoluteAdapterPosition());
				break;
			case R.id.aumenta_cota:
				listener.aumenta(this, getAbsoluteAdapterPosition());
				break;
			case R.id.btn_add_carrinho:
				listener.adicionarCarrinho(getAbsoluteAdapterPosition());
				break ;
		}
	}

	@Override
	public void atualizaQuantidadeTextView(int quantidade, int maxPermitido) {
		qtdCotas.setText(Integer.toString(quantidade));
		atualizaBotoesQtdCotas(quantidade, maxPermitido);
	}

	@Override
	public void atualizaBotoesQtdCotas(int quantidade, int maxPermitido) {
		if (quantidade == QUANTIDADE_MINIMA){
			btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, R.color.cinza_desativado_mkp));
			btnDiminui.setEnabled(false);
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnAumenta.setEnabled(true);
		} else if (quantidade == maxPermitido){
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, R.color.cinza_desativado_mkp));
			btnAumenta.setEnabled(false);
			btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnDiminui.setEnabled(true);
		} else {
			btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnDiminui.setEnabled(true);
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnAumenta.setEnabled(true);
		}
	}

	private ColorStateList getCor(int cor) {
		return ContextCompat.getColorStateList(activity, cor);
	}

	private void evitarCortarTexto(TextView tv, int maxSp, int minSp) {
		// 2 linhas + reticências
		tv.setMaxLines(2);
		tv.setEllipsize(TextUtils.TruncateAt.END);

		// AutoSize compatível via AndroidX (API 14+)
		TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
				tv,
				minSp,  // min text size (sp)
				maxSp,  // max text size (sp)
				1,      // step granularity (sp)
				TypedValue.COMPLEX_UNIT_SP
		);
	}

	private void preencheCorCirculoFavoritar(int cor){
		if(circuloFavoritar != null){
			Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.shape_circulo).mutate();

			if(drawable instanceof GradientDrawable){
				GradientDrawable gradientDrawable = (GradientDrawable) drawable;

				int strokeWidth = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP,
						3,
						activity.getResources().getDisplayMetrics()
				);

				gradientDrawable.setStroke(strokeWidth, cor);
				circuloFavoritar.setBackground(gradientDrawable);
			}

		}
	}
}
