package br.gov.caixa.loterias.apostas.view.listener;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;

public interface OnTrevosListener {

	Button getButtonQtdTrevos();

	Button getButtonFinalizar();

	Button getButtonLimpar();

	void atualizaValorAposta(ParametroValorApostaDTO valorAposta);

	int getQtdConcursos();

	RelativeLayout getButtonSalvar();

	String nomeApostaFavorita();

	ImageButton btnSalvarAposta();

	void atualizaLayoutSalverAposta();
}
