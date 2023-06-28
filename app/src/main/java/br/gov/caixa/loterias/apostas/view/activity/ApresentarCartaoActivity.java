package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.utils.CartaoUtil;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ApresentarCartaoActivity extends AppCompatActivity {

	public static final String CARTAO = "CARTAO";

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private RetornoCartao cartao;
	private Button btnIncluirCodigo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apresentar_cartao);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.confirmarCartao)));
		configMenu();

		pegaExtras();
		atualizaCartaoFrente();
		configButton();
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

	private void pegaExtras() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			cartao = (RetornoCartao) bundle.getSerializable(CARTAO);
		}
	}

	private void atualizaCartaoFrente() {

		((AppCompatImageView) findViewById(R.id.imgBandeiraCartao)).setImageDrawable(CartaoUtil.getBandeira(this, cartao.getNomeMetodoPagamento()));

		((TextView) findViewById(R.id.textoNumero)).setText(getString(R.string.mascara_cartao_sem_ultimos_digitos) + "  " + cartao.getUltimosDigitos());

		((TextView) findViewById(R.id.textoValidMesAno)).setText(cartao.getMesValidade() + "/"  + cartao.getAnoValidade());

	}

	private void configButton() {
		btnIncluirCodigo = findViewById(R.id.btnIncluirCodigo);
		btnIncluirCodigo.setOnClickListener(v -> {
			Intent intent = IntentUtil.getIntentOrigemDestino(this, DigitarCvvActivity.class);
			intent.putExtra(ApresentarCartaoActivity.CARTAO, cartao);
			startActivity(intent);
		});
	}

}