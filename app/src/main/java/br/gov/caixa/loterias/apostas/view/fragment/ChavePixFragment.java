package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;

public class ChavePixFragment extends Fragment {
	private static final String ARG_TELA= "ARG_TELA";
	public static final String TEXTO_CPF = "CPF";
	public static final String TEXTO_EMAIL = "E-mail";
	public static final String TEXTO_CELULAR = "Celular";
	public static final String TEXTO_CHAVE_ALEATORIA = "Chave Aleatória";

	private View view;

	private String texto = "";

	public ChavePixFragment() {}

	public static ChavePixFragment newInstance(String texto) {
		ChavePixFragment fragment = new ChavePixFragment();
		Bundle          args     = new Bundle();

		args.putString(ARG_TELA, texto);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			texto = getArguments().getString(ARG_TELA);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chave_pix, container, false);

		TextView tvTexto = view.findViewById(R.id.chavePix);
		tvTexto.setText(texto);

		startFragment();

		return view;
	}

	private void startFragment() {

		//FragmentUtils.startChavePix(getActivity().getSupportFragmentManager(),R.id.chavePixCPF, texto);

	}

}