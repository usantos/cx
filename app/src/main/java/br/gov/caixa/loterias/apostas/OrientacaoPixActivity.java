package br.gov.caixa.loterias.apostas;

import android.os.Bundle;

import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;

public class OrientacaoPixActivity extends LoteriasAppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orientacao_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		startFragment();
	}

	private void startFragment() {
		 FragmentUtils.startCardPix(getSupportFragmentManager(), R.id.fragmentOrientacaoPix, CardPixFragment.TELA_ORIENTACAO);
	}

}