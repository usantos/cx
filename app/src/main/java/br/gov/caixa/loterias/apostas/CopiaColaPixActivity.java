package br.gov.caixa.loterias.apostas;

import android.os.Bundle;

import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;

public class CopiaColaPixActivity extends LoteriasAppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copia_cola_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		startFragment();
	}

	private void startFragment() {
		FragmentUtils.startCardPix(getSupportFragmentManager(), R.id.fragmentCopiaColaPix, CardPixFragment.TELA_COPIA_COLA);
	}
}