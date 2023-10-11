package br.gov.caixa.loterias.apostas;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;

public class LoteriasAppMarketPlaceActivity extends LoteriasAppActivity {

	private ConstraintLayout btnFiltrar;

	@Override
	public void configToolbar(int idToolbar) {
		setToolbar(findViewById(idToolbar));
		setSupportActionBar(getToolbar());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void configFiltrar(){
		btnFiltrar = findViewById(R.id.btn_filtrar);
		if (btnFiltrar != null){
			btnFiltrar.setOnClickListener(onFiltrarListener());
		}

	}

	private View.OnClickListener onFiltrarListener() {
		return v -> {
			startActivity(IntentUtil.getIntentOrigemDestino(this, FiltraMarketplaceActivity.class));
		};
	}

}