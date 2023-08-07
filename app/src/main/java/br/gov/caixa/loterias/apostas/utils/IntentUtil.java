package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity_;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity_;

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

	public static Intent getIntentDeepLink(Activity activity, String deepLink) {
		Intent intent = null;

		if (deepLink.equals(activity.getResources().getString(R.string.deeplink_path_combos))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, PrincipalActivity_.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.deeplink_path_apostas))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, MinhasApostasActivity_.class);
		} else if (deepLink.equals(activity.getResources().getString(R.string.depplink_path_compras))) {
			intent = IntentUtil.getIntentOrigemDestino(activity, ListaComprasActivity.class);
		}

		return intent;
	}

}
