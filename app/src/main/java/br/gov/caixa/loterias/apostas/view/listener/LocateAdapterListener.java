package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;

public interface LocateAdapterListener {
	void clickEquipeLoteca(ParametroPartida parametroPartida);

	default boolean podeAdicionarResultadoLoteca() {
		return true;
	}

	default void onLimitePalpitesLotecaAtingido() {
	}
}
