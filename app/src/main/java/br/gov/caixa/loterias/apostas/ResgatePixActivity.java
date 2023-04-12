package br.gov.caixa.loterias.apostas;

import android.os.Bundle;

import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ChavePixFragment;

public class ResgatePixActivity extends LoteriasAppActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resgate_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resgate_pix)));

		startFragments();
	}

	private void startFragments() {
		FragmentUtils.startCardPix(getSupportFragmentManager(), R.id.fragmentResgatePix, CardPixFragment.TELA_RESGATE);
		FragmentUtils.startChavePix(getSupportFragmentManager(), R.id.fragmentChavePix2, ChavePixFragment.TEXTO_CPF);
		FragmentUtils.startChavePix(getSupportFragmentManager(), R.id.fragmentChaveEmail, ChavePixFragment.TEXTO_EMAIL);
		FragmentUtils.startChavePix(getSupportFragmentManager(), R.id.fragmentChaveCelular, ChavePixFragment.TEXTO_CELULAR);
		FragmentUtils.startChavePix(getSupportFragmentManager(), R.id.fragmentChaveAleatoria, ChavePixFragment.TEXTO_CHAVE_ALEATORIA);
	}

}