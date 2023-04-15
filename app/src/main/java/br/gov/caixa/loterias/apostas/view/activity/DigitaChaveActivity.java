package br.gov.caixa.loterias.apostas.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.CelularMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.custom.ChaveAleatoriaMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.custom.EmailMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class DigitaChaveActivity extends LoteriasAppActivity {
	public static final String CHAVE_ARG = "CHAVE_ARG";
	public static final String VALOR_ARG = "VALOR_ARG";
	private static final int CELULAR_LENGTH = 16;

	private ChavePixEnum chave;
	private BigDecimal valor;

	private TextView txtValor, txtDescricao;
	private EditText edtChave;
	private Button btnConfirmar, btnTrocarChave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_digita_chave);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Resgate Loteria"));
		getExtras();

		txtValor = findViewById(R.id.premio_valor);
		txtDescricao = findViewById(R.id.txt_informe_chave);
		edtChave = findViewById(R.id.edt_chave);
		btnConfirmar = findViewById(R.id.btn_confimar);
		btnTrocarChave = findViewById(R.id.btn_trocar_chave);


		setTextViews();
		setEditType();
		setValidacao();
		setListeners();
	}

	private void setListeners() {
		btnTrocarChave.setOnClickListener((v) -> { finish();});
		btnConfirmar.setOnClickListener((v) -> {
			TestVisao.toast("Vamos fazer a tela de confirmacao ainda!");
		});
		desabiliaBotaoConfirmar();
	}

	private void desabiliaBotaoConfirmar() {
		btnConfirmar.setEnabled(false);
		btnConfirmar.setBackgroundColor(getResources().getColor(R.color.cinza_pix_disabled));
	}

	private void setValidacao() {
		switch (chave){
			case EMAIL:
				edtChave.addTextChangedListener(new EmailMaskTextWatcher(edtChave, onTextWatchListener()));
				break;
			case CELULAR:
				edtChave.addTextChangedListener(new CelularMaskTextWatcher(edtChave, CELULAR_LENGTH, onTextWatchListener()));
				break;
			case CHAVE_ALEATORIA:
				edtChave.addTextChangedListener(new ChaveAleatoriaMaskTextWatcher(edtChave, onTextWatchListener()));
				break;
		}
	}

	private TextWatcherListener onTextWatchListener() {
		return isValid -> {
			if (isValid){
				habilitaBotaoConfirmar();
			} else {
				desabiliaBotaoConfirmar();
			}
		};
	}

	private void habilitaBotaoConfirmar() {
		btnConfirmar.setEnabled(true);
		btnConfirmar.setBackgroundColor(getResources().getColor(R.color.verdeazul));
	}

	private void setTextViews() {
		ViewUtils.setMoedaFormatHtml(valor,txtValor);

		if (chave == ChavePixEnum.CHAVE_ALEATORIA){
			txtDescricao.setText("Cole a sua Chave Aleatória");
		}

		((TextInputLayout)findViewById(R.id.labelChave)).setHint(chave.getTexto());
		if (chave == ChavePixEnum.CELULAR){
			findViewById(R.id.region_phone).setVisibility(View.VISIBLE);
		}
	}

	private void setEditType() {
		switch (chave){
			case EMAIL:
				edtChave.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				break;
			case CELULAR:
				edtChave.setInputType(InputType.TYPE_CLASS_NUMBER);
				InputFilter[] fArray = new InputFilter[1];
				fArray[0] = new InputFilter.LengthFilter(CELULAR_LENGTH);
				edtChave.setFilters(fArray);
				break;
			case CHAVE_ALEATORIA:
				edtChave.setTextIsSelectable(false);
				edtChave.setFocusable(false);
				break;
		}
	}

	private void getExtras() {
		if(getIntent() != null){
			chave = (ChavePixEnum) getIntent().getSerializableExtra(CHAVE_ARG);
			valor = new BigDecimal(getIntent().getStringExtra(VALOR_ARG));
		}
	}
}