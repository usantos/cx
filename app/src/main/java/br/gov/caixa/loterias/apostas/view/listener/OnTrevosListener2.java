package br.gov.caixa.loterias.apostas.view.listener;

import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;

public interface OnTrevosListener2 {
	Button getButtonQtdTrevos();
	Button getButtonFinalizar();
	void atualizaValorAposta(ParametroValorApostaDTO valorAposta);
	int getQtdConcursos();
	void atualizaLayoutSalvarAposta();
}
