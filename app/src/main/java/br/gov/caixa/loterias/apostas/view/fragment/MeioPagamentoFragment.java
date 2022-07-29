package br.gov.caixa.loterias.apostas.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CartoesMeioPagamentoRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.listener.OnCartaoMeioPagamentoListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeioPagamentoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeioPagamentoFragment extends Fragment {
	private static final String ARG_CONFIG = "ARG_CONFIG";
	private static final String ARG_CARTOES = "ARG_CARTOES";

	private MeioPagamentoConfig config;
	private View view;
	private View margem;
	private AppCompatImageView imagem;
	private ConstraintLayout btnAdd;

	private RecyclerView listaCartoes;
	private CartoesMeioPagamentoRecyclerViewAdapter adapter;

	private List<RetornoCartao> cartaos;

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
	public static MeioPagamentoFragment newInstance(List<RetornoCartao> cartoes, MeioPagamentoConfig config) {
		MeioPagamentoFragment fragment = new MeioPagamentoFragment();
		Bundle                   args     = new Bundle();
		if(config != null){
			args.putSerializable(ARG_CONFIG, config);
		}

		if (cartoes != null){
			args.putString(ARG_CARTOES, new Gson().toJson(cartoes));
		}

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			config  = (MeioPagamentoConfig) getArguments().getSerializable(ARG_CONFIG);

			TypeToken<List<RetornoCartao>> token = new TypeToken<List<RetornoCartao>>() {};
			String                         cartoesString = getArguments().getString(ARG_CARTOES);
			if(cartoesString != null && cartoesString.length() > 0){
				cartaos = new Gson().fromJson(cartoesString, token.getType());
			} else {
				cartaos = new ArrayList<>();
			}

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
		listaCartoes = view.findViewById(R.id.listaCartoes);
		adapter = new CartoesMeioPagamentoRecyclerViewAdapter(cartaos, getContext(), onRecyclerlistener());
		RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
		listaCartoes.setLayoutManager(lm);
		listaCartoes.setAdapter(adapter);

		aplicaConfig();

		configButton();

		return view;
	}

	@NonNull
	private OnCartaoMeioPagamentoListener onRecyclerlistener() {
		return new OnCartaoMeioPagamentoListener() {
			@Override
			public void itemClick(Object holder, int position) {
				TestVisao.toast("Item selecionado");
			}

			@Override
			public void favoritar(Object holder, int position) {
				TestVisao.toast("Fav selecionado");
			}

			@Override
			public void deletar(Object holder, int position) {
				TestVisao.toast("Deleta selecionado");
			}
		};
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