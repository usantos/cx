package br.gov.caixa.loterias.apostas;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsetsController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.gov.caixa.loterias.apostas.utils.AppUtils;

public abstract class LoteriasBaseAppActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        AppUtils.applyInsets(this, baseConsumeInsets());
		setSystenBarsColors();
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