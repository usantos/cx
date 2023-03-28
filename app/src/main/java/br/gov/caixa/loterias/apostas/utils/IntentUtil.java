package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
	public static Intent getIntentOrigemDestino(Activity activity, Class clazz, Bundle bundle){
		Intent intent = getIntentOrigemDestino(activity, clazz);
		intent.putExtras(bundle);

		return intent;
	}

	public static Intent getIntentWeb(String url){
		return new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
	}
}
