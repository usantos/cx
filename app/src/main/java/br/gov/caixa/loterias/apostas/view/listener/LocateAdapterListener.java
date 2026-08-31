package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;

public interface LocateAdapterListener {
	void clickEquipeLoteca(ParametroPartida parametroPartida);

	default boolean podeAdicionarResultadoLoteca() {
		return true;
	}

	default boolean simulaVerificaPodeAdicionar(ParametroPartida parametroPartida) {
		return true;
	}
	default void onLimitePalpitesLotecaAtingido() {
	}
}
