package br.gov.caixa.loterias.apostas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.biometric.BiometricManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.model.model.PixModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.FormaPagamentoActivity;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;
import br.gov.caixa.loterias.apostas.view.listener.OnCopiaColaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialog3ButtonsListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListenerLeftRight;

public class CopiaColaPixActivity extends LoteriasAppActivity implements OnCopiaColaListener {
	public static Boolean isRunning = false;
	private boolean tempoExpirado = false, gerouCodigoPix = false;
	private SwipeRefreshLayout swipeRefreshLayout;
	private PixModel model;
	private CardPixFragment pixFragment;
	private Dialog confirmaSairTelaPix;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copia_cola_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		Button btnGerar = findViewById(R.id.botaoGerarPix);
		swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setOnRefreshListener(() -> {

			if (pixFragment != null){
				pixFragment.checkStatusPix(onCheckStatusPix());
			}
		});
		swipeRefreshLayout.setEnabled(false);

		btnGerar.setOnClickListener(v -> restart());

		model = new PixModel(this);
		gerarCodigoPix();
	}

	private OnCompraAsync onCheckStatusPix() {
		return new OnCompraAsync() {
			@Override
			public void successCompra() {
				swipeRefreshLayout.setRefreshing(false);
				finalizaProcessoCompraSucesso();
			}

			@Override
			public void redirectCompra(CompraAsyncResponse compraAsyncResponse) {
				swipeRefreshLayout.setRefreshing(false);
				RedirectNetwork.checkRedirectSucesso( compraAsyncResponse.getRedirect(), CopiaColaPixActivity.this);
			}

			@Override
			public void errorCompra(VolleyError volleyError) {
				RedirectNetwork.checkRedirectCompraAsync(volleyError, CopiaColaPixActivity.this);
				swipeRefreshLayout.setRefreshing(false);
			}
		};
	}

	private void finalizaProcessoCompraSucesso() {
		try {
			new CompraModel(CopiaColaPixActivity.this).salvaUltimaCompra(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal(), new Date());

			AppCenterManager.registraEvento(getResources().getString(R.string.evento_pagamento_sucesso));

			RateUtils.setQtdCompras(RateUtils.getQtdCompras() + 1);

			CarrinhoSingleton.getInstance().zerarCarrinho();
			CarrinhoFavoritoCRUD crud = new CarrinhoFavoritoCRUD(CopiaColaPixActivity.this);
			crud.deletaTodos();

			Bundle bundle   = new Bundle();
			bundle.putLong(ConfirmacaoPagamentoActivity.ARG_ORIGEM, MeioPagamentoUtils.PIX);
			Intent activity = IntentUtil.getIntentOrigemDestino(CopiaColaPixActivity.this, ConfirmacaoPagamentoActivity.class, bundle);
			startActivity(activity);
		}catch (Exception e){
			Log.d("COPIACOLAPIXA_ONSUCCESS", Objects.requireNonNull(e.getLocalizedMessage()));
		}
		finish();
	}

	private void gerarCodigoPix() {
		AlertDialogUtils.show(this);
		model.gerarCodigoPix(onGerarPixListener());
	}

	@Override
	protected void onStart() {
		super.onStart();
		isRunning = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		isRunning = false;
	}

	private OnSilceListener<GerarPixDTO> onGerarPixListener() {
		return new OnSilceListener<GerarPixDTO>() {
			@Override
			public void success(GerarPixDTO payload) {
				AlertDialogUtils.dismiss();
				startFragment(payload);
				swipeRefreshLayout.setEnabled(true);
				CarrinhoSingleton.getInstance().zerarCarrinho();
				gerouCodigoPix=true;
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void startFragment(GerarPixDTO payload) {
		pixFragment = FragmentUtils.startCardCopiaColaPix(payload, getSupportFragmentManager(), R.id.fragmentCopiaColaPix);
	}

	@Override
	public void onFinish()
	{
		tempoExpirado = true;
		swipeRefreshLayout.setEnabled(false);
	}

	@Override
	public void onSuccess() {
		finalizaProcessoCompraSucesso();
	}

	@Override
	public void irParaCompras() {
		Intent intent = IntentUtil.getIntentOrigemDestino(CopiaColaPixActivity.this, ListaComprasActivity.class);
		startActivity(intent);
	}

	private void restart() {
		Intent intent = IntentUtil.getIntentOrigemDestino(this, CopiaColaPixActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	@Override
	public boolean onSupportNavigateUp() {
		if(!tempoExpirado && gerouCodigoPix){
			confirmaSairTelaPix = DialogUtils.showDialogCustom(this,
					getString(R.string.label_atencao),
					getString(R.string.mensagem_pop_up_aguardando_confirmacao_pix_pergunta),
					getString(R.string.label_voltar_ao_inicio), getString(R.string.label_sim),
					null, onDialogListener(), true);
		}else{
			voltarParaCarrossel();
		}
		return false;
	}



	private OnDialog3ButtonsListener onDialogListener() {
		return new OnDialog3ButtonsListener() {
			@Override
			public void leftButtom() {
				//ocultado
				voltarParaCarrossel();

			}

			@Override
			public void centerButtom() {
				confirmaSairTelaPix.dismiss();
			}

			@Override
			public void rightButtom() {

			}
		};
	}

	private void voltarParaCarrossel(){
		Intent intent = IntentUtil.getIntentLimpandoPilhaActivities(CopiaColaPixActivity.this, PrincipalActivity.class);
		startActivity(intent);
		finish();
	}



}