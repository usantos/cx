package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.view.animation.AnimacaoHeight;

public class DetalheDuvidaHolder extends LoteriasHolder<DuvidaDTO> {

	private LinearLayout layoutCabecalhoDuvidaDetalhe, layoutItemDetalheDuvida, layoutTextDetalhe;
	private TextView textDuvidaDetalhe, textDetalhe;


	public DetalheDuvidaHolder(View itemView) {
		super(itemView);
		layoutCabecalhoDuvidaDetalhe = itemView.findViewById(R.id.layoutCabecalhoDuvidaDetalhe);
		layoutItemDetalheDuvida = itemView.findViewById(R.id.layoutItemDetalheDuvida);
		layoutTextDetalhe = itemView.findViewById(R.id.layoutTextDetalhe);
		textDuvidaDetalhe = itemView.findViewById(R.id.textDuvidaDetalhe);
		textDetalhe = itemView.findViewById(R.id.textDetalhe);
	}

	@Override
	public void bind(DuvidaDTO item, int position) {
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
						textDuvidaDetalhe.setTextSize(14);
						textDuvidaDetalhe.setTextColor(itemView.getContext().getResources().getColor(R.color.cinza));
						AnimacaoHeight.collapse(layoutTextDetalhe);

					} else {
						layoutCabecalhoDuvidaDetalhe.setSelected(Boolean.TRUE);
						textDuvidaDetalhe.setTextSize(16);
						textDuvidaDetalhe.setTextColor(itemView.getContext().getResources().getColor(R.color.verdeazul));
						AnimacaoHeight.expand(layoutTextDetalhe);
					}
				}
			});
		}
	}
}
