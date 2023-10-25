package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.constraintlayout.widget.ConstraintLayout;

import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;

public class LoteriasAppMarketPlaceActivity extends LoteriasAppActivity {

	private ConstraintLayout btnFiltrar;

	@Override
	public void configToolbar(int idToolbar) {
		setToolbar(findViewById(idToolbar));
		setSupportActionBar(getToolbar());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void configFiltrar(ActivityResultLauncher<Intent> launcher){
		btnFiltrar = findViewById(R.id.btn_filtrar);
		if (btnFiltrar != null){
			btnFiltrar.setOnClickListener(onFiltrarListener(launcher));
		}
	}

	private View.OnClickListener onFiltrarListener(ActivityResultLauncher<Intent> launcher) {
		return v -> IntentUtil.startActivityForResult(launcher, LoteriasAppMarketPlaceActivity.this,
												  FiltraMarketplaceActivity.class);
	}

}