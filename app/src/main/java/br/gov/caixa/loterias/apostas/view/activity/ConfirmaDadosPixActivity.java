package br.gov.caixa.loterias.apostas.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.LoteriasAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoResgatePixActivity;
import br.gov.caixa.loterias.apostas.utils.ChavePixEnum;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.PixUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.ChavePixFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;

public class ConfirmaDadosPixActivity extends LoteriasAppActivity {
	public static final String NOME_ARG = "NOME_ARG";
	public static final String CPF_ARG = "CPF_ARG";
	public static final String INSTITUICAO_ARG = "INSTITUICAO_ARG";

	private TextView txtNome, txtCPF, txtInstituicao;
	private Button btnConfirmar, btnTrocar;

	private String instituicao;
	private String cpf;
	private String nome;

	private static boolean isValid = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirma_dados_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Resgate Loteria"));
		getExtras();

		txtNome 		= findViewById(R.id.txt_nome);
		txtCPF 			= findViewById(R.id.txt_cpf);
		txtInstituicao 	= findViewById(R.id.txt_intituicao);
		btnConfirmar	= findViewById(R.id.btn_confimar);
		btnTrocar		= findViewById(R.id.btn_trocar_chave);

		setTextViews();
		setListeners();
		isValid = false;
	}

	private void setListeners() {
		btnTrocar.setOnClickListener((v) -> {
			trocarChave();
		});
		btnConfirmar.setOnClickListener((v) -> {
			if (isValid){
				Bundle bundle = new Bundle();
				bundle.putLong(getResources().getString(R.string.id_aposta), 16399l);
				bundle.putLong(ConfirmacaoPagamentoActivity.ARG_FLUXO, PixUtils.FLUXO_RESGATE);
				bundle.putLong(ConfirmacaoPagamentoActivity.ARG_ORIGEM, MeioPagamentoUtils.PIX);
				startActivity(IntentUtil.getIntentOrigemDestino(ConfirmaDadosPixActivity.this,
																ConfirmacaoPagamentoActivity.class, bundle));
			} else {
				isValid = true;
				DialogUtils.showDialogDefault(ConfirmaDadosPixActivity.this, 0, getString(R.string.resgate_nao_autorizado),
											  getString(R.string.chave_sem_vinculo),
											  getString(R.string.trocar_chave), getString(R.string.trocar_forma_resgate), onChaveInvalidaListener());
			}
		});
	}

	private void trocarChave() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(DigitaChaveActivity.ARG_TROCAR_CHAVE, "");
		setResult(Activity.RESULT_CANCELED, returnIntent);
		finish();
	}

	private void trocarResgate(){
		Intent returnIntent = new Intent();
		returnIntent.putExtra(ChavePixFragment.ARG_TROCAR_RESGATE, "");
		setResult(Activity.RESULT_CANCELED, returnIntent);
		finish();
	}

	private OnDialogListenerLeftRight onChaveInvalidaListener() {
		return new OnDialogListenerLeftRight() {
			@Override
			public void left() {
				trocarChave();
			}

			@Override
			public void right() {
				trocarResgate();
			}
		};
	}

	private void setTextViews() {
		txtNome.setText(StringUtils.capitalizer(nome));
		txtCPF.setText(cpf);
		txtInstituicao.setText(instituicao);
	}

	private void getExtras() {
		if(getIntent() != null){
			nome =  getIntent().getStringExtra(NOME_ARG);
			cpf = getIntent().getStringExtra(CPF_ARG);
			instituicao = getIntent().getStringExtra(INSTITUICAO_ARG);
		}
	}
}