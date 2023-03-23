package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.utils.BuildVersionUtil;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;

public class CopiaColaPixActivity extends LoteriasAppActivity {

	private ConstraintLayout layoutSimular;

	FloatingActionButton btnCompraSucesso, btnCompraCPFDivergente;
	ExtendedFloatingActionButton btnSimularCompra;
	TextView txtCompraSucesso, txtCompraCPFDivergente;

	private boolean isAllFabsVisible = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copia_cola_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		aplicaLogicaSimulacao();

		checaVersao();

		startFragment();
	}

	private void aplicaLogicaSimulacao() {
		layoutSimular = findViewById(R.id.layoutSimular);
		btnSimularCompra = findViewById(R.id.btnSimularCompra);
		btnCompraSucesso = findViewById(R.id.btnCompraSucesso);
		btnCompraCPFDivergente = findViewById(R.id.btnCompraCPFDivergente);
		txtCompraSucesso = findViewById(R.id.txtCompraSucesso);
		txtCompraCPFDivergente = findViewById(R.id.txtCompraDivergente);

		btnSimularCompra.shrink();
		btnSimularCompra.setOnClickListener(v -> {
			if (!isAllFabsVisible) {
				btnCompraSucesso.show();
				btnCompraCPFDivergente.show();
				txtCompraSucesso.setVisibility(View.VISIBLE);
				txtCompraCPFDivergente.setVisibility(View.VISIBLE);

				btnSimularCompra.extend();
				isAllFabsVisible = true;
			} else {
				btnCompraSucesso.hide();
				btnCompraCPFDivergente.hide();
				txtCompraSucesso.setVisibility(View.GONE);
				txtCompraCPFDivergente.setVisibility(View.GONE);

				btnSimularCompra.shrink();
				isAllFabsVisible = false;
			}
		});

		btnCompraSucesso.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putLong(ConfirmacaoPagamentoActivity.ARG_ORIGEM, MeioPagamentoUtils.PIX);

			Intent intent = IntentUtil.getIntentOrigemDestino(CopiaColaPixActivity.this, ConfirmacaoPagamentoActivity.class, bundle);
			startActivity(intent);
		});

		btnCompraCPFDivergente.setOnClickListener(v -> {
			TestVisao.toast("Implementando o popup");
		});
	}

	private void checaVersao() {
		if (BuildVersionUtil.isTQS()){
			layoutSimular.setVisibility(View.VISIBLE);
		}
	}

	private void startFragment() {
		FragmentUtils.startCardPix(getSupportFragmentManager(), R.id.fragmentCopiaColaPix, CardPixFragment.TELA_COPIA_COLA);
	}
}