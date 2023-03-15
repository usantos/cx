package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;

public class CopiaColaPixFragment extends Fragment {

	private View view;

	public CopiaColaPixFragment() {}

	public static CopiaColaPixFragment newInstance() {
		CopiaColaPixFragment fragment = new CopiaColaPixFragment();
		Bundle               args     = new Bundle();

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_copia_cola_pix, container, false);

		return view;
	}

}