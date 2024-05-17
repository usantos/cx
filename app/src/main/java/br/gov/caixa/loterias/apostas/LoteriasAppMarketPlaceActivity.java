package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;

public class LoteriasAppMarketPlaceActivity extends LoteriasAppActivity {

	private ConstraintLayout btnFiltrar;
	private Toolbar toolbar;

	@Override
	public void configToolbar(int idToolbar) {
		toolbar = findViewById(idToolbar);
		setToolbar(toolbar);
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

	public void configBackgroundColorMenu(int cor){
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, cor)));
	}

	public void configColorTextMenu(int cor){
		toolbar.setTitleTextColor(ContextCompat.getColor(this, cor));
		configColorUpArrowMenu(cor);
	}

	public void configColorUpArrowMenu(int cor){
		Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.seta_esquerda);
		upArrow.mutate().setColorFilter(ContextCompat.getColor(this, cor), PorterDuff.Mode.SRC_ATOP);
		getSupportActionBar().setHomeAsUpIndicator(upArrow);
	}
}