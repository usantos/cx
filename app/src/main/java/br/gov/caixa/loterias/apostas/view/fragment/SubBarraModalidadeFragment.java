package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;


public class SubBarraModalidadeFragment extends Fragment {
	private static final String ARG_MODALIDADE = "ARG_MODALIDADE";
	private static final String ARG_CONCURSO = "ARG_CONCURSO";
	private static final String ARG_SORTEIO = "ARG_SORTEIO";

	private  View view;
	private TextView tvModalidade, tvConcurso, tvSorteio;

	private String concurso;
	private String sorteio;
	private ModalidadeEnum modalidade;

	public SubBarraModalidadeFragment() {
		// Required empty public constructor
	}

	public static SubBarraModalidadeFragment newInstance(ModalidadeEnum modalidade, String concuso,
														 String sorteio) {
		SubBarraModalidadeFragment fragment = new SubBarraModalidadeFragment();
		Bundle                  args     = new Bundle();
		args.putString(ARG_MODALIDADE, new Gson().toJson(modalidade));
		args.putSerializable(ARG_CONCURSO, concuso);
		args.putSerializable(ARG_SORTEIO, sorteio);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			concurso = getArguments().getString(ARG_CONCURSO);
			sorteio = getArguments().getString(ARG_SORTEIO);
			modalidade = new Gson().fromJson(getArguments().getString(ARG_MODALIDADE), ModalidadeEnum.class);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_sub_barra_modalidade, container, false);

		tvModalidade = view.findViewById(R.id.id_modalidade);
		tvConcurso = view.findViewById(R.id.id_concurso);
		tvSorteio = view.findViewById(R.id.id_sorteio);

		preencheDados();

		return view;
	}

	private void preencheDados() {
		tvModalidade.setText(ModalidadeEnum.fromString(modalidade));
		tvConcurso.setText(concurso);
		tvSorteio.setText(sorteio);
	}
}