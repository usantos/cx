package br.gov.caixa.loterias.apostas.view.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.enums.FiltroBolaoResultadoEnum;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrosselBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.FiltroVazioFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.BolaoCarrosselHolder;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnEstadoVazioListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnVaiParaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

public class BolaoActivity extends LoteriasAppMarketPlaceActivity implements OnEstadoVazioListener, OnVaiParaCarrinhoListener, OnCompraBolaoListener {
	public static final String ARG_IDMODALIDADE = "ARG_IDMODALIDADE";

	private DiscreteScrollView carrosselView;
	private CarrosselBolaoAdapter carrosselAdapter;

	private RecyclerView listView;
	private ListaBolaoAdapter listAdapter;

	private BolaoModel model;
	private List<CotasBolaoDTO> bolaoList;
	private List<CotasBolaoDTO> carrosselList;
	private SomadorCarrinhoFragment somadorCarrinhoFragment;
	private ActivityResultLauncher<Intent> launcherFiltro;
	private ActivityResultLauncher<Intent> launcherAddCarrinho;
	private ActivityResultLauncher<Intent> launcherCarrinho;
	private ActivityResultLauncher<Intent> launcherDetalhes;

	private TextView todosBoloesTxt;
	private TextView toolBarTxt;
	private TextView txtFimLista;
	private int idMunicipio = -1;
	private int idUf = -1;
	private boolean acumulouBoloesParaApresentar;

	private NestedScrollView scrollView;
	private FrameLayout frameEstadoVazio;
	private FiltroVazioFragment filtroVazioFragment;
	private FiltroAplicadoMarketplace filtro;
	private List<CotasBolaoDTO> cotasDisponiveis;
	private List<CotasBolaoDTO> cotasFiltradas;

