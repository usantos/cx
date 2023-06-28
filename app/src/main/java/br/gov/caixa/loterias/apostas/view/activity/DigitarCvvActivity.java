package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.CartaoUtil;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class DigitarCvvActivity extends AppCompatActivity {

	public static final String CARTAO = "CARTAO";

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private RetornoCartao cartao;
	private boolean isAmex;
	private EditText cvv1, cvv2, cvv3, cvv4;
	private Button btnConfirmarCvv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_digitar_cvv);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.informarCvv)));
		configMenu();

		pegaExtras();
		atualizaCartaoVerso();
		configEditTexto();
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

	private void atualizaCartaoVerso() {

		((AppCompatImageView) findViewById(R.id.imgBandeiraVerso)).setImageDrawable(CartaoUtil.getBandeira(this, cartao.getNomeMetodoPagamento()));
		((TextView) findViewById(R.id.textoCartaoFinal)).setText(getString(R.string.cartaoFinal) +" " + cartao.getUltimosDigitos());

		isAmex = false;
		if (cartao.getNomeMetodoPagamento().equalsIgnoreCase("amex") ||
			cartao.getNomeMetodoPagamento().equalsIgnoreCase("american express")) {

			isAmex = true;
			((TextView) findViewById(R.id.textoCVV)).setText(getString(R.string.xxx) + "X");
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
			intent.putExtra(ConfirmarCartaoActivity.CARTAO, cartao);
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