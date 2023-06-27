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
		//showKeyBoard(cvv1);

		//Digitou muda o foco para proximo
		mudaFocoProximo(cvv1, cvv2);
		mudaFocoProximo(cvv2, cvv3);
		if (isAmex) {
			cvv4.setVisibility(View.VISIBLE);
			mudaFocoProximo(cvv3, cvv4);
		}
		//Digitou Enter
		pressEnter(cvv1);
		pressEnter(cvv2);
		pressEnter(cvv3);
		pressEnter(cvv4);
	}

	private void mudaFocoProximo(EditText currentEditText, EditText nextEditText) {
		currentEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (currentEditText.getText().toString().length() == 1) { //size as per your requirement
					nextEditText.requestFocus();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});
	}


	private void pressEnter(EditText currentEditText) {
		currentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Toast.makeText(getApplicationContext(), "*** Botao Enter Precionado ***", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
		});
	}

//	private void showKeyBoard(EditText currentEditText) {
//		currentEditText.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				InputMethodManager imm = (InputMethodManager) getSystemService(DigitarCvvActivity.INPUT_METHOD_SERVICE);
//				imm.showSoftInput(currentEditText, InputMethodManager.SHOW_FORCED);
//			}
//		},200);
//	}
//
//	private void hideKeyBoard() {
//		View view = this.getCurrentFocus();
//		if (view != null) {
//			InputMethodManager imm = (InputMethodManager) getSystemService(DigitarCvvActivity.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//		}
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		hideKeyBoard();
//	}

}