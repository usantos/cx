package br.gov.caixa.loterias.apostas.utils;

import android.widget.Toast;


public class ToastUtil {

	public static void toastShort(String msg){
		toast(msg, Toast.LENGTH_LONG);
	}

	public static void toastLong(String msg){
		toast( msg, Toast.LENGTH_LONG);
	}

	private static void toast(String msg, int time){
		Toast.makeText(Aplicacao.application.getApplicationContext(), msg, time).show();
	}

}
