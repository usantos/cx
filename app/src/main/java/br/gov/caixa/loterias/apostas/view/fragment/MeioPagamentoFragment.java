package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
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
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CartoesMeioPagamentoRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.holder.CartaoMeioPagamentoHolder;
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
	private ConstraintLayout cabecalho;

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
		cabecalho = view.findViewById(R.id.cabecalho);
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
	private OnCartaoMeioPagamentoListener<CartaoMeioPagamentoHolder> onRecyclerlistener() {
		return new OnCartaoMeioPagamentoListener<CartaoMeioPagamentoHolder>() {
			@Override
			public void itemClick(CartaoMeioPagamentoHolder holder, int position) {
				TestVisao.toast("Item selecionado");
			}

			@Override
			public void favoritar(CartaoMeioPagamentoHolder holder, int position) {
				if (!cartaos.get(position).isFav()){
					Dialog dialogConfirmacao = ViewUtils.alertTitleCustomPositiveListener(getContext(), getString(R.string.alterar_favorito_interrogacao),
																						  getString(R.string.cancelar),
																						  getString(R.string.confirmar),
																						  getString(R.string.confirmar_alterar_cartao_favorito),
																						  (dialog, which) -> {
																							  adapter.atualizaCartaoFavorito(cartaos.get(position));
																						  });
					dialogConfirmacao.show();
				}
			}

			@Override
			public void deletar(CartaoMeioPagamentoHolder holder, int position) {
				if (cartaos.get(position).isFav()){
					ViewUtils.alertTitleButton(getContext(),
											   R.string.excluir_cartao_interrogacao,
											   getString(R.string.nao_e_possivel_eliminar_cartao_favorito),
											   getString(R.string.title_entendi));
				} else {
					String mensagem = getString(R.string.deseja_realmente_exluir_cartao) + cartaos.get(position).getUltimosDigitos() + "?";
					Dialog dialogConfirmacao = ViewUtils.alertTitleCustomPositiveListener(getContext(), getString(R.string.excluir_cartao_interrogacao),
																				getString(R.string.label_nao),
																				getString(R.string.confirmar),
																				mensagem,
																				(dialog, which) -> {
																					cartaos.remove(position);
																					adapter.notifyDataSetChanged();
																				});
					dialogConfirmacao.show();
				}
			}
		};
	}

	private void configButton() {
		btnAdd.setOnClickListener(v -> {
			goToAddCartao();
		});

		cabecalho.setOnClickListener(v -> {
			goToAddCartao();
		});
	}

	private void goToAddCartao(){
		TestVisao.toast("Implementar no futuro!");
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