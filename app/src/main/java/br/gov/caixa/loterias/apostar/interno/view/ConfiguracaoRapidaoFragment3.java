package br.gov.caixa.loterias.apostar.interno.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.gov.caixa.loterias.apostar.interno.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracaoRapidaoFragment3 extends Fragment {


	public ConfiguracaoRapidaoFragment3() {
	}

	public static ConfiguracaoRapidaoFragment3 newInstance() {
		ConfiguracaoRapidaoFragment3 fragment = new ConfiguracaoRapidaoFragment3();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment3, container, false);
	}

}
