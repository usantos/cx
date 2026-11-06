package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroPartida;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaAdapter;

public interface LocateAdapterListener {
	/**
	 * @return {@code true} se a combinação resultante do clique é válida e
	 * deve ser mantida; {@code false} se a combinação não é permitida e o
	 * clique (toggle) que a originou deve ser desfeito pelo chamador.
	 */
	boolean clickEquipeLoteca(ParametroPartida parametroPartida);

	default boolean podeAdicionarResultadoLoteca() {
		return true;
	}

	default boolean simulaVerificaPodeAdicionar(ParametroPartida parametroPartida) {
		return true;
	}
	default void onLimitePalpitesLotecaAtingido() {
	}
}
