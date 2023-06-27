package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class DigitarCvvActivity extends AppCompatActivity {

	public static final String CARTAO = "CARTAO";

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private RetornoCartao cartao;
	private Button btnIncluirCodigo;

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

	private void configEditTexto() {
		EditText cvv1 = findViewById(R.id.cvv_digit1);
		EditText cvv2 = findViewById(R.id.cvv_digit2);
		EditText cvv3 = findViewById(R.id.cvv_digit3);

		cvv1.requestFocus();
//		// Mostra o teclado
//		InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//		imm.showSoftInput(cvv1, InputMethodManager.SHOW_IMPLICIT);

//		EditText editText1 = findViewById(R.id.cvv_digit1);
//		editText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_DONE) {
//					// O botão 'Enter' foi pressionado, você pode realizar a ação aqui
//					// Por exemplo, mover o foco para o próximo EditText
//					EditText editText2 = findViewById(R.id.cvv_digit2);
//					editText2.requestFocus();
//					return true;
//				}
//				return false;
//			}
//		});

		cvv1.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (cvv1.getText().toString().length() == 1) { //size as per your requirement
					cvv2.requestFocus();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});

		cvv2.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (cvv2.getText().toString().length() == 1) { //size as per your requirement
					cvv3.requestFocus();
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});

    }

//	private void configButton() {
//		btnIncluirCodigo = findViewById(R.id.btnIncluirCodigo);
//		btnIncluirCodigo.setOnClickListener(v -> abrirTermosUso());
//		btnIncluirCodigo.setOnClickListener(v -> {
//			Intent intent = IntentUtil.getIntentOrigemDestino(this, DigitarCvvActivity.class);
//			//intent.putExtra(ConfirmarCartaoActivity.CARTAO, cartao);
//			startActivity(intent);
//		});
//	}

}