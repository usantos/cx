package br.gov.caixa.loterias.apostas.view.listener;

import android.widget.Button;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;

public interface OnTrevosListener {

	Button getButtonQtdTrevos();

	Button getButtonFinalizar();

	Button getButtonLimpar();

	void valorAposta(ParametroValorApostaDTO valorAposta);

}
