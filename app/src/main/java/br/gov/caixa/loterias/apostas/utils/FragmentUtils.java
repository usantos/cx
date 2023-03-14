package br.gov.caixa.loterias.apostas.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeusCartoesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.OrientacaoPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.PaginacaoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

public class FragmentUtils {

	public static void startFragment(FragmentManager fm, int id, Fragment fragment){
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(id, fragment);
		fragmentTransaction.commit();
	}

	public static SomadorCarrinhoFragment startSomadorCarrinhoFragment(FragmentManager fm,SomadorCarrinhoFragment fragment, int id, String TAG) {
		if(fragment == null){
			fragment = SomadorCarrinhoFragment.newInstance(CarrinhoSingleton.getInstance().getCarrinho(), TAG);
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}

	public static MeusCartoesFragment meusCartoesFragment(FragmentManager fm, int id, List<RetornoCartao> cartoes, MeusCartoesConfig config) {
		MeusCartoesFragment fragment = MeusCartoesFragment.newInstance(cartoes, config);
		FragmentUtils.startFragment(fm, id, fragment);

		return fragment;

	}

	public static PaginacaoFragment startPaginacaoFragment(FragmentManager fm, PaginacaoFragment fragment, int id) {
		if(fragment == null){
			fragment = PaginacaoFragment.newInstance();
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}

	public static MeioPagamentoFragment startMeioPagamento(FragmentManager fm, int id, MeioPagamentoConfig config) {
		MeioPagamentoFragment fragment = null;
		if(fragment == null){
			fragment = MeioPagamentoFragment.newInstance(config);
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}

	public static CardPixFragment startCardOrientacaoPix(FragmentManager fm, int id) {
		CardPixFragment fragment = null;
		if(fragment == null){
			fragment = CardPixFragment.newInstance(PixUtils.TELA_ORIENTACAO_PIX);
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}

	public static OrientacaoPixFragment startOrientacaoPix(FragmentManager fm, int id) {
		OrientacaoPixFragment fragment = null;
		if(fragment == null){
			fragment = OrientacaoPixFragment.newInstance();
			FragmentUtils.startFragment(fm, id, fragment);
		}
		return fragment;
	}
}
