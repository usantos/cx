package br.gov.caixa.loterias.apostas.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeusCartoesConfig;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoVersoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.ChavePixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CopiaColaPixFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.MeusCartoesFragment;
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

	public static SomadorCarrinhoFragment startSomadorCarrinhoFragment(FragmentManager fm,SomadorCarrinhoFragment fragment, int id, Boolean irCarrinho) {
		if(fragment == null){
			fragment = SomadorCarrinhoFragment.newInstance(CarrinhoSingleton.getInstance().getCarrinho(), irCarrinho);
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
		MeioPagamentoFragment fragment = MeioPagamentoFragment.newInstance(config);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CardPixFragment startCardPix(FragmentManager fm, int id, int tela) {
		CardPixFragment fragment = CardPixFragment.newOrientacaoInstance(tela);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CartaoCreditoFragment startCartaoCredito(FragmentManager fm, int id, CartaoCreditoConfig config) {
		CartaoCreditoFragment fragment = CartaoCreditoFragment.newInstance(config);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CardPixFragment startCardOrientacaoPix(FragmentManager fm, int id) {
		CardPixFragment fragment = CardPixFragment.newOrientacaoInstance(CardPixFragment.TELA_ORIENTACAO);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CartaoCreditoVersoFragment startCartaoCreditoVerso(FragmentManager fm, int id, String metodo, String cvv) {
		CartaoCreditoVersoFragment fragment = CartaoCreditoVersoFragment.newInstance(metodo,cvv);
		FragmentUtils.startFragment(fm, id,fragment);
		return fragment;
	}

	public static CardPixFragment startCardCopiaColaPix(GerarPixDTO pixDTO, FragmentManager fm, int id) {
		CardPixFragment fragment = CardPixFragment.newCopiaColaInstance(pixDTO, CardPixFragment.TELA_COPIA_COLA);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static OrientacaoPixFragment startOrientacaoPix(FragmentManager fm, int id) {
		OrientacaoPixFragment fragment = OrientacaoPixFragment.newInstance();
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static CopiaColaPixFragment startCopiaColaPix(GerarPixDTO pixDTO, FragmentManager fm, int id) {
		CopiaColaPixFragment fragment = CopiaColaPixFragment.newInstance(pixDTO);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}

	public static ChavePixFragment startChavePix(FragmentManager fm, int id, ChavePixEnum chave) {
		ChavePixFragment fragment = ChavePixFragment.newInstance(chave);
		FragmentUtils.startFragment(fm, id, fragment);
		return fragment;
	}
}
