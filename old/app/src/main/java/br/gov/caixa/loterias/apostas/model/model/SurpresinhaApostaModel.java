package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;

public class SurpresinhaApostaModel {

	private Activity activity;
	private SimularApostaModel model;

	public SurpresinhaApostaModel(Activity activity) {
		this.activity = activity;
		this.model = new SimularApostaModel(activity);
	}

	public void addSurpresinhaCarrinho(IncluirSurpresinhaDTO surpresinha){
		model.addSurpresinhaCarrinho(surpresinha);
	}
}
