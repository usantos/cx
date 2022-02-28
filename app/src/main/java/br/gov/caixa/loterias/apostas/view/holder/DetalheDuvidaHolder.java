package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaDetalheView;

public class DetalheDuvidaHolder extends LoteriasHolder<DuvidaDTO> {

	private ItemDuvidaDetalheView itemDuvidaDetalheView;

	public DetalheDuvidaHolder(View itemView) {
		super(itemView);
		this.itemDuvidaDetalheView = (ItemDuvidaDetalheView) itemView;
	}

	@Override
	public void bind(DuvidaDTO item, int position) {
		this.itemDuvidaDetalheView.setLayout(item);
		if (position != 0) {
			this.itemDuvidaDetalheView.getLayoutItemDetalheDuvida().setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
		}
	}
}
