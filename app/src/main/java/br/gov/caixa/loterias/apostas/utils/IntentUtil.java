package br.gov.caixa.loterias.apostas.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.MinhasApostasActivity_;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity_;
import br.gov.caixa.loterias.apostas.controllers.SimularApostaActivity_;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.view.activity.BolaoActivity;
import br.gov.caixa.loterias.apostas.view.activity.TutorialActivity;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;

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

	public static Intent getIntentFimTutorial(Activity activity, ModalidadeEnum modalidade, Integer IdModalidade) {
		if (modalidade == ModalidadeEnum.BOLAO){
			Intent intent = new Intent(activity, BolaoActivity.class);
			intent.putExtra(BolaoActivity.ARG_IDMODALIDADE,IdModalidade);
			return intent;
		} else {
			return new Intent(activity, SimularApostaActivity_.class);
		}
	}

	public static Intent getIntentTutorial(Activity activity, ModalidadeEnum modalidade, int idModalidade) {
		Intent intent = new Intent(activity, TutorialActivity.class);
		intent.putExtra(activity.getResources().getString(R.string.tipoAposta), modalidade);
		intent.putExtra(BolaoActivity.ARG_IDMODALIDADE, idModalidade);
		return intent;
	}

	public static Intent getIntentTutorial(Activity activity, ModalidadeEnum modalidade, ParametroJogoDTO parametroJogo) {
		Intent intent = new Intent(activity, TutorialActivity.class);
		intent.putExtra(activity.getResources().getString(R.string.tipoAposta), modalidade);
		intent.putExtra(activity.getResources().getString(R.string.extra_modalidade), parametroJogo);

		return intent;
	}

	public static ActivityResultLauncher<Intent> registerLauncherActivityForResult(AppCompatActivity activity, OnActivityForResult listener) {
		return activity
				.registerForActivityResult(new ActivityResultContracts
												   .StartActivityForResult(),
										   result -> {
											   listener.result(result);
										   });
	}

	public static void startActivityForResult(ActivityResultLauncher<Intent> launcher, ComponentActivity activity, Class clazz) {
		launcher.launch(IntentUtil.getIntentOrigemDestino(activity, clazz));
	}

	public static void startActivityForResult(ActivityResultLauncher<Intent> launcher, Intent intent) {
		launcher.launch(intent);
	}
}
