package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.OrientacaoPix;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.OrientacaoPixRecyclerView;

public class OrientacaoPixFragment extends Fragment {
	private static final String ARG_CONFIG = "ARG_CONFIG";

	private View view;
	private RecyclerView listView;

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
		listView = view.findViewById(R.id.lista_orientacao);
		startFragmentPix();
		startListaOrientacao();

		return view;
	}

	private void startListaOrientacao() {
		OrientacaoPixRecyclerView adapter = new OrientacaoPixRecyclerView(getOrientacaoList());
		listView.setLayoutManager(new LinearLayoutManager(getContext()));
		listView.setAdapter(adapter);
	}

	private List<OrientacaoPix> getOrientacaoList() {
		List<OrientacaoPix> lista = new ArrayList();
		String[] stringArray = getResources().getStringArray(R.array.orientacao_pix_list);
		for (int i = 0; i < stringArray.length; i++) {
			lista.add(new OrientacaoPix(String.valueOf(i+1),stringArray[i]));
		}
		return lista;
	}

	private void startFragmentPix() {
		fragmentPix = FragmentUtils.startMeioPagamento(getActivity().getSupportFragmentManager(),
													   R.id.fragmentPix,
													   MeioPagamentoUtils.getConfigPix(false));
	}

}