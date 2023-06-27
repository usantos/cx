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

//        // Se desejar, você pode adicionar um TextWatcher ao terceiro campo para retornar o foco para o primeiro campo
//        cvv3.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (cvv3.getText().toString().length() == 1) { //size as per your requirement
//                    cvv1.requestFocus();
//                }
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void afterTextChanged(Editable s) {}
//        });
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