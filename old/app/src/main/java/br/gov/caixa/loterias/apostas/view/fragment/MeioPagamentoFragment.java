package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.AdicionaCartaoActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.ApresentarCartaoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CartoesMeioPagamentoRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.holder.CartaoMeioPagamentoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnCartaoMeioPagamentoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public class MeioPagamentoFragment extends Fragment {
	private static final String ARG_CONFIG = "ARG_CONFIG";

	private View view;
	private AppCompatImageView imagem;
	private int[] idsImageViewBandeiras = {R.id.imgBandeira1, R.id.imgBandeira2, R.id.imgBandeira3, R.id.imgBandeira4, R.id.imgBandeira5, R.id.imgBandeira6};
	private AppCompatImageView[] imageViewBandeiras = new AppCompatImageView[idsImageViewBandeiras.length];
	private TextView btnAdd;
	private TextView textoSemCartao;
	private TextView btnListaMaisMenos;

	private RecyclerView listaCartoes;
	private CartoesMeioPagamentoRecyclerViewAdapter adapter;

	private MeioPagamentoConfig config;
	private List<RetornoCartao> cartoes;
	private CartoesModel model;

	public MeioPagamentoFragment() {}

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

		model = new CartoesModel(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_meio_pagamento, container, false);
		imagem = view.findViewById(R.id.imagemMeioPagamento);
		for (int i = 0 ; i < idsImageViewBandeiras.length; i ++){
			imageViewBandeiras[i] = view.findViewById(idsImageViewBandeiras[i]);
		}
		btnAdd = view.findViewById(R.id.linkAdicionaCartao);
		textoSemCartao = view.findViewById(R.id.textoSemCartao);
		btnListaMaisMenos = view.findViewById(R.id.linkListarMaisMenos);
		listaCartoes = view.findViewById(R.id.listaCartoes);

		buscaCartoes();
		aplicaConfig();
		configButton();

		return view;
	}

	private void buscaCartoes() {
		if (model.isMercadoPago(config.getMeioPagamento().getValor())){
			AlertDialogUtils.show(getContext());
			model.buscaCartoesPorMeioPagamento(MeioPagamentoUtils.MP_MERCADO_PAGO, onBuscaCartoesListener());
		} else if (model.isRecargaPay(config.getMeioPagamento().getValor())){
			AlertDialogUtils.show(getContext());
			model.buscaCartoesPorMeioPagamento(MeioPagamentoUtils.MP_RECARGA_PAY, onBuscaCartoesListener());
		}
	}

	private OnSilceListener<List<RetornoCartao>> onBuscaCartoesListener() {
		return new OnSilceListener<List<RetornoCartao>>() {
			@Override
			public void success(List<RetornoCartao> retornoCartaoList) {
				if (retornoCartaoList.size() == 0) {
					setSemCartao();
				}

				cartoes = retornoCartaoList;
				adapter = new CartoesMeioPagamentoRecyclerViewAdapter(cartoes, getContext(), onRecyclerlistener());
				RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
				listaCartoes.setLayoutManager(lm);
				listaCartoes.setAdapter(adapter);

				if (adapter.temMaisPraMostrar()) {
					btnListaMaisMenos.setVisibility(View.VISIBLE);
				}

				AlertDialogUtils.dismiss();
			}

			@Override
			public void error(VolleyError error) {
				setSemCartao();
				AlertDialogUtils.dismiss();
			}
		};
	}

	@NonNull
	private OnCartaoMeioPagamentoListener<CartaoMeioPagamentoHolder> onRecyclerlistener() {
		return new OnCartaoMeioPagamentoListener<CartaoMeioPagamentoHolder>() {
			@Override
			public void itemClick(CartaoMeioPagamentoHolder holder, int position) {
				RetornoCartao cartao = cartoes.get(position);
				Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ApresentarCartaoActivity.class);
				intent.putExtra(ApresentarCartaoActivity.CARTAO, cartao);
				intent.putExtra(ApresentarCartaoActivity.MEIO, config);
				startActivity(intent);
			}

			@Override
			public void favoritar(CartaoMeioPagamentoHolder holder, int position) {
				if (!cartoes.get(position).isFav()){
					DialogUtils.dialogTituloConfirmar(
							getContext(),
							getStringById(R.string.alterar_favorito_interrogacao),
							getStringById(R.string.confirmar_alterar_cartao_favorito),
							new OnDialogBotaoListener() {
								@Override
								public void onButtonClick(DialogInterface dialog, int which) {
									adapter.atualizaCartaoFavorito(cartoes.get(position));
								}
							}
					);
				}
			}

			@Override
			public void deletar(CartaoMeioPagamentoHolder holder, int position) {
				if (cartoes.get(position).isFav()){
					DialogUtils.dialogTituloEntendi(
							getContext(),
							getStringById(R.string.excluir_cartao_interrogacao),
							getStringById(R.string.nao_e_possivel_eliminar_cartao_favorito)
					);

				} else {
					String mensagem = getStringById(R.string.voceDesejaExcluir);
					mensagem = mensagem.replace("{bandeira}", cartoes.get(position).getNomeMetodoPagamento());
					mensagem = mensagem.replace("{XXXX}", cartoes.get(position).getUltimosDigitos());

					DialogUtils.dialogTituloSim(
							getContext(),
							getStringById(R.string.excluir_cartao_interrogacao),
							mensagem,
							new OnDialogBotaoListener() {
								@Override
								public void onButtonClick(DialogInterface dialog, int which) {
									deletaCartao(cartoes.get(position), position);
								}
							}

					);
				}
			}
		};
	}

	private void deletaCartao(RetornoCartao cartao, int position) {
		AlertDialogUtils.show(getContext());
		if (model.isMercadoPago(config.getMeioPagamento().getValor())){
			model.deletaCartao(cartao.getIdCartao().toString(), MeioPagamentoUtils.MP_MERCADO_PAGO, onDeletaCartao(position));
		} else if (model.isRecargaPay(config.getMeioPagamento().getValor())){
			model.deletaCartao(cartao.getTokenCartao(), MeioPagamentoUtils.MP_RECARGA_PAY, onDeletaCartao(position));
		}
	}

	private OnSilceListener<List<RetornoCartao>> onDeletaCartao(int position) {
		return new OnSilceListener<List<RetornoCartao>>() {
			@Override
			public void success(List<RetornoCartao> payload) {
				AlertDialogUtils.dismiss();
				cartoes.remove(position);
				adapter.atualizaLista();
				if (cartoes.isEmpty()){
					setSemCartao();
				}

				if (adapter.temMaisPraMostrar()) {
					btnListaMaisMenos.setVisibility(View.VISIBLE);
				} else {
					btnListaMaisMenos.setVisibility(View.GONE);
				}
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void configButton() {
		btnAdd.setOnClickListener(v -> {
			DialogUtils.dialogEntendiListener(
					getContext(),
					getStringById(R.string.suaTitularidade),
					new OnDialogBotaoListener() {
						@Override
						public void onButtonClick(DialogInterface dialog, int which) {
							goToAddCartao();
						}
					}
			);
		});

		btnListaMaisMenos.setOnClickListener(v -> clickMostraMaisMenos());
	}

	private void goToAddCartao(){
		Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), AdicionaCartaoActivity.class, getStringById(R.string.bundle_key_config), config);
		startActivity(intent);
	}

	private void aplicaConfig() {
		if (config != null){
			btnAdd.setPaintFlags(btnAdd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			btnListaMaisMenos.setPaintFlags(btnListaMaisMenos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			imagem.setBackground(getActivity().getDrawable(config.getIdImagem()));
			if(model.isMercadoPago(config.getMeioPagamento().getValor())){
				imagem.setContentDescription("Mercado Pago");
			}
			else {
				imagem.setContentDescription("Recarga Pay");
			}
			imagem.setFocusable(true);
			for(int i = 0; i < config.getBandeirasMeioPagamento().size(); i++){
				imageViewBandeiras[i].setImageDrawable(getActivity().getDrawable(config.getBandeirasMeioPagamento().get(i)));
			}
		}
	}

	private void setSemCartao() {
		textoSemCartao.setVisibility(View.VISIBLE);
		if (model.isMercadoPago(config.getMeioPagamento().getValor())){
			textoSemCartao.setText(getStringById(R.string.semCartaoMercadoPago));
		}
		if (model.isRecargaPay(config.getMeioPagamento().getValor())){
			textoSemCartao.setText(getStringById(R.string.semCartaoRecargaPay));
		}
		btnAdd.setText(getStringById(R.string.novoCartaoCredito));
	}

	private void clickMostraMaisMenos() {
		//Lista Mais
		if (btnListaMaisMenos.getText().equals(getStringById(R.string.listarMais))) {
			btnListaMaisMenos.setText(getStringById(R.string.listarMenos));
			adapter.mostrarTodos();
		} else { //Lista Menos
			btnListaMaisMenos.setText(getStringById(R.string.listarMais));
			adapter.mostrarMenos();
		}

	}

	private String getStringById(int idString){
		try {
			if (getActivity() != null){
				return getActivity().getString(idString);
			} else if (getResources() != null){
				return getResources().getString(idString);
			} else {
				return getString(idString);
			}
		} catch (Exception e){
			return "";
		}
	}

}
