package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
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

	private MeioPagamentoFragment frameMercadoPago, frameRecargaPay;
	private Toolbar toolbar;
	private ImageButton btnAjuda;

	private MeioPagamentoModel model;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forma_pagamento);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_Forma de pagamento_"));

		configMenu();

		model = new MeioPagamentoModel(FormaPagamentoActivity.this);

		buscaMeiosPagamentos();
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
					for (MeioPagamentoDTO meioPagamento: payload){
						if (MeioPagamentoUtils.isMercadoPago(meioPagamento.getId())){
							startFragmentMercadoPago();
						} else if (MeioPagamentoUtils.isRecargaPay(meioPagamento.getId())){
							startFragmentRecargaPay();
						}
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

	private void startFragmentRecargaPay() {
		frameRecargaPay = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									R.id.fragmentRecargaPay,
									new MeioPagamentoConfig(R.drawable.recargapay, getRecargaPay(),
											R.drawable.ic_bandeira_elo, R.drawable.ic_bandeira_master,
											R.drawable.ic_bandeira_visa, 0,0, 0,
											R.color.laranja_recarga_pay));
	}
									//new MeioPagamentoConfig(R.color.laranja_recarga_pay, R.drawable.recargapay, getRecargaPay()));


	private DTOEnumLong getRecargaPay() {
		DTOEnumLong recarga = new DTOEnumLong();
		recarga.setValor(5L);
		recarga.setDescricao("RecargaPay");
		return recarga;
	}

	private DTOEnumLong getMercadoPago() {
		DTOEnumLong recarga = new DTOEnumLong();
		recarga.setValor(1L);
		recarga.setDescricao("Mercado pago");
		return recarga;
	}

	private void startFragmentMercadoPago() {
		frameMercadoPago = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									R.id.fragmentMercadoPago,
									new MeioPagamentoConfig(R.drawable.ic_mercado_pago, getMercadoPago(),
										R.drawable.ic_bandeira_amex2, R.drawable.ic_bandeira_diners3,
										R.drawable.ic_bandeira_elo, R.drawable.ic_hipercard,
										R.drawable.ic_bandeira_master, R.drawable.ic_bandeira_visa,
										R.color.azul_mercado_pago));
	}
						//new MeioPagamentoConfig(R.color.azul_mercado_pago, R.drawable.ic_mercado_pago, getMercadoPago()));

}