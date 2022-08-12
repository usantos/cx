package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;

public class FormaPagamentoActivity extends AppCompatActivity {

	MeioPagamentoFragment frameMercadoPago, frameRecargaPay;
	private Toolbar toolbar;
	private ImageButton btnAjuda;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forma_pagamento);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		configMenu();

		startFragmentMercadoPago();
		startFragmentRecargaPay();

	}

	private void configMenu() {
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

	private void startFragmentRecargaPay() {
		frameRecargaPay = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									R.id.fragmentRecargaPay,
									new MeioPagamentoConfig(R.color.laranja_recarga_pay, R.drawable.recargapay, getRecargaPay()));
	}


	private DTOEnumLong getRecargaPay() {
		DTOEnumLong recarga = new DTOEnumLong();
		recarga.setValor(5L);
		recarga.setDescricao("RecargaPay");
		return recarga;
	}

	private DTOEnumLong getMercadoPago() {
		DTOEnumLong recarga = new DTOEnumLong();
		recarga.setValor(1L);
		recarga.setDescricao("Mercado pago");
		return recarga;
	}

	private void startFragmentMercadoPago() {
		frameMercadoPago = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									R.id.fragmentMercadoPago,
									new MeioPagamentoConfig(R.color.azul_mercado_pago, R.drawable.ic_mercado_pago, getMercadoPago()));
	}

}