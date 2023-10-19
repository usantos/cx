package br.gov.caixa.loterias.apostas.view.holder;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Bolao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.VectorUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

public class BolaoHolder extends LoteriasHolder<Bolao> implements View.OnClickListener, OnViewAumentaDiminuiQuantidade {
	private Activity activity;
	private static final int QUANTIDADE_MINIMA = 1;
	private TextView modalidade;
	private TextView loterica;
	private TextView concurso;
	private TextView cidade;
	private TextView valor;
	private ImageView seta;
	private ConstraintLayout verMais;
	private TextView valorPremio;
	private TextView qtdCotas;
	private Button btnDetalhes;
	private Button btnDiminui;
	private Button btnAumenta;

	private OnItemBolaoListener listener;


	public BolaoHolder(Activity activity, View itemView, OnItemBolaoListener listener) {
		super(itemView);
		this.activity = activity;
		modalidade 	= itemView.findViewById(R.id.modalidade_bolao);
		loterica 	= itemView.findViewById(R.id.loterica_bolao);
		concurso 	= itemView.findViewById(R.id.concurso_bolao);
		cidade 		= itemView.findViewById(R.id.cidade_bolao);
		valor 		= itemView.findViewById(R.id.valor_bolao);
		seta 		= itemView.findViewById(R.id.seta_bolao);
		verMais 	= itemView.findViewById(R.id.container_ver_mais);
		valorPremio = itemView.findViewById(R.id.valor_estimado_premio);
		btnDetalhes = itemView.findViewById(R.id.detalhes);
		btnDiminui 	= itemView.findViewById(R.id.diminui_cota);
		btnAumenta 	= itemView.findViewById(R.id.aumenta_cota);
		qtdCotas 	= itemView.findViewById(R.id.quantidade_cotas);

		this.listener = listener;
		seta.setOnClickListener(this);
		btnDetalhes.setOnClickListener(this);
		btnDiminui.setOnClickListener(this);
		btnAumenta.setOnClickListener(this);
	}

	@Override
	public void bind(Bolao bolao, int position) {
		modalidade.setText(ModalidadeEnum.fromString(bolao.getModalidade()));
		loterica.setText(bolao.getLoterica());
		concurso.setText(String.valueOf(bolao.getConcurso()));
		cidade.setText(bolao.getCidade());
		valor.setText(bolao.getValor());
		valorPremio.setText(bolao.getValorEstimadoPremio());
		atualizaBotoesQtdCotas(bolao.getQuantidade(), bolao.getQtdPermitida());
		mostrarDetalhes(bolao.isAberto());
		qtdCotas.setText(Integer.toString(bolao.getQuantidade()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.seta_bolao:
				listener.verMais(this, getAbsoluteAdapterPosition());
				break;
			case R.id.detalhes:
				listener.detalhes(getAbsoluteAdapterPosition());
				break;
			case R.id.diminui_cota:
				listener.diminui(this, getAbsoluteAdapterPosition());
				break;
			case R.id.aumenta_cota:
				listener.aumenta(this, getAbsoluteAdapterPosition());
				break;
		}
	}

	public void mostrarDetalhes(Boolean aberto) {
		if (aberto){
			seta.setBackground(VectorUtils.getShape(R.drawable.ic_seta_cima_resultado, R.color.azulBolao));
			verMais.setVisibility(View.VISIBLE);
		} else {
			seta.setBackground(VectorUtils.getShape(R.drawable.ic_seta_baixo_resultado, R.color.azulBolao));
			verMais.setVisibility(View.GONE);
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
