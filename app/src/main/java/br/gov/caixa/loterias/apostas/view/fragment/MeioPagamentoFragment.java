package br.gov.caixa.loterias.apostas.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.AdicionaCartaoActivity;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.ApresentarCartaoActivity;
import br.gov.caixa.loterias.apostas.view.activity.ConfirmarCartaoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CartoesMeioPagamentoRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.holder.CartaoMeioPagamentoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnCartaoMeioPagamentoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;

public class MeioPagamentoFragment extends Fragment {
	private static final String ARG_CONFIG = "ARG_CONFIG";

	private View view;
	//private View margem;
	//private ConstraintLayout cabecalho;
	private AppCompatImageView imagem;
	private AppCompatImageView imgBandeira1;
	private AppCompatImageView imgBandeira2;
	private AppCompatImageView imgBandeira3;
	private AppCompatImageView imgBandeira4;
	private AppCompatImageView imgBandeira5;
	private AppCompatImageView imgBandeira6;
	private TextView btnAdd;
	private TextView textoSemCartao;
	private TextView btnListaMaisMenos;
	private EditText userInput;

	private RecyclerView listaCartoes;
	private CartoesMeioPagamentoRecyclerViewAdapter adapter;

	private MeioPagamentoConfig config;
	private List<RetornoCartao> cartoes;
	private List<RetornoCartao> cartoesMaiorDois;
	private CartoesModel model;
	private String requestBody;

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
		//margem = view.findViewById(R.id.margem);
		//cabecalho = view.findViewById(R.id.cabecalho);
		imagem = view.findViewById(R.id.imagemMeioPagamento);
		imgBandeira1 = view.findViewById(R.id.imgBandeira1);
		imgBandeira2 = view.findViewById(R.id.imgBandeira2);
		imgBandeira3 = view.findViewById(R.id.imgBandeira3);
		imgBandeira4 = view.findViewById(R.id.imgBandeira4);
		imgBandeira5 = view.findViewById(R.id.imgBandeira5);
		imgBandeira6 = view.findViewById(R.id.imgBandeira6);
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
		AlertDialogUtils.show(getContext());
		if (model.isMercadoPago(config.getMeioPagamento().getValor())){
			model.buscaCartoesPorMeioPagamento(MeioPagamentoUtils.MP_MERCADO_PAGO, onBuscaCartoesListener());
		} else if (model.isRecargaPay(config.getMeioPagamento().getValor())){
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
				if (retornoCartaoList.size() <= 2) {
					cartoes = retornoCartaoList;
				} else {
					maisQueDoisCartoes(retornoCartaoList);
				}

				adapter = new CartoesMeioPagamentoRecyclerViewAdapter(cartoes, getContext(), onRecyclerlistener());
				RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
				listaCartoes.setLayoutManager(lm);
				listaCartoes.setAdapter(adapter);
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
				//showDialogCVV(cartao);
				Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), ApresentarCartaoActivity.class);
				intent.putExtra(ApresentarCartaoActivity.CARTAO, cartao);
				intent.putExtra(ApresentarCartaoActivity.MEIO, config);
				startActivity(intent);
			}

			@Override
			public void favoritar(CartaoMeioPagamentoHolder holder, int position) {
				if (!cartoes.get(position).isFav()){
					Dialog dialogConfirmacao = ViewUtils.alertTitleCustomPositiveListener(getContext(), getString(R.string.alterar_favorito_interrogacao),
																						  getString(R.string.cancelar),
																						  getString(R.string.confirmar),
																						  getString(R.string.confirmar_alterar_cartao_favorito),
																						  (dialog, which) -> {
																							  adapter.atualizaCartaoFavorito(cartoes.get(position));
																						  });
					dialogConfirmacao.show();
				}
			}

			@Override
			public void deletar(CartaoMeioPagamentoHolder holder, int position) {
				if (cartoes.get(position).isFav()){
					ViewUtils.alertTitleButton(getContext(),
											   R.string.excluir_cartao_interrogacao,
											   getString(R.string.nao_e_possivel_eliminar_cartao_favorito),
											   getString(R.string.title_entendi));
				} else {
					//String mensagem = getString(R.string.deseja_realmente_exluir_cartao) + " " + cartoes.get(position).getUltimosDigitos() + "?";
					String mensagem = getString(R.string.voceDesejaExcluir);
					mensagem = mensagem.replace("{bandeira}", cartoes.get(position).getNomeMetodoPagamento());
					mensagem = mensagem.replace("{XXXX}", cartoes.get(position).getUltimosDigitos());
					Dialog dialogConfirmacao = ViewUtils.alertTitleCustomPositiveListener(getContext(), getString(R.string.excluir_cartao_interrogacao),
																				getString(R.string.naoExcluir),
																				getString(R.string.excluir),
																				mensagem,
																				(dialog, which) -> {
																					deletaCartao(cartoes.get(position), position);
																				});
					dialogConfirmacao.show();
				}
			}
		};
	}

	private void showDialogCVV(RetornoCartao cartao) {
		LayoutInflater li = LayoutInflater.from(getContext());
		View promptsView = li.inflate(R.layout.dialog_security_code, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
		alertDialogBuilder.setView(promptsView);
		userInput = promptsView.findViewById(R.id.cvvCardNumberEditText);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.confirmar),
								   (DialogInterface dialog, int which) -> {
									   onPositiveCVV(cartao);
								   })
				.setNegativeButton(getResources().getString(R.string.cancelar),
								   (DialogInterface dialog, int which) -> {
									   dialog.cancel();
								   });
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void onPositiveCVV(RetornoCartao cartao) {
		int tamanhoCVV = userInput.getText().toString().length();
		if (tamanhoCVV == 3 || tamanhoCVV == 4) {
			if (model.isMercadoPago(config.getMeioPagamento().getValor())){
				//requestBody = model.preencheDadosPagamentoCartaoSalvoMP(cartao, userInput.getText());
				requestBody = model.preencheDadosPagamentoCartaoSalvoMP(cartao, userInput.getText().toString());
				runOnUiThread(() -> AlertDialogUtils.show(getContext()));
				model.realizaCompraMercadoPago(cartao.getPrimeirosDigitos(), requestBody, "false", false, onCompraAsyncListener());
			} else {
				runOnUiThread(() -> AlertDialogUtils.show(getContext()));
				String cvv = userInput.getText().toString();
				model.recuperaTokenApostadorRecargaPay(onRecuperaTokenListener(cartao, cvv));
			}
		} else {
			ViewUtils.alertTitleButtonPositiveListener(getContext(), R.string.label_atencao, getResources().getString( R.string.label_informa_cvv ), R.string.ok,
													   (dialog1, which1) -> showDialogCVV(cartao));
		}
	}

	private OnSilceListener<String> onRecuperaTokenListener(RetornoCartao retornoCartao, String cvv) {
		return new OnSilceListener<String>() {
			@Override
			public void success(String token) {

				model.realizaCompraRecargaPayCartaoSalvo(token,retornoCartao.getTokenCartao(), cvv, onRecargaPayListener());
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private OnCompraAsync onRecargaPayListener() {
		return new OnCompraAsync() {
			@Override
			public void successCompra() {
				onCompraSuccess();
			}

			@Override
			public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
				onCompraRedirect(compraAsyncResponse);
			}

			@Override
			public void errorCompra(VolleyError volleyError) {
				onCompraError(volleyError);
			}
		};
	}

	private void onCompraError(VolleyError volleyError) {
		RedirectNetwork.checkRedirectCompraAsync( volleyError, getActivity() );
		AlertDialogUtils.dismiss();
	}

	private void onCompraRedirect(CompraAsyncResponse compraAsyncResponse) {
		RedirectNetwork.checkRedirectSucesso(compraAsyncResponse.getRedirect(), getActivity());
		AlertDialogUtils.dismiss();
	}

	private void onCompraSuccess() {
		new CompraModel(getActivity()).salvaUltimaCompra(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal(), new Date());
		AlertDialogUtils.dismiss();
		AppCenterManager.registraEvento(getResources().getString(R.string.evento_pagamento_sucesso));
		RateUtils.setQtdCompras(RateUtils.getQtdCompras() + 1);
		startActivity(new Intent(getContext(), ConfirmacaoPagamentoActivity.class));
		CarrinhoSingleton.getInstance().setCarrinho(new CarrinhoDTO());
		model.limparCarrinhoFavorito();
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
				adapter.notifyDataSetChanged();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void configButton() {
		btnAdd.setOnClickListener(v -> {
			ViewUtils.alertTitleButtonYesListener(getContext(),
					getResources().getString(R.string.suaTitularidade),
					getResources().getString(R.string.title_entendi),
					(dialogInterface, i) -> goToAddCartao());
		});

		btnListaMaisMenos.setOnClickListener(v -> clickMostraMaisMenos());
	}

	private void goToAddCartao(){

		Intent intent = IntentUtil.getIntentOrigemDestino(getActivity(), AdicionaCartaoActivity.class,
														  getString(R.string.bundle_key_config), config);

		startActivity(intent);
	}

	private void aplicaConfig() {
		if (config != null){
			//margem.setBackgroundColor(getResources().getColor(config.getIdCorBarra()));
			//btnAdd.setBackground(getActivity().getDrawable(config.getIdCorBarra()));
			//btnAdd.getBackground().setAlpha(20);
			btnAdd.setPaintFlags(btnAdd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			btnListaMaisMenos.setPaintFlags(btnListaMaisMenos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			imagem.setBackground(getActivity().getDrawable(config.getIdImagem()));
			imgBandeira1.setImageDrawable(getActivity().getDrawable(config.getIdBandeira1()));
			imgBandeira2.setImageDrawable(getActivity().getDrawable(config.getIdBandeira2()));
			imgBandeira3.setImageDrawable(getActivity().getDrawable(config.getIdBandeira3()));
			if (config.getIdBandeira4() == 0) {
				imgBandeira4.setVisibility(View.GONE);
			} else {
				imgBandeira4.setImageDrawable(getActivity().getDrawable(config.getIdBandeira4()));
			}
			if (config.getIdBandeira5() == 0) {
				imgBandeira5.setVisibility(View.GONE);
			} else {
				imgBandeira5.setImageDrawable(getActivity().getDrawable(config.getIdBandeira5()));
			}
			if (config.getIdBandeira6() == 0) {
				imgBandeira6.setVisibility(View.GONE);
			} else {
				imgBandeira6.setImageDrawable(getActivity().getDrawable(config.getIdBandeira6()));
			}
		}
	}

	private OnCompraAsync onCompraAsyncListener() {
		return new OnCompraAsync() {
			@Override
			public void successCompra() {
				onCompraSuccess();
			}

			@Override
			public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
				onCompraRedirect(compraAsyncResponse);
			}

			@Override
			public void errorCompra(VolleyError volleyError) {
				onCompraError(volleyError);
			}
		};
	}

	private void setSemCartao() {
		textoSemCartao.setVisibility(View.VISIBLE);
		if (model.isMercadoPago(config.getMeioPagamento().getValor())){
			textoSemCartao.setText(getActivity().getString(R.string.semCartaoMercadoPago));
		}
		if (model.isRecargaPay(config.getMeioPagamento().getValor())){
			textoSemCartao.setText(getActivity().getString(R.string.semCartaoRecargaPay));
		}
		btnAdd.setText(getActivity().getString(R.string.novoCartaoCredito));
	}

	private void maisQueDoisCartoes(List<RetornoCartao> retornoCartaoList) {
		btnListaMaisMenos.setVisibility(View.VISIBLE);
		cartoesMaiorDois = retornoCartaoList;
		cartoes = model.getCartoes2Primeiros(retornoCartaoList);
	}

	private void clickMostraMaisMenos() {

		//Lista Mais
		if (btnListaMaisMenos.getText().equals(getActivity().getString(R.string.listarMais))) {
			btnListaMaisMenos.setText(getActivity().getString(R.string.listarMenos));
			cartoes = cartoesMaiorDois;
			adapter.setLista(cartoes);
		} else { //Lista Menos
			btnListaMaisMenos.setText(getActivity().getString(R.string.listarMais));
			cartoes = model.getCartoes2Primeiros(cartoesMaiorDois);
			adapter.setLista(cartoes);
		}

	}


}