package br.gov.caixa.loterias.apostas.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.CelularMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.custom.ChaveAleatoriaMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.custom.EmailMaskTextWatcher;
import br.gov.caixa.loterias.apostas.view.fragment.ChavePixFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;
import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class DigitaChaveActivity extends LoteriasAppActivity {
	public static final String CHAVE_ARG = "CHAVE_ARG";
	public static final String VALOR_ARG = "VALOR_ARG";

	public static final String ARG_TROCAR_CHAVE = "ARG_TROCAR_CHAVE";
	private static final int TROCA_CHAVE_PIX = 321;

	private static final int CELULAR_LENGTH = 16;

	private ChavePixEnum chave;
	private BigDecimal valor;

	private TextView txtValor, txtDescricao;
	private EditText edtChave;
	private Button btnConfirmar, btnTrocarChave;

	private static boolean isValid = false;

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
		isValid = false;
	}

	private void setListeners() {
		btnTrocarChave.setOnClickListener((v) ->  finish() );
		btnConfirmar.setOnClickListener((v) -> {
			if (isValid){
				Intent intent = new Intent(DigitaChaveActivity.this, ConfirmaDadosPixActivity.class);
				intent.putExtra(ConfirmaDadosPixActivity.NOME_ARG, DadosUsuarioBO.obterNome());
				intent.putExtra(ConfirmaDadosPixActivity.CPF_ARG, "***.345.678-***");
				intent.putExtra(ConfirmaDadosPixActivity.INSTITUICAO_ARG, "Caixa Econômica Federal");
				intent.putExtra(ConfirmaDadosPixActivity.TIPO_CHAVE_ARG, chave);
				intent.putExtra(ConfirmaDadosPixActivity.VALOR_CHAVE_ARG, edtChave.getText().toString());
				intent.putExtra(ConfirmaDadosPixActivity.SALVAR_CHAVE_ARG, true);
				startActivityForResult(intent, TROCA_CHAVE_PIX);
			} else {
				isValid = true;
				DialogUtils.showDialogDefault(DigitaChaveActivity.this, 0,"Chave Inválida",
											  "A cahve digitada não está vinculada a uma conta ativa",
											  getString(R.string.trocar_chave), getString(R.string.trocar_forma_resgate), onChaveInvalidaListener());
			}
		});
		desabiliaBotaoConfirmar();
	}

	private OnDialogListenerLeftRight onChaveInvalidaListener() {
		return new OnDialogListenerLeftRight() {
			@Override
			public void left() {
				finish();
			}

			@Override
			public void right() {
				Intent returnIntent = new Intent();
				returnIntent.putExtra(ChavePixFragment.ARG_TROCAR_RESGATE, "");
				setResult(Activity.RESULT_CANCELED, returnIntent);
				finish();
			}
		};
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
				edtChave.addTextChangedListener(new CelularMaskTextWatcher(this, edtChave, CELULAR_LENGTH, onTextWatchListener()));
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TROCA_CHAVE_PIX) {
			if (resultCode == Activity.RESULT_CANCELED ) {
				if (data != null){
					if (data.getStringExtra(ChavePixFragment.ARG_TROCAR_RESGATE) != null){
						Intent returnIntent = new Intent();
						returnIntent.putExtra(ChavePixFragment.ARG_TROCAR_RESGATE, "");
						setResult(Activity.RESULT_CANCELED, returnIntent);
						finish();
					} else {
						finish();
					}
				}
			}
		}
	}
}