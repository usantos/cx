package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.FonteUtils;
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

		this.listener = listener;
		btnDetalhes.setOnClickListener(this);
		btnDiminui.setOnClickListener(this);
		btnAumenta.setOnClickListener(this);
		btnAddCarrinho.setOnClickListener(this);
	}

	@Override
	public void bind(CotasBolaoDTO bolao, int position) {
		preencheCabecalho(bolao);
		preencheConteudo(bolao);
		atualizaQuantidadeTextView(bolao.getQtdCotasBolao(), bolao.getQtdCotaDisponivel());
		btnAddCarrinho.setText(FonteUtils.textCaixaSTDBold(activity,activity.getResources().getString(R.string.adicionarAoCarrinho)));
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
		modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()));
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
			btnDiminui.setBackgroundTintList(getCor(R.color.cinzaescuro));
			btnDiminui.setEnabled(false);
			btnAumenta.setBackgroundTintList(getCor(R.color.megasenaclaro));
			btnAumenta.setEnabled(true);
		} else if (quantidade == maxPermitido){
			btnAumenta.setBackgroundTintList(getCor(R.color.cinzaescuro));
			btnAumenta.setEnabled(false);
			btnDiminui.setBackgroundTintList(getCor(R.color.megasenaclaro));
			btnDiminui.setEnabled(true);
		} else {
			btnDiminui.setBackgroundTintList(getCor(R.color.megasenaclaro));
			btnDiminui.setEnabled(true);
			btnAumenta.setBackgroundTintList(getCor(R.color.megasenaclaro));
			btnAumenta.setEnabled(true);
		}
	}

	private ColorStateList getCor(int cor) {
		return ContextCompat.getColorStateList(activity, cor);
	}
}
