package br.gov.caixa.loterias.apostas.utils;

import android.widget.Toast;

import br.gov.caixa.loterias.apostas.BuildConfig;

public class TestVisao {

	public static void toast(String msg){
		if(isTeste()) {
			ToastUtil.toastLong(msg);
		}
	}

	public static boolean isTeste(){
		return BuildConfig.DEBUG && !BuildConfig.FLAVOR.equalsIgnoreCase("prd");
	}
}