	private Integer idModalidade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bolao);
		configToolbar(R.id.toolbar);
		if (getIntent() != null && getIntent().getExtras() != null) {
			idModalidade = getIntent().getExtras().getInt(ARG_IDMODALIDADE);
		}
		launcherFiltro = IntentUtil.registerLauncherActivityForResult(this, OnActivityForResult());
		launcherAddCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
		launcherCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
		launcherDetalhes = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
		configFiltrar(launcherFiltro);
		setTitle("");
		toolBarTxt = findViewById(R.id.toolbar_title);
		toolBarTxt.setText(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.label_voltar_apostas_individuais)));
		toolBarTxt.setVisibility(View.VISIBLE);


		todosBoloesTxt = findViewById(R.id.txt_todos_boloes);

		listView = findViewById(R.id.lista_bolao);
		carrosselView = findViewById(R.id.bolao_carousel);
		scrollView = findViewById(R.id.id_scrollview);
		frameEstadoVazio = findViewById(R.id.id_fraf_estado_vazio);
		txtFimLista = findViewById(R.id.id_fim_lista);

		scrollView.setOnScrollChangeListener((NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
			View view = v.getChildAt(v.getChildCount() - 1);
			int  diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));
			// if diff is zero, then the bottom has been reached
			if (diff == 0) {
				if (!model.isAcabouRegistros()){
					buscarMaisBoloes();
				}
			}
		});

		model = new BolaoModel(this);

		fragmentSomadorCarrinho();

		AlertDialogUtils.show(this);
		model.buscaDadosUsuario(onBuscaDadosUsuarios());
	}

	private void buscaBoloes() {
		model.buscaBoloes(onBoloesListener());
	}

	private OnSilceListener<ApostadorDTO> onBuscaDadosUsuarios() {
		return new OnSilceListener<ApostadorDTO>() {
			@Override
			public void success(ApostadorDTO payload) {
				idMunicipio = payload.getMunicipioId().getNumero().intValue();
				idUf = payload.getMunicipioId().getIdUF().intValue();
				model.setIdMunicipioUF(idMunicipio, idUf);
				model.setIdModalidade(idModalidade);
				buscaBoloes();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				apresentanEstadoVazio();
			}
		};
	}

	private void initFragmentFiltroVazio() {
		if (filtroVazioFragment == null){
			filtroVazioFragment = FragmentUtils.startFiltroVazio(getSupportFragmentManager(), R.id.id_fraf_estado_vazio);
			frameEstadoVazio.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.GONE);
		} else {
			frameEstadoVazio.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.GONE);
		}

	}

	private void apresentanEstadoVazio() {
		initFragmentFiltroVazio();
		filtroVazioFragment.estadoVazio();
	}

	private void apresentaEstadoErro() {
		initFragmentFiltroVazio();
		filtroVazioFragment.estadoErro();
	}

	private OnSilceListener<ListaBolaoDTO> onBoloesListener() {
		return new OnSilceListener<ListaBolaoDTO>() {
			@Override
			public void success(ListaBolaoDTO payload) {

				if (payload == null) {
					payload = simulaPayloadZerado();
				}

				model.atualizaPaginacao(payload);
				setCotasDisponiveis(payload);

				if (naoTemOndeBuscar()){
					if (!payload.getCotas().isEmpty() || acumulouBoloesParaApresentar){
						preencheListas(cotasDisponiveis);
					}
					if (!cotasDisponiveis.isEmpty()){
						txtFimLista.setVisibility(View.VISIBLE);
					}

					if (cotasDisponiveis == null || cotasDisponiveis.isEmpty()){
						apresentanEstadoVazio();
					}
					AlertDialogUtils.dismiss();
				} else if (payload.getCotas().isEmpty() || payload.getCotas().size() < PaginacaoFiltro.QTD_POR_PAGINA){
					acumulouBoloesParaApresentar = true;
					buscarMaisBoloes();
				} else {
					preencheListas(cotasDisponiveis);
					AlertDialogUtils.dismiss();
				}

				verificaApresentaFimLista();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				apresentanEstadoVazio();
			}
		};
	}

	private ListaBolaoDTO simulaPayloadZerado() {
		ListaBolaoDTO payload = new ListaBolaoDTO();
		payload.setTotalRegistros(0);
		payload.setPaginaAtual(1);
		payload.setUltimaPagina(0);
		payload.setCotas(new ArrayList<CotasBolaoDTO>());
		return payload;
	}

	private OnSilceListener<ListaBolaoDTO> onBoloesFiltradosListener() {
		return new OnSilceListener<ListaBolaoDTO>() {
			@Override
			public void success(ListaBolaoDTO payload) {

				if (payload == null) {
					payload = simulaPayloadZerado();
				}

				model.atualizaPaginacao(payload);

				if (payload.getCotas().isEmpty()){
					if (naoTemOndeBuscar()){
						if (cotasFiltradas == null || cotasFiltradas.isEmpty()){
							apresentanEstadoVazio();
						} else {
							preencheListas(cotasFiltradas);
							if (!cotasFiltradas.isEmpty()){
								if (cotasFiltradas.size() > 4) {
									txtFimLista.setVisibility(View.VISIBLE);
								}
							}
						}
						AlertDialogUtils.dismiss();
					} else {
						acumulouBoloesParaApresentar = true;
						buscarMaisBoloes();
					}
				} else {
					setCotasFiltradas(payload);

					if (naoTemOndeBuscar()){
						preencheListas(cotasFiltradas);
						if (!cotasFiltradas.isEmpty()){
							if (cotasFiltradas.size() > 4) {
								txtFimLista.setVisibility(View.VISIBLE);
							}
						}
						AlertDialogUtils.dismiss();
					} else  if (payload.getCotas().size() < PaginacaoFiltro.QTD_POR_PAGINA){
						acumulouBoloesParaApresentar = true;
						buscarMaisBoloes();
					} else {
						preencheListas(cotasFiltradas);
						AlertDialogUtils.dismiss();
					}
				}

				verificaApresentaFimLista();
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				apresentaEstadoErro();
			}
		};
	}

	private void verificaApresentaFimLista() {
		if (bolaoList == null || bolaoList.isEmpty()) {
			todosBoloesTxt.setVisibility(View.INVISIBLE);
		} else {
			todosBoloesTxt.setVisibility(View.VISIBLE);
		}
	}

	private void preencheListas(List<CotasBolaoDTO> cotas) {
		carrosselList = model.getCarrosselList(cotas);
		carrosselAdapter = new CarrosselBolaoAdapter(BolaoActivity.this, carrosselList, onCarrosselItemListener());
		carrosselView.setAdapter(carrosselAdapter);
		carrosselView.setItemTransformer(new ScaleTransformer.Builder()
				.setMinScale(0.8f)
				.build());


		bolaoList = model.getBolaoList(cotas);
		listAdapter = new ListaBolaoAdapter(bolaoList, onItemBolaoListener(), BolaoActivity.this);
		listView.setAdapter(listAdapter);

		acumulouBoloesParaApresentar = false;
	}

	private void setCotasDisponiveis(ListaBolaoDTO payload) {
		if (cotasDisponiveis == null){
			cotasDisponiveis = payload.getCotas();
		} else {
			for (CotasBolaoDTO cota : payload.getCotas()){
				cotasDisponiveis.add(cota);
			}
		}
		cotasFiltradas = null;
	}

	private void setCotasFiltradas(ListaBolaoDTO payload) {
		if (cotasFiltradas == null){
			cotasFiltradas = payload.getCotas();
		} else {
			for (CotasBolaoDTO cota : payload.getCotas()){
				cotasFiltradas.add(cota);
			}
		}
		cotasDisponiveis = null;
	}

	private void buscarMaisBoloes() {
		if (filtro != null){
			buscaBoloesFiltro();
		} else {
			buscaBoloes();
		}
	}

	private boolean naoTemOndeBuscar() {
		return model.isAcabouRegistros();
	}

	private OnActivityForResult OnActivityForResult() {
		return result -> {
			frameEstadoVazio.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);

			if (result.getResultCode() == Activity.RESULT_OK) {
				Intent data = result.getData();
				filtro = (FiltroAplicadoMarketplace) data.getSerializableExtra(FiltraMarketplaceActivity.FILTRO_MARKETPLACE_RETORNO);

				AlertDialogUtils.show(BolaoActivity.this);
				model.zeraPaginacao();
				cotasFiltradas = null;

				buscaBoloesFiltro();
			}
		};
	}

	private OnActivityForResult onAtualizaListasDeCotas() {
		return result -> {
			if (result.getResultCode() == Activity.RESULT_OK) {
				AlertDialogUtils.show(BolaoActivity.this);
				model.zeraPaginacao();
				if (filtro != null){
					cotasFiltradas = null;
					buscaBoloesFiltro();
				} else {
					cotasDisponiveis = null;
					buscaBoloes();
				}
			}
		};
	}

	private void buscaBoloesFiltro(){
		model.buscaFiltradaBoloes(filtro, onBoloesFiltradosListener());
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
		holder.atualizaQuantidadeTextView(list.get(position).getQtdCotasBolao(), list.get(position).getQtdCotaDisponivel());
		holder.atualizaValorTotal(list.get(position));
	}

	private void diminuiQuatidade(List<CotasBolaoDTO> list, int position, OnViewAumentaDiminuiQuantidade holder) {
		list.get(position).diminuiQuantidade();
		holder.atualizaQuantidadeTextView(list.get(position).getQtdCotasBolao(), list.get(position).getQtdCotaDisponivel());
		holder.atualizaValorTotal(list.get(position));
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
		Bundle bundle = new Bundle();
		bundle.putString(DetalhesBolaoActivity.ARG_CODIGO_BOLAO, bolao.getCodigoBolao());
		bundle.putString(DetalhesBolaoActivity.ARG_MODALIDADE, new Gson().toJson(bolao.getModalidade()));
		bundle.putString(DetalhesBolaoActivity.ARG_MODO_VISUALIZACAO, new Gson().toJson(ModoVisualizacaoBolaoEnum.SIMULACAO));
		Intent intent = IntentUtil.getIntentOrigemDestino(this, DetalhesBolaoActivity.class, bundle);
		IntentUtil.startActivityForResult(launcherDetalhes, intent);
	}

	private void adicionarNoCarrinho(CotasBolaoDTO bolao) {
		model.addBolaoCarrinho(bolao);
	}

	@Override
	public void alterar(FiltroBolaoResultadoEnum tipoFiltro) {
		switch (tipoFiltro){
			case VAZIO:
				IntentUtil.startActivityForResult(launcherFiltro, BolaoActivity.this,
						FiltraMarketplaceActivity.class);
				break;
			case ERRO:
				buscaBoloesFiltro();
				break;
		}
	}

	@Override
	public void continuar(FiltroBolaoResultadoEnum tipoFiltro) {
		switch (tipoFiltro){
			case VAZIO:
				frameEstadoVazio.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				break;
			case ERRO:
				buscaBoloes();
				break;
		}
	}

	@Override
	public void vaiParaCarrinho() {
		IntentUtil.startActivityForResult(launcherCarrinho, IntentUtil.getIntentOrigemDestino(BolaoActivity. this, CarrinhoActivity.class));
	}

	@Override
	public void compraSucesso(Intent intent) {
		IntentUtil.startActivityForResult(launcherAddCarrinho, intent);
	}
}