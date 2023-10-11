package br.gov.caixa.loterias.apostas.view.activity;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Bolao;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrosselBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;

public class BolaoActivity extends LoteriasAppMarketPlaceActivity {

	private DiscreteScrollView carrosselView;
	private CarrosselBolaoAdapter carrosselAdapter;

	private RecyclerView listView;
	private ListaBolaoAdapter listAdapter;

	private BolaoModel model;
	private List<Bolao> bolaoList;
	private List<Bolao> carrosselList;
	private SomadorCarrinhoFragment somadorCarrinhoFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bolao);
		configToolbar(R.id.toolbar);
		configFiltrar();
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_Voltar Apostas Individuais_"));

		listView = findViewById(R.id.lista_bolao);

		model = new BolaoModel(this);

		bolaoList = model.getListBolao();
		listAdapter = new ListaBolaoAdapter(bolaoList, onItemBolaoListener(), this);
		listView.setAdapter(listAdapter);

		fragmentSomadorCarrinho();
	}

	private void fragmentSomadorCarrinho() {
		if(somadorCarrinhoFragment == null){
			somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, true);
		}else {
			somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
		}
	}

	private OnItemBolaoListener onItemBolaoListener() {
		return new OnItemBolaoListener() {
			@Override
			public void verMais(BolaoHolder holder, int position) {
				bolaoList.get(position).setAberto(!bolaoList.get(position).isAberto());

				holder.mostrarDetalhes(bolaoList.get(position).isAberto());
			}

			@Override
			public void detalhes(int position) {
				TestVisao.toast("Funcionalidade ainda nao implementada!");
			}

			@Override
			public void adicionarCarrinho(int position) {
				TestVisao.toast("Funcionalidade ainda nao implementada!");
			}

			@Override
			public void diminui(BolaoHolder holder, int position) {
				bolaoList.get(position).diminuiQuantidade();
				TestVisao.toast("Quantidade: " + bolaoList.get(position).getQuantidade());
				holder.atualizaQuantidadeTextView(bolaoList.get(position).getQuantidade(), bolaoList.get(position).getQtdPermitida());
			}

			@Override
			public void aumenta(BolaoHolder holder, int position) {
				bolaoList.get(position).aumentaQuantidade();
				TestVisao.toast("Quantidade: " + bolaoList.get(position).getQuantidade());
				holder.atualizaQuantidadeTextView(bolaoList.get(position).getQuantidade(), bolaoList.get(position).getQtdPermitida());
			}
		};
	}

}