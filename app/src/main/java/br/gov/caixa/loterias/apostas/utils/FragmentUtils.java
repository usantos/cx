package br.gov.caixa.loterias.apostas.utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

public class FragmentUtils {

	public static void startFragment(FragmentManager fm, int id, Fragment fragment){
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(id, fragment);
		fragmentTransaction.commit();
	}

	public static void startSomadorCarrinhoFragment(FragmentManager fm,SomadorCarrinhoFragment fragment, int id, String TAG) {
		if(fragment == null){
			fragment = SomadorCarrinhoFragment.newInstance(CarrinhoSingleton.getInstance().getCarrinho(), TAG);
			FragmentUtils.startFragment(fm, id, fragment);
		}

	}
}
