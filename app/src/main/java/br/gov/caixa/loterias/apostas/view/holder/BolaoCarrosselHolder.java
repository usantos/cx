package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

public class BolaoCarrosselHolder extends LoteriasHolder<CotasBolaoDTO> implements View.OnClickListener, OnViewAumentaDiminuiQuantidade {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView modalidade;
	private TextView valorPremio;
	private TextView concurso;
	private TextView loterica;
	private TextView valorCota;
	private TextView qtdCotas;
	private Button btnDetalhes;
	private Button btnDiminui;
	private Button btnAumenta;
	private Button btnAddCarrinho;
	private RelativeLayout trapezio;
	private ConstraintLayout cabecalho;
	private View linhaNumero;

	private EstiloModalidadeMKP estilo;

	private OnItemCarrosselBolaoListener listener;


	public BolaoCarrosselHolder(Activity activity, View itemView, OnItemCarrosselBolaoListener listener) {
		super(itemView);
		this.activity 	= activity;
		modalidade 		= itemView.findViewById(R.id.textViewCardBolaoTitle);
		valorPremio 	= itemView.findViewById(R.id.valor_estimado_premio);
		concurso 		= itemView.findViewById(R.id.descricao_numero_concurso);
		loterica 		= itemView.findViewById(R.id.loterica_cidade_uf);
		valorCota 		= itemView.findViewById(R.id.valor_cota);
		btnDetalhes 	= itemView.findViewById(R.id.detalhes);
		btnDiminui 		= itemView.findViewById(R.id.diminui_cota);
		btnAumenta 		= itemView.findViewById(R.id.aumenta_cota);
		qtdCotas 		= itemView.findViewById(R.id.quantidade_cotas);
		btnAddCarrinho 	= itemView.findViewById(R.id.btn_add_carrinho);
		trapezio		= itemView.findViewById(R.id.layout_money);
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
		estilo = new EstiloModalidadeMKP(bolao.getModalidade());
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
		int corFonte = ContextCompat.getColor(activity, estilo.getCorFonte());
		trapezio.setBackground(ContextCompat.getDrawable(activity, estilo.getTrapezio()));
		cabecalho.setBackgroundColor(corClara);
		modalidade.setTextColor(corFonte);
		valorPremio.setTextColor(corLetraLista);
		btnDetalhes.setTextColor(corEscura);
		btnDetalhes.setBackground(ContextCompat.getDrawable(activity, estilo.getShapeBtnDetalhes()));
		btnDiminui.setBackgroundColor(corEscura);
		btnAumenta.setBackgroundColor(corEscura);
		btnAddCarrinho.setBackground(VectorUtils.getShape(R.drawable.rounded_card, estilo.getCorEscura()));
		linhaNumero.setBackgroundColor(corEscura);
	}

	private void preencheConteudo(CotasBolaoDTO bolao) {
		valorPremio.setText(ViewUtils.getMoedaFormat(bolao.getVrPremioEstimado()));
		concurso.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(),
														   itemView.getContext().getString(R.string.label_premio_estimado_concurso_numero_bold, bolao.getConcurso())));
		loterica.setText(bolao.getNomeFantasia()+ "\n" + bolao.getMunicipio().getNome() + " - " + bolao.getUf().getSigla());
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
			if (estilo.getImagemEspecialCarrossel() != -1){
				trapezio.setVisibility(View.INVISIBLE);
				cabecalho.setBackground(ContextCompat.getDrawable(activity, estilo.getImagemEspecialCarrossel()));
			}
			if (bolao.getModalidade() == ModalidadeEnum.LOTOFACIL){
				modalidade.setText(ModalidadeEnum.getDescricaoEspecialDuasLinhas(bolao.getModalidade()).toLowerCase());
			} else {
				modalidade.setText(ModalidadeEnum.getDescricaoEspecial(bolao.getModalidade()).toLowerCase());
			}
		} else {
			modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()).toLowerCase());
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
			btnDiminui.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, R.color.cinzaescuro));
			btnDiminui.setEnabled(false);
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, estilo.getCorEscura()));
			btnAumenta.setEnabled(true);
		} else if (quantidade == maxPermitido){
			btnAumenta.setBackground(VectorUtils.getShape(R.drawable.ic_botao_surpresinha, R.color.cinzaescuro));
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
