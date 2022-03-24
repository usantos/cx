package br.gov.caixa.loterias.apostas.utils;

import android.widget.Toast;

import br.gov.caixa.loterias.apostas.BuildConfig;

public class TestVisao {

	public static void toast(String msg){
		if(BuildConfig.DEBUG && !BuildConfig.FLAVOR.equalsIgnoreCase("prd")) {
			Toast.makeText(Aplicacao.application.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
	}
}
