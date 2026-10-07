package br.gov.caixa.loterias.apostas;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.controllers.ConfirmacaoPagamentoActivity;
import br.gov.caixa.loterias.apostas.controllers.ListaComprasActivity;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraAsyncResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.crud.CarrinhoFavoritoCRUD;
import br.gov.caixa.loterias.apostas.model.model.CompraModel;
import br.gov.caixa.loterias.apostas.model.model.PixModel;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraAsync;
import br.gov.caixa.loterias.apostas.view.listener.OnCopiaColaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogDoisBotoesListener;

public class CopiaColaPixActivity extends LoteriasAppActivity implements OnCopiaColaListener {
	public static final String ARG_GEROU_CODIGO = "ARG_GEROU_CODIGO";
	public static final String ARG_PIX = "ARG_PIX";
	public static Boolean isRunning = false;
	private boolean tempoExpirado = false, gerouCodigoPix = false;
	private SwipeRefreshLayout swipeRefreshLayout;
	private GerarPixDTO codigoPix;
	private PixModel model;
	private CardPixFragment pixFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copia_cola_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));
		swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setOnRefreshListener(() -> {
			if (pixFragment != null){
				pixFragment.checkStatusPix(onCheckStatusPix());
			}
		});
		swipeRefreshLayout.setEnabled(false);

		model = new PixModel(this);
		getExtras();
		gerarCodigoPix();
	}

	private void getExtras() {
		gerouCodigoPix = getIntent().getExtras().getBoolean(ARG_GEROU_CODIGO);
		String json = getIntent().getExtras().getString(ARG_PIX);
		codigoPix = new Gson().fromJson(json, GerarPixDTO.class);
		codigoPix.getPixCopiaECola();
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

	private boolean compraRegistrada;

	private void finalizaProcessoCompraSucesso() {
		if (compraRegistrada) return;
		compraRegistrada = true;
		new br.gov.caixa.loterias.apostas.utils.ModalidadePreferences(this).recordPurchase(CarrinhoSingleton.getInstance().getCarrinho());
		try {
			new CompraModel(CopiaColaPixActivity.this).salvaUltimaCompra(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal(), new Date());

			AppCenterManager.registraEvento(getResources().getString(R.string.evento_pagamento_sucesso));

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
		startFragment(codigoPix);
		swipeRefreshLayout.setEnabled(true);
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

	private void startFragment(GerarPixDTO payload) {
		pixFragment = FragmentUtils.startCardCopiaColaPix(payload, getSupportFragmentManager(), R.id.fragmentCopiaColaPix);
	}

	@Override
	public void onFinish() {
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
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	@Override
	public boolean onSupportNavigateUp() {
		voltarTela();
		return false;
	}

	private void voltarTela() {
		if(!tempoExpirado && gerouCodigoPix){

			DialogUtils.dialogDoisBotoesPersonalizados(CopiaColaPixActivity.this,
					"Atenção",
					getString(R.string.mensagem_pop_up_aguardando_confirmacao_pix_pergunta),
					"Sim",
					"Voltar ao início",
					new OnDialogDoisBotoesListener() {
						@Override
						public void PositiveButton(DialogInterface dialog, int which) {
							//Sem ação, pois o botão "Sim" é o de confirmar que o usuário quer permanecer na tela, ou seja, não tem ação a ser executada
						}

						@Override
						public void NegativeButton(DialogInterface dialog, int which) {
							voltarParaCarrossel();
						}
					});

		}else{
			voltarParaCarrossel();
		}
	}

	@Override
	public void onBackPressed() {
		voltarTela();
	}

	private void voltarParaCarrossel(){
		Intent intent = IntentUtil.getIntentLimpandoPilhaActivities(CopiaColaPixActivity.this, PrincipalActivity.class);
		startActivity(intent);
		finish();
	}

}