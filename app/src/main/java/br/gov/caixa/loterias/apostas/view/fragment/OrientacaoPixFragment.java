package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;

public class OrientacaoPixFragment extends Fragment {
	private static final String ARG_CONFIG = "ARG_CONFIG";

	private View view;

	private MeioPagamentoFragment fragmentPix;

	public OrientacaoPixFragment() {}

	public static OrientacaoPixFragment newInstance() {
		OrientacaoPixFragment fragment = new OrientacaoPixFragment();
		Bundle                args     = new Bundle();
//		if(config != null){
//			args.putSerializable(ARG_CONFIG, config);
//		}

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
//			config  = (MeioPagamentoConfig) getArguments().getSerializable(ARG_CONFIG);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_orientacao_pix, container, false);

		startFragmentPix();

		return view;
	}

	private void startFragmentPix() {
		fragmentPix = FragmentUtils.startMeioPagamento(getActivity().getSupportFragmentManager(),
													   R.id.fragmentPix,
													   MeioPagamentoUtils.getConfigPix(false));
	}

}