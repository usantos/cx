package br.gov.caixa.loterias.apostas.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class FormaPagamentoActivity extends AppCompatActivity {

	FrameLayout frameMercadoPago, frameRecargaPay;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forma_pagamento);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		frameMercadoPago = findViewById(R.id.fragmentMercadoPago);
		frameRecargaPay = findViewById(R.id.fragmentRecargaPay);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.settings, menu);

		return true;
	}
}