package br.gov.caixa.loterias.apostas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GerarPixDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.TimerSingleton;
import br.gov.caixa.loterias.apostas.model.model.PixModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.OnCopiaColaListener;

public class CopiaColaPixActivity extends LoteriasAppActivity implements OnCopiaColaListener {
	public static Boolean isRunning = false;

	private Button btnGerar;

	private PixModel model;

//	private ConstraintLayout layoutSimular;
//	FloatingActionButton btnCompraSucesso, btnCompraCPFDivergente, btnAlterarContagem;
//	ExtendedFloatingActionButton btnSimularCompra;
//	TextView txtCompraSucesso, txtCompraCPFDivergente, txtAlterarContagem;

//	private boolean isAllFabsVisible = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copia_cola_pix);
		configToolbar(R.id.toolbar);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		btnGerar = findViewById(R.id.botaoGerarPix);

		//aplicaLogicaSimulacao();


		btnGerar.setOnClickListener(v -> {
			restart();
		});

		model = new PixModel(this);
		gerarCodigoPix();
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
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	//	private void aplicaLogicaSimulacao() {
//		layoutSimular = findViewById(R.id.layoutSimular);
//		btnSimularCompra = findViewById(R.id.btnSimularCompra);
//		btnCompraSucesso = findViewById(R.id.btnCompraSucesso);
//		btnCompraCPFDivergente = findViewById(R.id.btnCompraCPFDivergente);
//		btnAlterarContagem = findViewById(R.id.btnAlterarContagem);
//		txtCompraSucesso = findViewById(R.id.txtCompraSucesso);
//		txtCompraCPFDivergente = findViewById(R.id.txtCompraDivergente);
//		txtAlterarContagem = findViewById(R.id.txtAlterarContagem);
//
//		btnSimularCompra.shrink();
//		btnSimularCompra.setOnClickListener(v -> {
//			if (!isAllFabsVisible) {
//				btnCompraSucesso.show();
//				btnCompraCPFDivergente.show();
//				btnAlterarContagem.show();
//				txtCompraSucesso.setVisibility(View.VISIBLE);
//				txtCompraCPFDivergente.setVisibility(View.VISIBLE);
//				txtAlterarContagem.setVisibility(View.VISIBLE);
//
//				btnSimularCompra.extend();
//				isAllFabsVisible = true;
//			} else {
//				btnCompraSucesso.hide();
//				btnCompraCPFDivergente.hide();
//				btnAlterarContagem.hide();
//				txtCompraSucesso.setVisibility(View.GONE);
//				txtCompraCPFDivergente.setVisibility(View.GONE);
//				txtAlterarContagem.setVisibility(View.GONE);
//
//				btnSimularCompra.shrink();
//				isAllFabsVisible = false;
//			}
//		});
//
//		btnAlterarContagem.setOnClickListener(v->{
//			DialogUtils.showDialogGetIntTest(CopiaColaPixActivity.this, "Quantos segundos?", "Informe um valor interiro válido em segundos: (1 = 1 seg, 10 = 10 seg)", valor -> {
//				CopiaColaPixFragment.SEGUNDOS = valor;
//				restart();
//				TimerSingleton.getInstance().cancel();
//			});
//		});
//
//		btnCompraSucesso.setOnClickListener(v -> {
//			Bundle bundle = new Bundle();
//			bundle.putLong(ConfirmacaoPagamentoActivity.ARG_ORIGEM, MeioPagamentoUtils.PIX);
//
//			Intent intent = IntentUtil.getIntentOrigemDestino(CopiaColaPixActivity.this, ConfirmacaoPagamentoActivity.class, bundle);
//			startActivity(intent);
//		});
//
//		btnCompraCPFDivergente.setOnClickListener(v -> {
//			DialogUtils.showDialog3Botoes(CopiaColaPixActivity.this,R.drawable.vermelho,
//										  getString(R.string.popup_pix_nao_autorizado),
//										  getString(R.string.popup_pix_nao_autorizado_descricao),
//										  getString(R.string.gerar_novo_codigo),
//										  getString(R.string.alterar_forma_pagamento),
//										  getString(R.string.btn_title_voltarAoInicio), onDialogListener());
//		});
//
//		if (BuildVersionUtil.isTQS()){
//			layoutSimular.setVisibility(View.VISIBLE);
//		} else {
//			layoutSimular.setVisibility(View.GONE);
//		}
//	}

//	private OnDialog3ButtonsListener onDialogListener() {
//		return new OnDialog3ButtonsListener() {
//			@Override
//			public void leftButtom() {
//				cancelContagem();
//				Intent intent = IntentUtil.getIntentOrigemDestino(CopiaColaPixActivity.this, OrientacaoPixActivity.class);
//				startActivity(intent);
//				finish();
//			}
//
//			@Override
//			public void centerButtom() {
//				cancelContagem();
//				Intent intent = IntentUtil.getIntentOrigemDestino(CopiaColaPixActivity.this, FormaPagamentoActivity.class);
//				startActivity(intent);
//				finish();
//			}
//
//			@Override
//			public void rightButtom() {
//				cancelContagem();
//				PrincipalActivity_.intent(CopiaColaPixActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP).start();
//				finish();
//			}
//		};
//	}

	private void cancelContagem(){
		TimerSingleton.getInstance().cancel();
	}

	private void startFragment(GerarPixDTO payload) {
		FragmentUtils.startCardCopiaColaPix(payload ,getSupportFragmentManager(), R.id.fragmentCopiaColaPix);
	}

	@Override
	public void onFinish() {
		btnGerar.setText(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.gerar_novo_codigo_pix)));
		btnGerar.setVisibility(View.VISIBLE);
	}

	private void restart() {
		Intent intent = IntentUtil.getIntentOrigemDestino(this, CopiaColaPixActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}

}