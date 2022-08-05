package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
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
									null,
									new MeioPagamentoConfig(R.color.laranja_recarga_pay,R.drawable.recargapay));
	}

	private void startFragmentMercadoPago() {
		frameMercadoPago = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									R.id.fragmentMercadoPago,
									getListCartoes(),
									new MeioPagamentoConfig(R.color.azul_mercado_pago,R.drawable.ic_mercado_pago));
	}

	private List<RetornoCartao> getListCartoes() {
		List<RetornoCartao> lista = new ArrayList<>();

		RetornoCartao cartaofav = new RetornoCartao();
		cartaofav.setFav(true);
		cartaofav.setUltimosDigitos("1234");
		cartaofav.setIdMetodoPagamento("master");
		cartaofav.setIdCartao(1L);

		RetornoCartao cartao = new RetornoCartao();
		cartao.setFav(false);
		cartao.setUltimosDigitos("3456");
		cartao.setIdMetodoPagamento("hipercard");
		cartao.setIdCartao(2L);

		RetornoCartao cartao3 = new RetornoCartao();
		cartao3.setFav(false);
		cartao3.setUltimosDigitos("5678");
		cartao3.setIdMetodoPagamento("visa");
		cartao3.setIdCartao(3L);

		RetornoCartao cartao4 = new RetornoCartao();
		cartao4.setFav(false);
		cartao4.setUltimosDigitos("7890");
		cartao4.setIdMetodoPagamento("amex");
		cartao4.setIdCartao(4L);

		lista.add(cartaofav);
		lista.add(cartao);
		lista.add(cartao3);
		lista.add(cartao4);

		return lista;
	}
}