package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;

public class CardPixFragment extends Fragment {
	private static final String ARG_TELA= "ARG_TELA";
	public static final int TELA_ORIENTACAO = 1;
	public static final int TELA_COPIA_COLA = 2;

	private View view;

	private int numeroTela = 0;

	public CardPixFragment() {}

	public static CardPixFragment newInstance(int tela) {
		CardPixFragment fragment = new CardPixFragment();
		Bundle          args     = new Bundle();

		args.putInt(ARG_TELA, tela);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			numeroTela = getArguments().getInt(ARG_TELA);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_card_pix, container, false);

		view.findViewById(R.id.contuedo).setBackgroundColor(getResources().getColor(R.color.verde_pix));

		startFragment();

		return view;
	}

	private void startFragment() {
		if (isTela(TELA_ORIENTACAO)){
			view.findViewById(R.id.valor_pix).setVisibility(View.GONE);
			FragmentUtils.startOrientacaoPix(getActivity().getSupportFragmentManager(),R.id.corpo);
		} else if (isTela(TELA_COPIA_COLA)){
			FragmentUtils.startCopiaColaPix(getActivity().getSupportFragmentManager(), R.id.corpo);
		}
	}

	private boolean isTela(int tela) {
		return numeroTela == tela;
	}
}