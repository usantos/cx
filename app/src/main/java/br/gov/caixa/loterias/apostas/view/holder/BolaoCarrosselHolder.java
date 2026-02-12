package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.utils.EspecialUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.SharedPreferencesUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDeDezenas;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getApostasDePalpites;
import static br.gov.caixa.loterias.apostas.utils.StringUtils.getCotaDeTotal;

public class BolaoCarrosselHolder extends LoteriasHolder<CotasBolaoDTO> implements View.OnClickListener, OnViewAumentaDiminuiQuantidade {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView modalidade;
	private TextView valorPremio;
	private TextView concurso;
	private TextView loterica;
	private TextView textQtdDezenas;
	private TextView textQtdCotas;
	private TextView valorCota;
	private TextView qtdCotas;
	private Button btnDetalhes;
	private Button btnDiminui;
	private Button btnAumenta;
	private Button btnAddCarrinho;
	private RelativeLayout trapezio;
	private ConstraintLayout cabecalho;
	private View linhaNumero;
	private ImageView icTrevoBolao, headerEspeciais;

	private EstiloModalidadeMKP estilo;

	private OnItemCarrosselBolaoListener listener;
	private Boolean isMega30 = false;


	public BolaoCarrosselHolder(Activity activity, View itemView, OnItemCarrosselBolaoListener listener) {
		super(itemView);
		this.activity 	= activity;
		modalidade 		= itemView.findViewById(R.id.textViewCardBolaoTitle);
		valorPremio 	= itemView.findViewById(R.id.valor_estimado_premio);
		concurso 		= itemView.findViewById(R.id.descricao_numero_concurso);
		loterica 		= itemView.findViewById(R.id.loterica_cidade_uf);
		textQtdDezenas	= itemView.findViewById(R.id.descricao_qtd_dezenas);
		textQtdCotas	= itemView.findViewById(R.id.descricao_qtd_cotas_disponiveis);
		valorCota 		= itemView.findViewById(R.id.valor_cota);
		btnDetalhes 	= itemView.findViewById(R.id.detalhes);
		btnDiminui 		= itemView.findViewById(R.id.diminui_cota);
		btnAumenta 		= itemView.findViewById(R.id.aumenta_cota);
		qtdCotas 		= itemView.findViewById(R.id.quantidade_cotas);
		btnAddCarrinho 	= itemView.findViewById(R.id.btn_add_carrinho);
		trapezio		= itemView.findViewById(R.id.layout_trevo);
		headerEspeciais = itemView.findViewById(R.id.header_especiais_bolao);
		icTrevoBolao	= itemView.findViewById(R.id.trevoImagem);
		cabecalho		= itemView.findViewById(R.id.cabecalho_bolao);
		linhaNumero		= itemView.findViewById(R.id.linha_numero);

		this.listener = listener;
		btnDetalhes.setOnClickListener(this);
		btnDiminui.setOnClickListener(this);
		btnAumenta.setOnClickListener(this);
		btnAddCarrinho.setOnClickListener(this);
	}

	@Override
	public void bind(CotasBolaoDTO bolao, int position) {
		//TODO: MEGA 30 ANOS//
		isMega30 = EspecialUtils.isMega30(bolao.getConcurso(),bolao.getTipoConcurso());
		estilo = new EstiloModalidadeMKP(bolao.getModalidade(),isMega30);
		aplicaEstilo();
		preencheCabecalho(bolao);
		preencheConteudo(bolao);
		atualizaQuantidadeTextView(bolao.getQtdCotasBolao(), bolao.getQtdCotaDisponivel());
		btnAddCarrinho.setText(FonteUtils.textCaixaSTDBold(activity,activity.getResources().getString(R.string.adicionarAoCarrinho)));
	}

	private void aplicaEstilo() {
		int corClara = ContextCompat.getColor(activity, estilo.getCorClara());
		int corEscura = ContextCompat.getColor(activity, estilo.getCorEscura());
		int corLetraLista = ContextCompat.getColor(activity, estilo.getCorLetraLista());
		int corFonteFundoClaro = ContextCompat.getColor(activity, estilo.getCorFonteFundoClaro());
		int corFonteFundoEscuro = ContextCompat.getColor(activity, estilo.getCorFonteFundoEscuro());
		trapezio.setBackground(ContextCompat.getDrawable(activity, estilo.getTrapezio()));
		icTrevoBolao.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), estilo.getTrevoFundoEscuro()));
		cabecalho.setBackgroundColor(corClara);
		modalidade.setTextColor(corFonteFundoClaro);
		valorPremio.setTextColor(corLetraLista);
		btnDetalhes.setTextColor(corLetraLista);
		btnDetalhes.setBackground(ContextCompat.getDrawable(activity, estilo.getShapeBtnDetalhes()));
		btnDiminui.setBackgroundColor(corEscura);
		btnDiminui.setTextColor(corFonteFundoEscuro);
		btnAumenta.setBackgroundColor(corEscura);
		btnAumenta.setTextColor(corFonteFundoEscuro);
		btnAddCarrinho.setBackground(VectorUtils.getShape(R.drawable.rounded_card, estilo.getCorEscura()));
		btnAddCarrinho.setTextColor(corFonteFundoEscuro);
		linhaNumero.setBackgroundColor(corEscura);
	}

	private void preencheConteudo(CotasBolaoDTO bolao) {
		valorPremio.setText(ViewUtils.getMoedaFormatPorExtenso(bolao.getVrPremioEstimado()));
		concurso.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(),
														   itemView.getContext().getString(R.string.label_premio_estimado_concurso_numero_bold, bolao.getConcurso())));
		loterica.setText(bolao.getNomeFantasia()+ "\n" + bolao.getMunicipio().getNome() + " - " + bolao.getUf().getSigla());

		textQtdDezenas.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(),
				bolao.getModalidade().equals(ModalidadeEnum.LOTECA) ?
						getApostasDePalpites(bolao.getQtdApostas(), bolao.getQtdNumeros()) :
						getApostasDeDezenas(bolao.getQtdApostas(), bolao.getQtdNumeros())));

		textQtdCotas.setText(ViewUtils.textCaixaSTDBold(itemView.getContext(),
								getCotaDeTotal(bolao.getQtdCotaDisponivel(), bolao.getQtdCotaTotal())));

		atualizaValorTotal(bolao);
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
			this.headerEspeciais.setBackground(itemView.getContext().getDrawable(estilo.getImagemEspecialCarrossel()));
			if (bolao.getModalidade() == ModalidadeEnum.LOTOFACIL){
				modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
			} else {
				if(isMega30){
					modalidade.setText(SharedPreferencesUtils.getValorString(EspecialUtils.MEGA_DUAS_LINHAS,""));
				}
				else {
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
}
