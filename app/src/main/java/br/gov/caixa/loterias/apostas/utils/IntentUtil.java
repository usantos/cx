package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.content.Intent;

import java.io.Serializable;

public class IntentUtil {

	public static Intent getIntentLimpandoPilhaActivities(Activity activity, Class clazz){
		Intent intent = getIntentOrigemDestino(activity, clazz);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		return intent;
	}

	public static Intent getIntentOrigemDestino(Activity activity, Class clazz){
		return new Intent(activity, clazz);
	}

	public static Intent getIntentOrigemDestino(Activity activity, Class clazz, String key, Serializable extra){
		Intent intent = getIntentOrigemDestino(activity, clazz);
		intent.putExtra(key, extra);

		return intent;
	}
}
