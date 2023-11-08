package br.gov.caixa.loterias.apostas.view.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.TestVisao;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrosselBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.BolaoCarrosselHolder;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

public class BolaoActivity extends LoteriasAppMarketPlaceActivity {

	private DiscreteScrollView carrosselView;
	private CarrosselBolaoAdapter carrosselAdapter;

	private RecyclerView listView;
	private ListaBolaoAdapter listAdapter;

	private BolaoModel model;
	private List<CotasBolaoDTO> bolaoList;
	private List<CotasBolaoDTO> carrosselList;
	private SomadorCarrinhoFragment somadorCarrinhoFragment;
	private ActivityResultLauncher<Intent> launcher;

	private TextView todosBoloesTxt;
	private int idMunicipio;
	private int idUf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bolao);
		configToolbar(R.id.toolbar);
		launcher = IntentUtil.registerLauncherActivityForResult(this, OnActivityForResult());
		configFiltrar(launcher);
		setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, "_Voltar Apostas Individuais_"));

		todosBoloesTxt = findViewById(R.id.txt_todos_boloes);

		listView = findViewById(R.id.lista_bolao);
		carrosselView = findViewById(R.id.bolao_carousel);

		model = new BolaoModel(this);

		AlertDialogUtils.show(this);
		model.buscaDadosUsuario(onBuscaDadosUsuarios());

		fragmentSomadorCarrinho();
	}

	private OnSilceListener<ApostadorDTO> onBuscaDadosUsuarios() {
		return new OnSilceListener<ApostadorDTO>() {
			@Override
			public void success(ApostadorDTO payload) {
				idMunicipio = payload.getMunicipioId().getNumero().intValue();
				idUf = payload.getMunicipioId().getIdUF().intValue();

				model.buscaBoloes(idMunicipio, idUf, onBoloesListener());
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				// montar estado vazio da tela
			}
		};
	}

	private OnSilceListener<ListaBolaoDTO> onBoloesListener() {
		return new OnSilceListener<ListaBolaoDTO>() {
			@Override
			public void success(ListaBolaoDTO payload) {
				carrosselList = model.getCarrosselList(payload.getCotas());
				carrosselAdapter = new CarrosselBolaoAdapter(BolaoActivity.this, carrosselList, onCarrosselItemListener());
				carrosselView.setAdapter(carrosselAdapter);
				carrosselView.setItemTransformer(new ScaleTransformer.Builder()
														 .setMinScale(0.8f)
														 .build());


				bolaoList = model.getBolaoList(payload.getCotas());
				listAdapter = new ListaBolaoAdapter(bolaoList, onItemBolaoListener(), BolaoActivity.this);
				listView.setAdapter(listAdapter);

				if (bolaoList.isEmpty()){
					todosBoloesTxt.setVisibility(View.INVISIBLE);
				}

				AlertDialogUtils.dismiss();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				// montar estado vazio da tela
			}
		};
	}

	private OnActivityForResult OnActivityForResult() {
		return result -> {
			if (result.getResultCode() == Activity.RESULT_OK) {
				Intent data = result.getData();
				FiltroAplicadoMarketplace filtro = (FiltroAplicadoMarketplace) data.getSerializableExtra(FiltraMarketplaceActivity.FILTRO_MARKETPLACE_RETORNO);

				AlertDialogUtils.show(BolaoActivity.this);
				model.buscaFiltradaBoloes(idMunicipio, idUf, filtro, onBoloesListener());
			}
		};
	}

	private OnItemCarrosselBolaoListener onCarrosselItemListener() {
		return new OnItemCarrosselBolaoListener() {
			@Override
			public void detalhes(int position) {
				detalhesBolao(carrosselList.get(position));
			}

			@Override
			public void adicionarCarrinho(int position) {
				adicionarNoCarrinho(carrosselList.get(position));
			}

			@Override
			public void diminui(BolaoCarrosselHolder holder, int position) {
				diminuiQuatidade(carrosselList, position, (BolaoCarrosselHolder)holder);
			}

			@Override
			public void aumenta(BolaoCarrosselHolder holder, int position) {
				aumentaQuatidade(carrosselList, position, holder);
			}
		};
	}

	private void aumentaQuatidade(List<CotasBolaoDTO> list, int position, OnViewAumentaDiminuiQuantidade holder) {
		list.get(position).aumentaQuantidade();
		holder.atualizaQuantidadeTextView(list.get(position).getQtdCotaReservada(), list.get(position).getQtdCotaDisponivel());
	}

	private void diminuiQuatidade(List<CotasBolaoDTO> list, int position, OnViewAumentaDiminuiQuantidade holder) {
		list.get(position).diminuiQuantidade();
		holder.atualizaQuantidadeTextView(list.get(position).getQtdCotaReservada(), list.get(position).getQtdCotaDisponivel());
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
				bolaoList.get(position).setAbertoParaMostrarDetalhes(!bolaoList.get(position).isAbertoParaMostrarDetalhes());
				holder.mostrarDetalhes(bolaoList.get(position).isAbertoParaMostrarDetalhes());
			}

			@Override
			public void detalhes(int position) {
				detalhesBolao(bolaoList.get(position));
			}

			@Override
			public void adicionarCarrinho(int position) {
				adicionarNoCarrinho(bolaoList.get(position));
			}

			@Override
			public void diminui(BolaoHolder holder, int position) {
				diminuiQuatidade(bolaoList, position, holder);
			}

			@Override
			public void aumenta(BolaoHolder holder, int position) {
				aumentaQuatidade(bolaoList, position, holder);
			}
		};
	}

	private void detalhesBolao(CotasBolaoDTO bolao) {
		TestVisao.toast("Funcionalidade ainda nao implementada!");
	}

	private void adicionarNoCarrinho(CotasBolaoDTO bolao) {
		TestVisao.toast("Funcionalidade ainda nao implementada!");
	}

}