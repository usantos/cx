package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostas.utils.ContagemRegressiva;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtil;
import br.gov.caixa.loterias.apostas.utils.SituacaoCompraUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnFavoritarClickListener;

public class CompraViewHolder extends LoteriasHolder<CompraDTO> {
	protected TextView dataCompra, horaCompra;
	protected TextView valorCompra;
	protected TextView itemCompraOrdemTextView;
	protected TextView textTotal;
	protected ImageButton ibFavoritar;
	protected TextView meioDePagamento;
	protected TextView textContadorDown;
	protected ImageView imageViewRelogio;
	protected Button btnMinhasCompras;

	private Context context;
	private String dataHoraServidor;
	private OnFavoritarClickListener listener;

	public CompraViewHolder(View itemView, Context activity, String dataHoraServidor, OnFavoritarClickListener listener) {
		super(itemView);
		itemCompraOrdemTextView = itemView.findViewById(R.id.situacaoCompraTextView);
		textTotal = itemView.findViewById(R.id.txtTotal);
		dataCompra = itemView.findViewById(R.id.dataCompraTextView);
		horaCompra = itemView.findViewById(R.id.horaCompraTextView);
		meioDePagamento = itemView.findViewById(R.id.meioDePagamento);
		textContadorDown = itemView.findViewById(R.id.contadorDown);
		valorCompra = itemView.findViewById(R.id.valorCompra);
		ibFavoritar = itemView.findViewById(R.id.ib_favoritar);
		imageViewRelogio = itemView.findViewById(R.id.imageViewRelogio);
		btnMinhasCompras = itemView.findViewById(R.id.btnMinhasCompras);
		context = activity;
		this.dataHoraServidor = dataHoraServidor;
		this.listener = listener;
	}

	@Override
	public void bind(CompraDTO item, int position) {
		final int ordemLista = position + 1;

		ContagemRegressiva contagemRegressiva = null;
		//Nao pode ser inicializado abaixo
		long tempo_inicial_pix = 0;

		preencheSituacaoCompra(item);
		if (SituacaoCompraUtil.isSituacaoPositiva(item.getSituacao())) {
			this.itemCompraOrdemTextView.setTextColor(context.getResources().getColor(R.color.azul_minhas_compras));
		} else if (SituacaoCompraUtil.isSituacaoNegativa(item.getSituacao())) {
			this.itemCompraOrdemTextView.setTextColor(context.getResources().getColor(R.color.vermelho_minhas_compras));
		} else if (SituacaoCompraUtil.isSituacaoProcessamento(item.getSituacao())) {
			this.itemCompraOrdemTextView.setTextColor(context.getResources().getColor(R.color.amarelo_minhas_compras));
		} else {
			this.itemCompraOrdemTextView.setTextColor(context.getResources().getColor(R.color.azul_minhas_compras));
		}

		this.textTotal.setText(ViewUtils.textCaixaSTDBold(context, context.getString(R.string.label_compra_lista_valor)));

		if (item.getDataFinalizacaoCompra() != null) {
			this.dataCompra.setText(context.getResources().getString(R.string.data_dois_pontos_espaco) + item.getDataFinalizacaoCompra());
			this.horaCompra.setText(context.getString(R.string.hora_dois_pontos) + item.getHoraFinalizacaoCompra());
		}

		this.valorCompra.setText(ViewUtils.getMoedaFormat(item.getValorTotal()));

		this.meioDePagamento.setText(ViewUtils.textCaixaSTDBold(context,
				context.getString(R.string.label_compra_lista_meio_de_pagamento,
						MeioPagamentoUtil.getDescricao(item.getMeioPagamento()))));

		//PIX
		imageViewRelogio.setVisibility(View.GONE);
		textContadorDown.setVisibility(View.GONE);
		btnMinhasCompras.setVisibility(View.GONE);
		if (item.getMeioPagamento().getValor() == MeioPagamentoUtil.PIX) {

			if (SituacaoCompraUtil.isSituacaoAguardandoPagamento(item.getSituacao())) {
				imageViewRelogio.setVisibility(View.VISIBLE);
				textContadorDown.setVisibility(View.VISIBLE);
				btnMinhasCompras.setVisibility(View.GONE);

				tempo_inicial_pix = item.getTimerSeconds(dataHoraServidor);
				contagemRegressiva = new ContagemRegressiva(textContadorDown,tempo_inicial_pix * 1000, 1 * 1000) {
					@Override
					public void onFinish() {
						btnMinhasCompras.setVisibility(View.GONE);
					}
				};
				contagemRegressiva.start();

				btnMinhasCompras.setText(ViewUtils.textCaixaSTDBold(context, context.getString(R.string.label_compra_lista_alterar_compra)));
			}
		}

		ContagemRegressiva finalContagemRegressiva = contagemRegressiva;
		this.itemView.setOnClickListener(view -> {
			Intent activity      = new Intent(context, DetalhesComprasActivity.class);
			String arrayAsString = new Gson().toJson(item);
			activity.putExtra(context.getResources().getString(R.string.extra_compra_dto), arrayAsString);
			activity.putExtra(context.getResources().getString(R.string.extra_ordem), ordemLista);
			if (finalContagemRegressiva != null) {
				activity.putExtra(context.getResources().getString(R.string.extra_tempo_pix), finalContagemRegressiva.getMillisUntilFinish());
			}
			context.startActivity(activity);
		});
	}

	private void preencheSituacaoCompra(CompraDTO item) {
		String descricao = SituacaoCompraUtil.getDescricao(item);
		this.itemCompraOrdemTextView.setText(ViewUtils.textCaixaSTDBold(context, context.getString(R.string.label_compra_lista_situacao_descricao,
																				descricao)));
	}
}
