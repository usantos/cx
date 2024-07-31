package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoHeight;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaDetalheView;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaDetalheView_;

public class DetalheDuvidaHolder extends LoteriasHolder<DuvidaDTO> {

	//private ItemDuvidaDetalheView itemDuvidaDetalheView;

	private LinearLayout layoutCabecalhoDuvidaDetalhe, layoutItemDetalheDuvida, layoutTextDetalhe;
	private TextView textDuvidaDetalhe, textDetalhe;


	public DetalheDuvidaHolder(View itemView) {
		super(itemView);
		//this.itemDuvidaDetalheView = (ItemDuvidaDetalheView) itemView;

		layoutCabecalhoDuvidaDetalhe = itemView.findViewById(R.id.layoutCabecalhoDuvidaDetalhe);
		layoutItemDetalheDuvida = itemView.findViewById(R.id.layoutItemDetalheDuvida);
		layoutTextDetalhe = itemView.findViewById(R.id.layoutTextDetalhe);
		textDuvidaDetalhe = itemView.findViewById(R.id.textDuvidaDetalhe);
		textDetalhe = itemView.findViewById(R.id.textDetalhe);
	}

	@Override
	public void bind(DuvidaDTO item, int position) {
		//this.itemDuvidaDetalheView.setLayout(item);
		if (position != 0) {
			layoutItemDetalheDuvida.setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
		}

		textDuvidaDetalhe.setText(item.getPergunta());
		textDetalhe.setText(item.getResposta());

		if (this.layoutCabecalhoDuvidaDetalhe!= null) {
			this.layoutCabecalhoDuvidaDetalhe.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (layoutCabecalhoDuvidaDetalhe.isSelected()) {
						layoutCabecalhoDuvidaDetalhe.setSelected(Boolean.FALSE);
						//textDuvidaDetalhe.setTypeface(ViewUtils.getFontArialMT(itemView.getContext()));
						textDuvidaDetalhe.setTextSize(14);
						textDuvidaDetalhe.setTextColor(itemView.getContext().getResources().getColor(R.color.cinza));
						AnimacaoHeight.collapse(layoutTextDetalhe);

					} else {
						layoutCabecalhoDuvidaDetalhe.setSelected(Boolean.TRUE);
						//textDuvidaDetalhe.setTypeface(ViewUtils.getFontFutura(itemView.getContext()));
						textDuvidaDetalhe.setTextSize(16);
						textDuvidaDetalhe.setTextColor(itemView.getContext().getResources().getColor(R.color.verdeazul));
						AnimacaoHeight.expand(layoutTextDetalhe);
					}
				}
			});
		}
	}
}
