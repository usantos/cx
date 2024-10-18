package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoVersoFragment;

public class DigitarCvvActivity extends AppCompatActivity {

	public static final String CARTAO_SALVO = "CARTAO_SALVO";
	public static final String CARTAO_NOVO = "CARTAO_NOVO";
	public static final String MEIO = "MEIO";

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private RetornoCartao cartaoSalvo;
	private CartaoCreditoConfig cartaoNovo;
	private MeioPagamentoConfig config;
	private boolean isAmex;
	private EditText cvv1, cvv2, cvv3, cvv4;
	private Button btnConfirmarCvv;

	private CartaoCreditoVersoFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_digitar_cvv);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.informarCvv)));

		configMenu();

		pegaExtras();
		atualizaCartaoVerso();
		configEditTexto();
		configButton();

		fragment = FragmentUtils.startCartaoCreditoVerso(getSupportFragmentManager(), R.id.containerCartaoVerso, getNomeMetodoPagamento(), "");
	}

	private String getNomeMetodoPagamento() {
		if (cartaoSalvo != null){
			return cartaoSalvo.getNomeMetodoPagamento();
		} else {
			return cartaoNovo.getNomeMetodo();
		}
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
			config = (MeioPagamentoConfig) bundle.getSerializable(MEIO);
			cartaoSalvo = (RetornoCartao) bundle.getSerializable(CARTAO_SALVO);
			cartaoNovo = (CartaoCreditoConfig)  bundle.getSerializable(CARTAO_NOVO);
		}
	}

	private void atualizaCartaoVerso() {
		((TextView) findViewById(R.id.textoCartaoFinal)).setText(getFinalCartao());

		isAmex = false;
		if (getNomeMetodoPagamento().equalsIgnoreCase("amex") ||
			getNomeMetodoPagamento().equalsIgnoreCase("american express") ||
			getNomeMetodoPagamento().equalsIgnoreCase("americanexpress")) {
			isAmex = true;
		}

	}

	private String getFinalCartao() {
		if (cartaoSalvo != null){
			return getString(R.string.cartaoFinal) +" " + cartaoSalvo.getUltimosDigitos();
		} else {
			if (cartaoNovo.getNumero().length() == 19){
				return getString(R.string.cartaoFinal) +" " + cartaoNovo.getNumero().substring(15,19);
			} else {
				return getString(R.string.cartaoFinal) +" " + cartaoNovo.getNumero().substring(12,16);
			}
		}
	}

	private void configEditTexto() {
		cvv1 = findViewById(R.id.cvv_digit1);
		cvv2 = findViewById(R.id.cvv_digit2);
		cvv3 = findViewById(R.id.cvv_digit3);
		cvv4 = findViewById(R.id.cvv_digit4);

		cvv1.requestFocus();

		//Digitou muda o foco para proximo
		mudaFocoProximo(cvv1, cvv2);
		mudaFocoProximo(cvv2, cvv3);
		mudaFocoProximo(cvv3, cvv3);
		if (isAmex) {
			cvv4.setVisibility(View.VISIBLE);
			mudaFocoProximo(cvv3, cvv4);
			mudaFocoProximo(cvv4, cvv4);
		}
	}

	private void mudaFocoProximo(EditText currentEditText, EditText nextEditText) {
		currentEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (currentEditText.getText().toString().length() == 1) { //size as per your requirement
					nextEditText.requestFocus();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void afterTextChanged(Editable s) {
				enableDisableButton();
			}
		});
	}

	private void configButton() {
		btnConfirmarCvv = findViewById(R.id.btnConfirmarCvv);
		btnConfirmarCvv.setOnClickListener(v -> {
			String cvv = cvv1.getText().toString() + cvv2.getText().toString() + cvv3.getText().toString();
			if (isAmex) {
				cvv += cvv4.getText().toString();
			}
			Intent intent = IntentUtil.getIntentOrigemDestino(this, ConfirmarCartaoActivity.class);
			if (cartaoSalvo != null){
				intent.putExtra(ConfirmarCartaoActivity.CARTAO_SALVO, cartaoSalvo);
			} else {
				intent.putExtra(ConfirmarCartaoActivity.CARTAO_NOVO, cartaoNovo);
			}
			intent.putExtra(ConfirmarCartaoActivity.MEIO, config);
			intent.putExtra(ConfirmarCartaoActivity.CVV, cvv);
			startActivity(intent);
		});
		enableDisableButton();
	}

	private void enableDisableButton() {
		if (cvv1.getText().length() > 0 && cvv2.getText().length() > 0 &&
			cvv3.getText().length() > 0 &&
			(isAmex && cvv4.getText().length() > 0 ? true : (!isAmex ? true : false))) {

		btnConfirmarCvv.setEnabled(true);
		btnConfirmarCvv.setBackground(getDrawable(R.drawable.button_rounded_verdeazul));
	} else {
			btnConfirmarCvv.setEnabled(false);
			btnConfirmarCvv.setBackground(getDrawable(R.color.cinzaDisable));
		}
	}
}