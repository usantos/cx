package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;

public class LoteriasAppActivity extends LoteriasBaseAppActivity {

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	public void configToolbar(int idToolbar) {
		toolbar = findViewById(idToolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		configMenu();
	}

	protected void configMenu() {
		btnAjuda = findViewById(R.id.ib_duvidas);
		btnAjuda.setVisibility(View.VISIBLE);
		btnAjuda.setOnClickListener(v -> abrirTermosUso());
	}

	protected void abrirTermosUso() {
		Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	public Toolbar getToolbar(){
		return this.toolbar;
	}

	public void setToolbar(Toolbar toolbar){
		this.toolbar = toolbar;
	}

	public void hideHomeAsUpButton(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

}