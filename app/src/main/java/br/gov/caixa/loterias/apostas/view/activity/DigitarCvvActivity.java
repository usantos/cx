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
import br.gov.caixa.loterias.apostas.utils.CartaoUtil;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class DigitarCvvActivity extends AppCompatActivity {

	public static final String CARTAO = "CARTAO";

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private RetornoCartao cartao;
	private Button btnIncluirCodigo;
	private boolean isAmex;

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
		//configButton();
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
		EditText cvv1 = findViewById(R.id.cvv_digit1);
		EditText cvv2 = findViewById(R.id.cvv_digit2);
		EditText cvv3 = findViewById(R.id.cvv_digit3);
		EditText cvv4 = findViewById(R.id.cvv_digit4);

		//Força Mostrar o teclado
		cvv1.requestFocus();
		cvv1.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(DigitarCvvActivity.INPUT_METHOD_SERVICE);
				imm.showSoftInput(cvv1, InputMethodManager.SHOW_FORCED);
			}
		},200);

		//Digitou muda o foco para o 2
		cvv1.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (cvv1.getText().toString().length() == 1) { //size as per your requirement
					cvv2.requestFocus();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});

		//Digitou muda o foco para o 3
		cvv2.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (cvv2.getText().toString().length() == 1) { //size as per your requirement
					cvv3.requestFocus();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});

		if (isAmex) {
			cvv4.setVisibility(View.VISIBLE);
			//Digitou muda o foco para o 4
			cvv3.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (cvv3.getText().toString().length() == 1) { //size as per your requirement
						cvv4.requestFocus();
					}
				}
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				public void afterTextChanged(Editable s) {}
			});
		}

		//Enter
		cvv1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Toast.makeText(getApplicationContext(), "*** Botao Enter Precionado no 1 ***", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		//Enter
		cvv2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Toast.makeText(getApplicationContext(), "*** Botao Enter Precionado no 2 ***", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		//Enter
		cvv3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Toast.makeText(getApplicationContext(), "*** Botao Enter Precionado no 3 ***", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

		//Enter
		cvv4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Toast.makeText(getApplicationContext(), "*** Botao Enter Precionado no 4 ***", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});

	}

}