package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;

public class EscolhaChavePixResgateFragment extends Fragment {
	private TextView txtChaveCPF;
	private EscolhaChavePixResgateFragment() {}

	public static EscolhaChavePixResgateFragment newInstance() {
		EscolhaChavePixResgateFragment fragment = new EscolhaChavePixResgateFragment();
		Bundle               args     = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_escolha_chave_pix_resgate, container, false);
		txtChaveCPF = view.findViewById(R.id.txtValorChave);

		txtChaveCPF.setText(DadosUsuarioBO.obterCpf());
		return view;
	}

}