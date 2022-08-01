package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnFavoritarClickListener;

public class CompraViewHolder extends LoteriasHolder<CompraDTO> {
	protected TextView dataCompra;
	protected TextView valorCompra;
	protected TextView itemCompraOrdemTextView;
	protected ImageButton ibFavoritar;
	private Context context;
	private OnFavoritarClickListener listener;

	public CompraViewHolder(View itemView, Context activity, OnFavoritarClickListener listener) {
		super(itemView);
		dataCompra = itemView.findViewById(R.id.dataCompraTextView);
		valorCompra = itemView.findViewById(R.id.valorCompra);
		itemCompraOrdemTextView = itemView.findViewById(R.id.situacaoCompraTextView);
		ibFavoritar = itemView.findViewById(R.id.ib_favoritar);
		context = activity;
		this.listener = listener;
	}

	@Override
	public void bind(CompraDTO item, int position) {
		if(item.getDataFinalizacaoCompra() != null){
			this.dataCompra.setText(context.getResources().getString(R.string.data_dois_pontos_espaco) + item.getDataFinalizacaoCompra());
		}
		this.valorCompra.setText(br.gov.caixa.loterias.apostas.utils.ViewUtils.getMoedaFormat(item.getValorTotal()));
		final int ordemLista = position + 1;

		this.valorCompra.setText(ViewUtils.textFuturaAndFuturaBold(context, context.getString(R.string.label_compra_lista_valor,
																								ViewUtils.getMoedaFormat(item.getValorTotal()))));
		/**
		 * Situação finalizada: #143DA6
		 * Situação não processado: #900000
		 * Situação em processamento: #FDAB01
		 * Situação de pago: #008254
		 *
		 * ------AZUL #143DA6 (POSITIVA), DEBITO_REALIZADO(6L), FINALIZADA(8L),
		 *
		 * ------VERMELHO #900000 (NEGATIVA), DEBITO_NAO_AUTORIZADO(3L), ESTORNADA(4L), CANCELADA(5L),
		 *
		 * ------AMARELO  (INDEFINICAO, ANDAMENTO, CARRINHO(1L), DEBITO_INICIADO(2L), EM_PROCESSAMENTO(7L), EM_PROCESSAMENTO_REPRESADA(9L);
		 * SITUACAO_COMPRA_ENUM
		 * */
		this.itemCompraOrdemTextView.setText(ViewUtils.textFuturaAndFuturaBold(context, context.getString(R.string.label_compra_lista_situacao,
																											item.getSituacao().getDescricao())));
		if (item.getSituacao().getDescricao().equalsIgnoreCase("POSITIVA") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("DEBITO_REALIZADO") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("FINALIZADA")) {
			this.itemCompraOrdemTextView.setTextColor(context.getResources().getColor(R.color.azul_minhas_compras));
		}
		if (item.getSituacao().getDescricao().equalsIgnoreCase("NEGATIVA") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("DEBITO_NAO_AUTORIZADO") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("ESTORNADA") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("CANCELADA")) {
			this.itemCompraOrdemTextView.setTextColor(context.getResources().getColor(R.color.vermelho_minhas_compras)));
		}
		if (item.getSituacao().getDescricao().equalsIgnoreCase("INDEFINICAO") ||
		    item.getSituacao().getDescricao().equalsIgnoreCase("ANDAMENTO") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("CARRINHO") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("DEBITO_INICIADO") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("EM_PROCESSAMENTO") ||
			item.getSituacao().getDescricao().equalsIgnoreCase("EM_PROCESSAMENTO_REPRESADA")) {
			this.itemCompraOrdemTextView.setTextColor(context.getResources().getColor(R.color.amarelo_minhas_compras)));
		}


		this.itemView.setOnClickListener(view -> {

			Intent activity      = new Intent(context, DetalhesComprasActivity.class);
			String arrayAsString = new Gson().toJson(item);
			activity.putExtra(context.getResources().getString(R.string.extra_compra_dto), arrayAsString);
			activity.putExtra(context.getResources().getString(R.string.extra_ordem), ordemLista);
			context.startActivity(activity);
		});

		this.ibFavoritar.setOnClickListener( v -> {
			if(listener != null){
				listener.onItemClick(item);
			}
		});
	}
}
