package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SilceResponse;

public abstract class AppModel {

	private Activity activity;

	public AppModel(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

	public void checkRedirect(SilceResponse response){
		if (response.getRedirect() != null){
			RedirectNetwork.checkRedirectSucesso(response.getRedirect(), getActivity());
		}
	}
}
