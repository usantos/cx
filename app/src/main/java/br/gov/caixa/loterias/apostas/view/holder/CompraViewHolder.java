package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.content.Intent;
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

		this.itemCompraOrdemTextView.setText(ViewUtils.textFuturaAndFuturaBold(context, context.getString(R.string.label_compra_lista_situacao,
																											item.getSituacao().getDescricao())));;

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
