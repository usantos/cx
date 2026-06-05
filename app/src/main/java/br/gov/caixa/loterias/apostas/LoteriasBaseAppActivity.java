package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsetsController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.gov.caixa.loterias.apostas.controllers.LoginActivity;
import br.gov.caixa.loterias.apostas.controllers.SplashScreenActivity;
import br.gov.caixa.loterias.apostas.controllers.TokenActivity;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.utils.AppState;
import br.gov.caixa.loterias.apostas.utils.AppUtils;

public abstract class LoteriasBaseAppActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        AppUtils.applyInsets(this, baseConsumeInsets());
		setSystenBarsColors();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (this instanceof LoginActivity) return;
		if (this instanceof TokenActivity) return;
		if (this instanceof SplashScreenActivity) return;


		if (AppState.wasInBackground) {
			AppState.wasInBackground = false;

			KeycloakBO kc = KeycloakBO.getInstance();

			if (kc.getAccessToken() == null || kc.isAccessTokenExpirado()) {
				redirectToLogin();
			}
		}
	}

	private void redirectToLogin() {
		KeycloakBO.getInstance().limparSessao(this);
		Intent intent = new Intent(this, SplashScreenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
	protected boolean baseConsumeInsets() {
		return true;
	}

	public void setSystenBarsColors(){
		//getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, color));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			getWindow().setDecorFitsSystemWindows(false);

			WindowInsetsController insetsController = getWindow().getDecorView().getWindowInsetsController();
			if (insetsController != null) {
				insetsController.setSystemBarsAppearance(
						WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
								WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
						WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
								WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
				);
			}
		}
	}


}