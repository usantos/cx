package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity_;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.CartoesModel;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.model.model.MeioPagamentoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.CartaoUtil;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;

import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;

public class ConfirmarCartaoActivity extends AppCompatActivity {

	public static final String CARTAO = "CARTAO";
	public static final String MEIO = "MEIO";
	public static final String CVV = "CVV";

	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private CartoesModel model;
	private String requestBody;

	private RetornoCartao cartao;
	private MeioPagamentoConfig config;
	private String cvv;
	private Button btnApostar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmar_cartao);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.confirmarCartao)));
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
			cartao = (RetornoCartao) bundle.getSerializable(CARTAO);
			config = (MeioPagamentoConfig) bundle.getSerializable(MEIO);
			cvv = (String) bundle.getSerializable(CVV);
		}
	}

	private void atualizaCartaoFrente() {

		((AppCompatImageView) findViewById(R.id.imgBandeiraCartao)).setImageDrawable(CartaoUtil.getBandeira(this, cartao.getNomeMetodoPagamento()));

		((TextView) findViewById(R.id.textoNumero)).setText(getString(R.string.mascara_cartao_sem_ultimos_digitos) + "  " + cartao.getUltimosDigitos());

		((TextView) findViewById(R.id.textoValidMesAno)).setText(cartao.getMesValidade() + "/"  + cartao.getAnoValidade());

	}

	private void atualizaCartaoVerso() {
		((AppCompatImageView) findViewById(R.id.imgBandeiraVerso)).setImageDrawable(CartaoUtil.getBandeira(this, cartao.getNomeMetodoPagamento()));
		((TextView) findViewById(R.id.textoCVV)).setText(cvv);
	}

	private void configButton() {
		btnApostar = findViewById(R.id.btnApostar);
		btnApostar.setOnClickListener(v -> {
			apostar();
		});
	}

	private void apostar() {
		if (model.isMercadoPago(config.getMeioPagamento().getValor())){
			requestBody = model.preencheDadosPagamentoCartaoSalvoMP(cartao, cvv);
			runOnUiThread(() -> AlertDialogUtils.show(this));
			model.realizaCompraMercadoPago(cartao.getPrimeirosDigitos(), requestBody, "false", false, onCompraAsyncListener());
		} else {
			runOnUiThread(() -> AlertDialogUtils.show(this));
			//String cvv = userInput.getText().toString();
			model.recuperaTokenApostadorRecargaPay(onRecuperaTokenListener(cartao, cvv));
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

	private void onCompraRedirect(CompraAsyncResponse compraAsyncResponse) {
		RedirectNetwork.checkRedirectSucesso(compraAsyncResponse.getRedirect(), this);
		AlertDialogUtils.dismiss();
	}

	private void onCompraSuccess() {
		new CompraModel(this).salvaUltimaCompra(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal(), new Date());
		AlertDialogUtils.dismiss();
		AppCenterManager.registraEvento(getResources().getString(R.string.evento_pagamento_sucesso));
		RateUtils.setQtdCompras(RateUtils.getQtdCompras() + 1);
		startActivity(new Intent(this, ConfirmacaoPagamentoActivity.class));
		CarrinhoSingleton.getInstance().setCarrinho(new CarrinhoDTO());
		model.limparCarrinhoFavorito();
	}

	private void onCompraError(VolleyError volleyError) {
		RedirectNetwork.checkRedirectCompraAsync( volleyError, this);
		AlertDialogUtils.dismiss();
	}

}