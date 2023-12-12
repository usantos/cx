package br.gov.caixa.loterias.apostas.view.activity;

import android.os.Bundle;

import com.google.gson.Gson;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;

public class DetalhesBolaoActivity extends LoteriasAppMarketPlaceActivity {
	public static final String ARG_CODIGO_BOLAO = "ARG_CODIGO_BOLAO";
	public static final String ARG_MODALIDADE = "ARG_MODALIDADE";

	private String codigoBolao;
	private ModalidadeEnum modalidade;

	private SomadorCarrinhoFragment somadorCarrinhoFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_bolao);
		configToolbar(R.id.toolbar);
		configBackground(R.color.mega_verde_escuro_mkp);

		codigoBolao = getIntent().getStringExtra(ARG_CODIGO_BOLAO);
		modalidade = new Gson().fromJson(getIntent().getStringExtra(ARG_MODALIDADE), ModalidadeEnum.class);

		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_apostas bolão_"));

		fragmentSubBarraModalidade();
		fragmentSomadorCarrinho();
	}

	private void fragmentSubBarraModalidade() {
		FragmentUtils.startFragmentSubBarraModalidade(getSupportFragmentManager(),R.id.id_fg_sub_barra,
				ModalidadeEnum.MEGA_SENA, "1891", "03/12");
	}

	private void fragmentSomadorCarrinho() {
		if(somadorCarrinhoFragment == null){
			somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, true);
		}else {
			somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
		}
	}

	private void adicionarNoCarrinho(CotasBolaoDTO bolao) {
	}

}