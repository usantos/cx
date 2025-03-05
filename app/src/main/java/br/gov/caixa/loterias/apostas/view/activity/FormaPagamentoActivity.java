package br.gov.caixa.loterias.apostas.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.OrientacaoPixActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.TermosUsoActivity;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumLong;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MeioPagamentoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.MeioPagamentoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MeioPagamentoUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.config.MeioPagamentoConfig;
import br.gov.caixa.loterias.apostas.view.fragment.MeioPagamentoFragment;

public class FormaPagamentoActivity extends AppCompatActivity {

	private MeioPagamentoFragment frameMercadoPago, frameRecargaPay;
	private Toolbar toolbar;
	private ImageButton btnAjuda;
	private ConstraintLayout containerPix;
	private MeioPagamentoModel model;
	private boolean carregouConteudo = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forma_pagamento);
		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(ViewUtils.textCaixaSTDBoldTitle(this, "_Forma de pagamento_"));

		containerPix = findViewById(R.id.container_pix);
		findViewById(R.id.btn_gerar_codigo).setOnClickListener(onGerarCodigoPixClick());

		configMenu();

		model = new MeioPagamentoModel(FormaPagamentoActivity.this);
	}

	private View.OnClickListener onGerarCodigoPixClick() {
		return v -> {
			Intent intent = IntentUtil.getIntentOrigemDestino(FormaPagamentoActivity.this, OrientacaoPixActivity.class);
			startActivity(intent);
		};
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
					// TODO: POSSIVELMENTE INCLUIR AQUI O FRAGMENT DO PIX
					if(retornoServicoContemPix(payload)){
						showPix();
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

	private void showPix() {
		containerPix.setVisibility(View.VISIBLE);
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
									R.id.fragmentRecargaPay,
									new MeioPagamentoConfig(R.drawable.recargapay, getRecargaPay(),
											getBandeirasRecargaPay(),
											R.color.laranja_recarga_pay));
	}
									//new MeioPagamentoConfig(R.color.laranja_recarga_pay, R.drawable.recargapay, getRecargaPay()));

	private List<Integer> getBandeirasRecargaPay(){

		List<Integer> bandeirasRecargaPay = new ArrayList<>();

		//Lista de bandeiras de cartão de crédito que são aceitas pelo Recarga Pay
		bandeirasRecargaPay.add(R.drawable.ic_bandeira_elo);
		bandeirasRecargaPay.add(R.drawable.ic_bandeira_master);
		bandeirasRecargaPay.add(R.drawable.ic_bandeira_visa);
		return bandeirasRecargaPay;
	}

	private void startFragmentMercadoPago(boolean primeiroMeioPagamentoCartao) {
		frameMercadoPago = FragmentUtils
				.startMeioPagamento(getSupportFragmentManager(),
									R.id.fragmentMercadoPago,
									new MeioPagamentoConfig(R.drawable.ic_mercado_pago, getMercadoPago(),
										getBandeirasMercadoPago(),
										R.color.azul_mercado_pago));
	}

	private List<Integer> getBandeirasMercadoPago(){

		List<Integer> bandeirasMercadoPago = new ArrayList<>();

		bandeirasMercadoPago.add(R.drawable.ic_bandeira_amex2);
		//listaBrandeirasMercadoPago.add(R.drawable.ic_bandeira_diners); -> Bandeira excluida.
		bandeirasMercadoPago.add(R.drawable.ic_bandeira_elo);
		bandeirasMercadoPago.add(R.drawable.ic_hipercard);
		bandeirasMercadoPago.add(R.drawable.ic_bandeira_master);
		bandeirasMercadoPago.add(R.drawable.ic_bandeira_visa);
		return bandeirasMercadoPago;

	}

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
}
