package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
	private static Context context = Aplicacao.application.getApplicationContext();

	public static < T > void setValor(String chave, T valor) {
		SharedPreferences.Editor editor = getEditor(chave);
		if (editor != null) {
			if(valor instanceof String){
				editor.putString(chave,(String)valor);
			}
			if(valor instanceof Integer){
				editor.putInt(chave,(Integer)valor);
			}
			if(valor instanceof Boolean){
				editor.putBoolean(chave,(Boolean) valor);
			}
			if(valor instanceof Float){
				editor.putFloat(chave,(Float)valor);
			}
			if(valor instanceof Long){
				editor.putLong(chave,(Long)valor);
			}
			editor.commit();
		}
	}

	public static String getValorString(String chave, String valDefault) {
		SharedPreferences	sharedPreferences	=	getSharedPreferences(chave);
		if(sharedPreferences != null){
			return sharedPreferences.getString(chave, valDefault);
		}
		return valDefault;
	}

	public static int getValorInt(String chave, int valDefault) {
		SharedPreferences	sharedPreferences	=	getSharedPreferences(chave);
		if(sharedPreferences != null){
			return sharedPreferences.getInt(chave, valDefault);
		}
		return valDefault;
	}

	public static Boolean getValorBoolean(String chave, Boolean valDefault) {
		SharedPreferences	sharedPreferences	=	getSharedPreferences(chave);
		if(sharedPreferences != null){
			return sharedPreferences.getBoolean(chave, valDefault);
		}
		return valDefault;
	}

	public static Float getValorFloat(String chave, Float valDefault) {
		SharedPreferences	sharedPreferences	=	getSharedPreferences(chave);
		if(sharedPreferences != null){
			return sharedPreferences.getFloat(chave, valDefault);
		}
		return valDefault;
	}

	public static Long getValorLong(String chave, Long valDefault) {
		SharedPreferences	sharedPreferences	=	getSharedPreferences(chave);
		if(sharedPreferences != null){
			return sharedPreferences.getLong(chave, valDefault);
		}
		return valDefault;
	}

	private static SharedPreferences.Editor getEditor(String chave) {
		SharedPreferences.Editor	editor	=	null;
		SharedPreferences	sharedPreferences	=	getSharedPreferences(chave);
		if(sharedPreferences != null){
			editor	=	sharedPreferences.edit();
		}
		return  editor;
	}

	private static SharedPreferences getSharedPreferences(String chave) {
		SharedPreferences	sharedPreferences = null;
		if (context != null) {
			sharedPreferences = context.getSharedPreferences(chave, Context.MODE_PRIVATE);
		}
		return sharedPreferences;
	}

}
