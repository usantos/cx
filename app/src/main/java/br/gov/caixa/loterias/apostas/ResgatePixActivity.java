package br.gov.caixa.loterias.apostas;

import android.os.Bundle;
import android.widget.TextView;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.model.ResgatePixModel;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class ResgatePixActivity extends LoteriasAppActivity {
	public static final String ARG_VALOR = "ARG_VALOR";
	private ResgatePixModel model;
	private BigDecimal valor;
	private TextView txtValor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resgate_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resgate_pix)));

		model = new ResgatePixModel(this);
		getExtras();
		setViews();

		txtValor.setText(ViewUtils.getMoedaFormat(valor));
	}

	private void getExtras() {
		valor = (BigDecimal) getIntent().getSerializableExtra(ARG_VALOR);
	}

	private void setViews() {
		txtValor = findViewById(R.id.txtValorDoCredito);
	}

}