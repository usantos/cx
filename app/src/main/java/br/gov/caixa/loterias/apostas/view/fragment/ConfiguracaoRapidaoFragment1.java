package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracaoRapidaoFragment1 extends Fragment {


	public ConfiguracaoRapidaoFragment1() { }

	public static ConfiguracaoRapidaoFragment1 newInstance() {
		ConfiguracaoRapidaoFragment1 fragment = new ConfiguracaoRapidaoFragment1();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_configuracao_rapidao_fragment1, container, false);
	}

}
