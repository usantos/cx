package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.MeioPagamentoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;

public class FormaPagamentoActivity extends AppCompatActivity {

	private MeioPagamentoFragment frameMercadoPago, frameRecargaPay, framePix;
	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private MeioPagamentoModel model;
	private boolean carregouConteudo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forma_pagamento);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "Forma de _pagamento_"));

		configMenu();

		model = new MeioPagamentoModel(FormaPagamentoActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!carregouConteudo){
			buscaMeiosPagamentos();
		}
	}

	private void apresentaAlertaPreCompra() {
		String valor = ViewUtils.getMoedaFormat(CarrinhoSingleton.getInstance().getCarrinho().getValorTotal());
		ViewUtils.alertTitleCustomPositiveListener(FormaPagamentoActivity.this,
												   "Cancelar",
												   "Confirmar",
												   FormaPagamentoActivity.this.getResources().getString(R.string.label_pre_pagamento).replace("{valorCompra}", valor), (dialog, which) -> {
			AlertDialogUtils.dismiss();
			dialog.dismiss();
		}, (dialog, which) -> {
			finish();
		} );
	}

	private void buscaMeiosPagamentos() {
		AlertDialogUtils.show(this);
		model.buscaMeiosPagamentos(onMeiosPagamentosListener());
	}

	private OnSilceListener<List<MeioPagamentoDTO>> onMeiosPagamentosListener() {
		return new OnSilceListener<List<MeioPagamentoDTO>>() {
			@Override
			public void success(List<MeioPagamentoDTO> payload) {
				if (payload != null && !payload.isEmpty()){
					if(retornoServicoContemPix(payload)){
						startFragmentPix();
						carregouConteudo = true;
					}
					boolean primeiroMeioPagamentoCartao = true;
					for (MeioPagamentoDTO meioPagamento: payload){
						if (MeioPagamentoUtils.isMercadoPago(meioPagamento.getId())){
							startFragmentMercadoPago(primeiroMeioPagamentoCartao);
						} else if (MeioPagamentoUtils.isRecargaPay(meioPagamento.getId())){
							startFragmentRecargaPay(primeiroMeioPagamentoCartao);
						}
						primeiroMeioPagamentoCartao = false;
						carregouConteudo = true;
					}
				}

				AlertDialogUtils.dismiss();
				apresentaAlertaPreCompra();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
			}
		};
	}

	private boolean retornoServicoContemPix(List<MeioPagamentoDTO> payload) {
		Boolean temPix = Boolean.FALSE;

		if (payload != null && !payload.isEmpty()){
			for (MeioPagamentoDTO meioPagamento: payload){
				if (meioPagamento.getId() == MeioPagamentoUtils.PIX){
					temPix = true;
					break;
				}
			}
		}

		return temPix;
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

	private void startFragmentRecargaPay(boolean primeiroMeioPagamentoCartao) {
		if (primeiroMeioPagamentoCartao){}
		frameRecargaPay = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									getIdFrameLayout(primeiroMeioPagamentoCartao),
									getConfigRecarga(primeiroMeioPagamentoCartao));
	}

	private int getIdFrameLayout(boolean primeiroMeioPagamentoCartao){
		if (primeiroMeioPagamentoCartao){
			return R.id.fragmentCartao1;
		}

		return R.id.fragmentCartao2;
	}

	private void startFragmentMercadoPago(boolean primeiroMeioPagamentoCartao) {
		frameMercadoPago = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									getIdFrameLayout(primeiroMeioPagamentoCartao),
									getConfigMercadoPago(primeiroMeioPagamentoCartao));
	}

	private void startFragmentPix() {
		framePix = FragmentUtils.startMeioPagamento(getSupportFragmentManager(),
													R.id.fragmentPix,
													MeioPagamentoUtils.getConfigPix(true));
	}

	private MeioPagamentoConfig getConfigRecarga(boolean primeiroMeioPagamentoCartao) {
		return MeioPagamentoUtils.getConfigRecarga(getTituloCartaoDeCredito(primeiroMeioPagamentoCartao), false);
	}

	private MeioPagamentoConfig getConfigMercadoPago(boolean primeiroMeioPagamentoCartao) {
		return MeioPagamentoUtils.getConfigMercadoPago(getTituloCartaoDeCredito(primeiroMeioPagamentoCartao), false);
	}

	@NonNull
	private String getTituloCartaoDeCredito(boolean primeiroMeioPagamentoCartao) {
		String titulo = "";
		if (primeiroMeioPagamentoCartao){
			titulo = getString(R.string.cartao_de_credito);
		}
		return titulo;
	}

}