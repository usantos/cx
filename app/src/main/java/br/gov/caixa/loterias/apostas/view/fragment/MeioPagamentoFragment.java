package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeioPagamentoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeioPagamentoFragment extends Fragment {
	private static final String ARG_CONFIG = "ARG_CONFIG";

	private MeioPagamentoConfig config;
	private View view;
	private View margem;
	private AppCompatImageView imagem;
	private ConstraintLayout btnAdd;

	public MeioPagamentoFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param config Parameter 1.
	 * @return A new instance of fragment MeioPagamentoFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MeioPagamentoFragment newInstance(MeioPagamentoConfig config) {
		MeioPagamentoFragment fragment = new MeioPagamentoFragment();
		Bundle                   args     = new Bundle();
		if(config != null){
			args.putSerializable(ARG_CONFIG, config);
		}
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			config  = (MeioPagamentoConfig) getArguments().getSerializable(ARG_CONFIG);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_meio_pagamento, container, false);
		margem = view.findViewById(R.id.margem);
		imagem = view.findViewById(R.id.imagemMeioPagamento);
		btnAdd = view.findViewById(R.id.addCartao);

		aplicaConfig();

		configButton();

		return view;
	}

	private void configButton() {
		btnAdd.setOnClickListener(v -> {
			TestVisao.toast("Implementar no futuro!");
		});
	}

	private void aplicaConfig() {
		if (config != null){
			margem.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
			btnAdd.setBackground(getActivity().getDrawable(config.getIdCorBarra()));
			btnAdd.getBackground().setAlpha(20);
			imagem.setBackground(getActivity().getDrawable(config.getIdImagem()));

		}
	}
}