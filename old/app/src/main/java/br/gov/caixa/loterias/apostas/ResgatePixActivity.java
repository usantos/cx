package br.gov.caixa.loterias.apostas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.VolleyError;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoResgatePixActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DadosChavePixDTO;
import br.gov.caixa.loterias.apostas.model.model.ResgatePixModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.EscolhaChavePixResgateFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolhaChavePisResgateListener;

public class ResgatePixActivity extends LoteriasAppActivity implements OnEscolhaChavePisResgateListener {
	public static final String ARG_VALOR = "ARG_VALOR";
	public static final String ARG_ID_APOSTA = "ARG_ID_APOSTA";
	private ResgatePixModel model;
	private BigDecimal valor;
	private String idAposta;
	private TextView txtValor;
	private AppCompatButton btnCancelar, btnContinuar;
	private ETAPA etapa = ETAPA.ESCOLHA_CHAVE;
	private EscolhaChavePixResgateFragment escolhaChavePixResgateFragment;
	private DadosChavePixDTO dadosChavePix;

	private enum ETAPA {
		ESCOLHA_CHAVE, CONFIRMACAO
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resgate_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.title_activity_resgate_pix)));

		model = new ResgatePixModel(this);
		getExtras();
		setViews();
		setListeners();

		txtValor.setText(ViewUtils.getMoedaFormat(valor));

		startFragment();
	}

	private void setListeners() {
		btnCancelar.setOnClickListener(v -> handleCancelar());
		btnContinuar.setOnClickListener(v -> handleContinuar());
	}

	private void handleContinuar() {
		switch (etapa){
			case ESCOLHA_CHAVE:
				AlertDialogUtils.show(this);
				model.consultaDadosChave(idAposta, getChavePIX(), onConsultaDadosListener());
				break;
			case CONFIRMACAO:
				AlertDialogUtils.show(this);
				model.resgatarPremio(idAposta, getChavePIX(), onResgatePixListener());
		}
	}

	private OnSilceListener onResgatePixListener() {
		return new OnSilceListener() {
			@Override
			public void success(Object payload) {
				AlertDialogUtils.dismiss();
				startActivity(IntentUtil.getIntentOrigemDestino(ResgatePixActivity.this, ConfirmacaoResgatePixActivity.class));
				finish();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void handleCancelar() {
		switch (etapa){
			case ESCOLHA_CHAVE:
				onBackPressed();
				break;
			case CONFIRMACAO:
				AlertDialogUtils.show(this);
				model.cancelarPix(idAposta, onCancelaPixListener());
		}
	}

	private OnSilceListener<DadosChavePixDTO> onCancelaPixListener() {
		return new OnSilceListener<DadosChavePixDTO>() {
			@Override
			public void success(DadosChavePixDTO payload) {
				AlertDialogUtils.dismiss();
				onBackPressed();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private String getChavePIX() {
		return escolhaChavePixResgateFragment.getChavePIX();
	}

	private OnSilceListener<DadosChavePixDTO> onConsultaDadosListener() {
		return new OnSilceListener<DadosChavePixDTO>() {
			@Override
			public void success(DadosChavePixDTO payload) {
				AlertDialogUtils.dismiss();
				etapa = ETAPA.CONFIRMACAO;
				btnContinuar.setText(R.string.resgatar);
				dadosChavePix = payload;
				FragmentUtils.startConfirmacaoChavePixResgate(getSupportFragmentManager(), R.id.containerFragment, dadosChavePix);
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void startFragment() {
		escolhaChavePixResgateFragment = (EscolhaChavePixResgateFragment) FragmentUtils.startFragmentAllowingStateLoss(getSupportFragmentManager(), R.id.containerFragment, EscolhaChavePixResgateFragment.newInstance());
	}

	private void getExtras() {
		valor = (BigDecimal) getIntent().getSerializableExtra(ARG_VALOR);
		idAposta = ((Long) getIntent().getSerializableExtra(ARG_ID_APOSTA)).toString();
	}

	private void setViews() {
		txtValor 		= findViewById(R.id.txtValorDoCredito);
		btnCancelar		= findViewById(R.id.btnCancelar);
		btnContinuar		= findViewById(R.id.btnContinuar);
	}

	@Override
	public void habilitaContinuar(boolean show) {
		btnContinuar.setEnabled(show);
		if (show){
			btnContinuar.setAlpha(1.0f);
		} else {
			btnContinuar.setAlpha(0.3f);
		}
	}
}