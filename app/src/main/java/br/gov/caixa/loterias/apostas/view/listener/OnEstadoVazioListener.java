package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.enums.FiltroBolaoResultadoEnum;

public interface OnEstadoVazioListener {
	void alterar(FiltroBolaoResultadoEnum tipoFiltro);
	void continuar(FiltroBolaoResultadoEnum tipoFiltro);

}
