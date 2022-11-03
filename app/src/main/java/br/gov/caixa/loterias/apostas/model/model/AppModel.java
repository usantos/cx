package br.gov.caixa.loterias.apostas.model.model;

import android.app.Activity;

public abstract class AppModel {

	private Activity activity;

	public AppModel(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}
}
