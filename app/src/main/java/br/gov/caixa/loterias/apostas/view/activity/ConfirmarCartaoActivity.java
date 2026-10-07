package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.Date;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.CartaoCreditoConfig;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoFragment;
import br.gov.caixa.loterias.apostas.view.fragment.CartaoCreditoVersoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;


public class ConfirmarCartaoActivity extends LoteriasBaseAppActivity {

	public static final String CARTAO_SALVO = "CARTAO_SALVO";
	public static final String CARTAO_NOVO = "CARTAO_NOVO";
	public static final String MEIO = "MEIO";
	public static final String CVV = "CVV";

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private CartoesModel model;
	private String requestBody;

	private RetornoCartao cartaoSalvo;
	private CartaoCreditoConfig cartaoNovo;
	private MeioPagamentoConfig config;
	private String cvv;
	private Button btnApostar;

	private CartaoCreditoFragment cartaoFrenteFragment;
	private CartaoCreditoVersoFragment cartaoVersoFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmar_cartao);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.confirmarDadosCartao)));
		configMenu();

		model = new CartoesModel(this);

		pegaExtras();
		atualizaCartaoFrente();
		atualizaCartaoVerso();
		configButton();
	}

	private void configMenu() {
		btnAjuda = findViewById(R.id.ib_duvidas);
		btnAjuda.setVisibility(View.VISIBLE);
		btnAjuda.setOnClickListener(v -> abrirTermosUso());
	}

	protected void abrirTermosUso() {
		Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onSupportNavigateUp() {
		finish();
		return true;
	}

	private void pegaExtras() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			cartaoSalvo = (RetornoCartao) bundle.getSerializable(CARTAO_SALVO);
			cartaoNovo = (CartaoCreditoConfig) bundle.getSerializable(CARTAO_NOVO);
			config = (MeioPagamentoConfig) bundle.getSerializable(MEIO);
			cvv = (String) bundle.getSerializable(CVV);
		}
	}

	private void atualizaCartaoFrente() {
		cartaoFrenteFragment = FragmentUtils.startCartaoCredito(getSupportFragmentManager(),R.id.containerCartaoFrente, getCartaoFrenteConfig());
	}

	private CartaoCreditoConfig getCartaoFrenteConfig() {
		if (cartaoSalvo != null){
			return new CartaoCreditoConfig(cartaoSalvo.getNomeMetodoPagamento(),
									getString(R.string.mascara_cartao_sem_ultimos_digitos) + "  " + cartaoSalvo.getUltimosDigitos(),
									"-",
										   StringUtils.padZeroLeft(cartaoSalvo.getMesValidade().intValue(),2),
										   String.valueOf(cartaoSalvo.getAnoValidade()),
									false);
		} else {
			return new CartaoCreditoConfig(cartaoNovo.getNomeMetodo(),cartaoNovo.getNumero(),
									cartaoNovo.getNomeTitular(), cartaoNovo.getMes(), cartaoNovo.getAno(),
									cartaoNovo.isSalvar());
		}
	}

	private void atualizaCartaoVerso() {
		cartaoVersoFragment = FragmentUtils.startCartaoCreditoVerso(getSupportFragmentManager(), R.id.containerCartaoVerso, getMetodo(), cvv);
	}

	private String getMetodo() {
		if (cartaoSalvo != null){
			return cartaoSalvo.getNomeMetodoPagamento();
		} else {
			return cartaoNovo.getNomeMetodo();
		}
	}

	private void configButton() {
		btnApostar = findViewById(R.id.btnApostar);
		btnApostar.setOnClickListener(v -> {
			btnApostar.setEnabled(false);
			apostar();
		});
	}

	private void apostar() {
		runOnUiThread(() -> AlertDialogUtils.show(this));

		if (cartaoSalvo != null){
			if (model.isMercadoPago(config.getMeioPagamento().getValor())){
				requestBody = model.preencheDadosPagamentoCartaoSalvoMP(cartaoSalvo, cvv);
				realizaCompraMercadoPago(cartaoSalvo.getPrimeirosDigitos(), requestBody,"false",false);
			} else {
				model.recuperaTokenApostadorRecargaPay(onRecuperaTokenListener(cartaoSalvo, cvv));
			}
		} else {
			model.buscaMeioPagamento(config.getMeioPagamento().getValor(), onMeioPagamentoListener());
		}
	}

	private OnSilceListener<MeioPagamentoDTO> onMeioPagamentoListener() {
		return new OnSilceListener<MeioPagamentoDTO>() {
			@Override
			public void success(MeioPagamentoDTO payload) {
				if (model.isMercadoPago(config.getMeioPagamento().getValor())){
					String requestBody = model.preencheDadosPagamento(DadosUsuarioBO.obterCpf(),
																	  cartaoNovo.getNomeTitular(),
																	  cartaoNovo.getNumero().replace(getResources().getString(R.string.espaco_em_branco), getResources().getString(R.string.string_vazia)),
																	  Integer.valueOf(cartaoNovo.getAno()),
																	  Integer.valueOf(cartaoNovo.getMes()),
																	  cvv);
					String bin = cartaoNovo.getNumero().replace(getResources().getString(R.string.espaco_em_branco),
																getResources().getString(R.string.string_vazia)).substring(0, 7);

					AppCenterManager.registraEventoDeviceMP(getResources().getString(R.string.app_center_mp_sem_cartao), requestBody);
					String salvarCartao = cartaoNovo.isSalvar() ? "true" : "false";
					realizaCompraMercadoPago(bin, requestBody,salvarCartao,true);
				} else {
					realizaCompraRecargaPay();
				}
			}

			@Override
			public void error(VolleyError error) {
				btnApostar.setEnabled(true);
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void realizaCompraRecargaPay() {
		model.recuperaTokenApostadorRecargaPay(onRecuperaTokenCartaoNovoListener());
	}

	private OnSilceListener<String> onRecuperaTokenCartaoNovoListener() {
		return new OnSilceListener<String>() {
			@Override
			public void success(String token) {
				// chama servico para recuperar informacoes pro recargapay
				model.buscaDadosPessoais(onDadosPessoaisListener(token));
			}

			@Override
			public void error(VolleyError error) {
				btnApostar.setEnabled(true);
				AlertDialogUtils.dismiss();
			}
		};
	}

	private OnSilceListener<ApostadorDTO> onDadosPessoaisListener(String token) {
		return new OnSilceListener<ApostadorDTO>() {
			@Override
			public void success(ApostadorDTO payload) {
				model.realizaCompraRecargaPayNovoCartao(token, true,
														model.getCartaoRecargaPay(payload,
																				  cartaoNovo.getNumero(),
																				  cartaoNovo.getMes(),
																				  cartaoNovo.getAno(),
																				  cvv,
																				  cartaoNovo.isSalvar()),
														onRecargaPayListener());
			}

			@Override
			public void error(VolleyError error) {
				btnApostar.setEnabled(true);
				AlertDialogUtils.dismiss();
			}
		};
	}



	private void realizaCompraMercadoPago(String bin, String requestBody, String salvar, boolean isNovo) {
		model.realizaCompraMercadoPago(bin, requestBody, salvar, isNovo, onCompraAsyncListener());
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
				btnApostar.setEnabled(true);
				onCompraError(volleyError);
			}
		};
	}

	private OnSilceListener<String> onRecuperaTokenListener(RetornoCartao retornoCartao, String cvv) {
		return new OnSilceListener<String>() {
			@Override
			public void success(String token) {

				model.realizaCompraRecargaPayCartaoSalvo(token,retornoCartao.getTokenCartao(), cvv, onRecargaPayListener());
			}

			@Override
			public void error(VolleyError error) {
				btnApostar.setEnabled(true);
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
				btnApostar.setEnabled(true);
				onCompraError(volleyError);
			}
		};
	}

	private void onCompraRedirect(CompraAsyncResponse compraAsyncResponse) {
		RedirectNetwork.checkRedirectSucesso(compraAsyncResponse.getRedirect(), this);
		AlertDialogUtils.dismiss();
	}

	private boolean compraRegistrada;

	private void onCompraSuccess() {
		if (compraRegistrada) return;
		compraRegistrada = true;
		new br.gov.caixa.loterias.apostas.utils.ModalidadePreferences(this).recordPurchase(CarrinhoSingleton.getInstance().getCarrinho());
		new CompraModel(this).salvaUltimaCompra(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal(), new Date());
		AlertDialogUtils.dismiss();
		AppCenterManager.registraEvento(getResources().getString(R.string.evento_pagamento_sucesso));
		startActivity(new Intent(this, ConfirmacaoPagamentoActivity.class));
		CarrinhoSingleton.getInstance().zerarCarrinho();
		model.limparCarrinhoFavorito();
	}

	private void onCompraError(VolleyError volleyError) {
		RedirectNetwork.checkRedirectCompraAsync( volleyError, this);
		AlertDialogUtils.dismiss();
	}

}