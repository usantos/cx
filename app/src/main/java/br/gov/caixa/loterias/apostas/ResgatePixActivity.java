package br.gov.caixa.loterias.apostas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.model.ResgatePixModel;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.EscolhaChavePixResgateFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnEscolhaChavePisResgateListener;

public class ResgatePixActivity extends LoteriasAppActivity implements OnEscolhaChavePisResgateListener {
	public static final String ARG_VALOR = "ARG_VALOR";
	private ResgatePixModel model;
	private BigDecimal valor;
	private TextView txtValor;
	private AppCompatButton btnCancelar, btnContinuar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resgate_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resgate_pix)));

		model = new ResgatePixModel(this);
		getExtras();
		setViews();
		setListeners();

		txtValor.setText(ViewUtils.getMoedaFormat(valor));

		startFragment();
	}

	private void setListeners() {
		btnCancelar.setOnClickListener(v -> onBackPressed());
		btnContinuar.setOnClickListener(v -> {

		});
	}

	private void startFragment() {
		FragmentUtils.startFragmentAllowingStateLoss(getSupportFragmentManager(), R.id.containerFragment, EscolhaChavePixResgateFragment.newInstance());
	}

	private void getExtras() {
		valor = (BigDecimal) getIntent().getSerializableExtra(ARG_VALOR);
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