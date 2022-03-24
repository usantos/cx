package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.utils.Aplicacao;
import br.gov.caixa.loterias.apostas.view.custom.PartidaView;
import br.gov.caixa.loterias.apostas.view.listener.LocateAdapterListener;

public class LotecaHolder extends LoteriasHolder<ParametroPartida>{

	private PartidaView partidaRow;
	private ParametroJogoDTO parametroJogo;

	public LotecaHolder(View itemView, ParametroJogoDTO parametroJogo, LocateAdapterListener listener) {
		super(itemView);
		this.partidaRow = (PartidaView) itemView;
		this.parametroJogo = parametroJogo;
		this.partidaRow.setListener(listener);
	}

	@Override
	public void bind(ParametroPartida item, int position) {
		Context ctx = Aplicacao.application.getApplicationContext();
		this.partidaRow.setLayout(item, parametroJogo);
		if(!item.isSelecionado()){
			this.partidaRow.lotecaTimeUm.getItemTitle().setTextColor(ctx.getResources().getColor(R.color.cinza));
			this.partidaRow.lotecaTimeUm.getItemDescription().setTextColor(ctx.getResources().getColor(R.color.cinza));
			this.partidaRow.lotecaTimeDois.getItemTitle().setTextColor(ctx.getResources().getColor(R.color.cinza));
			this.partidaRow.lotecaTimeDois.getItemDescription().setTextColor(ctx.getResources().getColor(R.color.cinza));
			this.partidaRow.lotecaEmpate.getItemTitle().setTextColor(ctx.getResources().getColor(R.color.cinza));
		}
	}
}
