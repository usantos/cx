package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.view.activity.BolaoActivity;

public class LoteriasAppMarketPlaceActivity extends LoteriasAppActivity {

	private Toolbar toolbar;

	@Override
	public void configToolbar(int idToolbar) {
		toolbar = findViewById(idToolbar);
		setToolbar(toolbar);
		setSupportActionBar(getToolbar());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void hideFiltrar(Boolean hideFiltrar){
		ImageView btnFiltrar = findViewById(R.id.icone_filtrar);
		if(hideFiltrar){
			btnFiltrar.setVisibility(View.GONE);
		}
		else {
			btnFiltrar.setVisibility(View.VISIBLE);
		}
	}
    public void hideSearch(Boolean shouldHide){
        ImageView btnFiltrar = findViewById(R.id.icone_buscar);
        if(shouldHide){
            btnFiltrar.setVisibility(View.GONE);
        } else {
            btnFiltrar.setVisibility(View.VISIBLE);
        }
    }

	public void configFiltrar(ActivityResultLauncher<Intent> launcher, FiltroAplicadoMarketplace filtro, FiltroAplicadoMarketplace filtroLimpo) {
        ImageView btnFiltrar = findViewById(R.id.icone_filtrar);
		if (filtro.isEqualsTo(filtroLimpo)) {
			btnFiltrar.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.botao_filtro_mkp));
		} else {
			btnFiltrar.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.botao_filtro_selecionado_mkp));
		}

		if (btnFiltrar != null){
			btnFiltrar.setOnClickListener(onFiltrarListener(launcher, filtro, filtroLimpo));
		}
		configMenu();
	}

	public void firstTimeFiltrar(){
		ImageView btnFiltrar = findViewById(R.id.icone_filtrar);
		btnFiltrar.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.botao_filtro_mkp));
	}

	public void configFiltrar(ActivityResultLauncher<Intent> launcher, FiltroAplicadoMarketplace filtro,Boolean desativarFiltro) {
		ImageView btnFiltrar = findViewById(R.id.icone_filtrar);
		btnFiltrar.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.botao_filtro_selecionado_mkp));

		if (btnFiltrar != null){
			btnFiltrar.setOnClickListener(onFiltrarListener(launcher, filtro,desativarFiltro));
		}
		configMenu();
	}

	@Override
	protected void configMenu(){
		ImageView btnDuvidas = findViewById(R.id.btn_duvidas_mkp);
		if(btnDuvidas != null){
			btnDuvidas.setOnClickListener(v -> abrirTermosUso());
		}

		ImageView btnBuscar = findViewById(R.id.icone_buscar);
		if (btnBuscar != null) {
			btnBuscar.setOnClickListener(v -> abrirBuscarFavoritas());
		}
	}

	private void abrirBuscarFavoritas() {
		Intent intent = new Intent(getBaseContext(), BuscarLotericasActivity.class);
		startActivity(intent);
	}

	private View.OnClickListener onFiltrarListener(ActivityResultLauncher<Intent> launcher,  FiltroAplicadoMarketplace filtro, FiltroAplicadoMarketplace filtroLimpo) {
		Bundle args = new Bundle();
		args.putSerializable(BolaoActivity.ARG_FILTRO_MARKETPLACE, filtro);
		args.putSerializable(BolaoActivity.ARG_FILTRO_LIMPO, filtroLimpo);
		Intent intent = IntentUtil.getIntentOrigemDestino(LoteriasAppMarketPlaceActivity.this, FiltraMarketplaceActivity.class);
		intent.putExtras(args);
		return v -> IntentUtil.startActivityForResult(launcher, intent);
	}

	private View.OnClickListener onFiltrarListener(ActivityResultLauncher<Intent> launcher, FiltroAplicadoMarketplace filtro, Boolean desativarFiltro) {
		Bundle args = new Bundle();
		args.putSerializable(BolaoActivity.ARG_FILTRO_MARKETPLACE, filtro);
		args.putBoolean(BolaoActivity.ARG_DESATIVAR_FILTRO,desativarFiltro);
		Intent intent = IntentUtil.getIntentOrigemDestino(LoteriasAppMarketPlaceActivity.this, FiltraMarketplaceActivity.class);
		intent.putExtras(args);
		return v -> IntentUtil.startActivityForResult(launcher, intent);
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