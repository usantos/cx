package br.gov.caixa.loterias.apostas.view.activity;

import android.app.Dialog;
import android.widget.*;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static br.gov.caixa.loterias.apostas.model.bo.BaseBO.transformaErroEmErrorResponse;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import br.gov.caixa.loterias.apostas.model.bean.ErrorResponse;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
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
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnVaiParaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

public class BolaoActivity extends LoteriasAppMarketPlaceActivity implements OnVaiParaCarrinhoListener, OnCompraBolaoListener {
	public static final String ARG_IDMODALIDADE = "ARG_IDMODALIDADE";

	public static final String ARG_TIPO_CONCURSO = "ARG_TIPO_CONCURSO";

	public static final String ARG_FILTRO_MARKETPLACE = "ARG_FILTRO_MARKETPLACE";

	private DiscreteScrollView carrosselView;
	private CarrosselBolaoAdapter carrosselAdapter;

	private RecyclerView listView;
	private ProgressBar loading;
	private ListaBolaoAdapter listAdapter;

	private BolaoModel model;
	private List<CotasBolaoDTO> bolaoList;
	private List<CotasBolaoDTO> carrosselList;
	private SomadorCarrinhoFragment somadorCarrinhoFragment;
	private ActivityResultLauncher<Intent> launcherFiltro;
	private ActivityResultLauncher<Intent> launcherAddCarrinho;
	private ActivityResultLauncher<Intent> launcherCarrinho;
	private ActivityResultLauncher<Intent> launcherDetalhes;

	private TextView toolBarTxt;
	private TextView txtFimLista;
	private int idMunicipio = -1;
	private int idUf = -1;
	private boolean acumulouBoloesParaApresentar, isFiltrando = false;

	private NestedScrollView scrollView;
	private FrameLayout frameEstadoVazio;
	private FiltroVazioFragment filtroVazioFragment;
	private FiltroAplicadoMarketplace filtro = new FiltroAplicadoMarketplace();
	private List<CotasBolaoDTO> cotasDisponiveis;

	private Integer idModalidade, idModalidadeInicial;
	private Integer tipoConcurso, tipoConcursoInicial;

	private int itensCardslVisiveis = 0;
	private ImageView[] itemPaginacao;
	private LinearLayout layoutPaginacaoCarrossel;

	private RadioButton btnBoloes, btnLotericasFavoritas;

	private ConstraintLayout layoutBoloes, layoutLotericasFavoritas;

	private ParametroJogoDTO parametroJogo;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bolao);
		configToolbar(R.id.toolbar);
		if (getIntent() != null && getIntent().getExtras() != null) {
			idModalidade = getIntent().getExtras().getInt(ARG_IDMODALIDADE);
			tipoConcurso = getIntent().getExtras().getInt(ARG_TIPO_CONCURSO);
			idModalidadeInicial = idModalidade;
			tipoConcursoInicial = tipoConcurso;
			loadParametroJogo(ModalidadeEnum.fromInteger(idModalidade));
		}
		launcherFiltro = IntentUtil.registerLauncherActivityForResult(this, OnActivityForResult());
		launcherAddCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
		launcherCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
		launcherDetalhes = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
		filtro.setModalidade(ModalidadeEnum.fromInteger(idModalidadeInicial));
		filtro.setTipoConcurso(TipoConcursoEnum.fromInteger(tipoConcursoInicial));
		configFiltrar(launcherFiltro,filtro);
		setTitle("");
		toolBarTxt = findViewById(R.id.toolbar_title);
		toolBarTxt.setText(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.label_bolao_bar_marketplace)));
		toolBarTxt.setVisibility(VISIBLE);


		listView = findViewById(R.id.lista_bolao);
		carrosselView = findViewById(R.id.bolao_carousel);
		scrollView = findViewById(R.id.id_scrollview);
		frameEstadoVazio = findViewById(R.id.id_fraf_estado_vazio);
		txtFimLista = findViewById(R.id.id_fim_lista);
		loading = findViewById(R.id.progress_loading);

		layoutPaginacaoCarrossel = findViewById(R.id.layout_paginacao_carousel);


		btnBoloes = findViewById(R.id.btn_boloes);
		btnLotericasFavoritas = findViewById(R.id.btn_lotericas_favoritas);
		layoutBoloes = findViewById(R.id.contentBoloes);
		layoutLotericasFavoritas = findViewById(R.id.contentLotericasFavoritas);

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

        carrosselView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
			@Override
			public void onCurrentItemChanged(@Nullable @org.jetbrains.annotations.Nullable RecyclerView.ViewHolder viewHolder, int position) {
				AlertDialogUtils.dismiss();
				//adapter aqui retorna o índice do carrossel
				int posicaoReal = getPosicaoReal(position);

				atualizaIndicadorPaginacaoCarrossel(posicaoReal);
			}
		});

        model = new BolaoModel(this);
		btnBoloes.setOnClickListener(v -> seletorBoloes());
		btnLotericasFavoritas.setOnClickListener(v -> seletorLotericasFavoritas());

		model = new BolaoModel(this);

		fragmentSomadorCarrinho();

		AlertDialogUtils.show(this);
		model.buscaDadosUsuario(onBuscaDadosUsuarios());

	}


	private void loadParametroJogo(ModalidadeEnum modalidade) {
		SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
		parametroJogo = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidade).getParametroJogo();
	}

	private void preencheBarraTitulo(ModalidadeEnum modalidade, TipoConcursoEnum tipoConcurso) {

		if (filtro != null && filtro.getLotericaDTO() != null && filtro.getLotericaDTO().getCodigo() != null) {
			FragmentUtils.startFragmentBarraTituloFiltroLoterica(getSupportFragmentManager(), R.id.barra_titulo_mkp, filtro);
			selectFiltroLoterica();
		} else {
			EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
			boolean isEspecial = tipoConcurso == TipoConcursoEnum.ESPECIAL;
			FragmentUtils.startFragmentBarraTituloBolao(getSupportFragmentManager(), R.id.barra_titulo_mkp,
					modalidade, parametroJogo.getConcurso().getNumero().toString(), getDataSorteio(), parametroJogo.getConcurso().getEstimativa(), isEspecial, estilo.getCorClara(), estilo.getCorEscura());
			unSelectFiltroLoterica();
		}
	}

	private String getDataSorteio() {
		String dataSorteio = "";
		SimpleDateFormat formatterDateFinal = new SimpleDateFormat(getResources().getString(R.string.dd_mm));
		DateFormat formatterDate = new SimpleDateFormat(getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));

		try {
			if (parametroJogo != null && parametroJogo.getConcurso() != null && parametroJogo.getConcurso().getDataHoraSorteio() != null){
				Date dataFormatada = formatterDate.parse(parametroJogo.getConcurso().getDataHoraSorteio());
				dataSorteio = formatterDateFinal.format(dataFormatada);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dataSorteio;
	}


	private void seletorLotericasFavoritas() {
		btnLotericasFavoritas.setChecked(Boolean.TRUE);
		layoutLotericasFavoritas.setVisibility(VISIBLE);
		unSelectBoloes();
	}

	private void seletorBoloes() {
		btnBoloes.setChecked(Boolean.TRUE);
		layoutBoloes.setVisibility(VISIBLE);
		unSelectLotericasFavoritas();
	}

	private void unSelectBoloes() {
		btnBoloes.setChecked(Boolean.FALSE);
		layoutBoloes.setVisibility(GONE);
	}
	private void unSelectLotericasFavoritas() {
		btnLotericasFavoritas.setChecked(Boolean.FALSE);
		layoutLotericasFavoritas.setVisibility(GONE);
	}

	private void selectFiltroLoterica() {
		btnBoloes.setText(R.string.label_boloes_loterica);
	}
	private void unSelectFiltroLoterica() {
		btnBoloes.setText(R.string.label_boloes);
	}


	private int getPosicaoReal(int posicao){
		if(itensCardslVisiveis == 0) return 0;
		return posicao % itensCardslVisiveis;
	}

	private void setupPaginacaoCarrossel(){

		itemPaginacao = new ImageView[itensCardslVisiveis];
		layoutPaginacaoCarrossel.removeAllViews(); //Limpa os itens caso ja existam

		for(int i = 0; i < itensCardslVisiveis; i++){
			itemPaginacao[i] = new ImageView(this);
			itemPaginacao[i].setImageResource(R.drawable.pontovazio);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT
			);

			params.setMargins(8,0,8,0);

			layoutPaginacaoCarrossel.addView(itemPaginacao[i], params);
		}

		//Ativa o primeiro ponto inicialmente
		if(itemPaginacao.length > 0){
			itemPaginacao[0].setImageResource(R.drawable.pontopreenchido);
		}

	}

	private void atualizaIndicadorPaginacaoCarrossel(int posicaoAtual) {
		for (int i = 0; i < itemPaginacao.length; i++) {
			if (i == posicaoAtual) {
				itemPaginacao[i].setImageResource(R.drawable.pontopreenchido);
			} else {
				itemPaginacao[i].setImageResource(R.drawable.pontovazio);
			}
		}
	}

	private void buscaBoloes() {
		AlertDialogUtils.show(BolaoActivity.this);
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
				model.setIdTipoConcurso(tipoConcurso);
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
			frameEstadoVazio.setVisibility(VISIBLE);
			scrollView.setVisibility(GONE);
		} else {
			frameEstadoVazio.setVisibility(VISIBLE);
			scrollView.setVisibility(GONE);
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
					loading.setVisibility(GONE);
					if (!payload.getCotas().isEmpty() || acumulouBoloesParaApresentar){
						preencheListas(cotasDisponiveis);
					}
					if (!cotasDisponiveis.isEmpty()){
						txtFimLista.setVisibility(VISIBLE);
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
					loading.setVisibility(GONE);
				}

				if(cotasDisponiveis != null && !cotasDisponiveis.isEmpty()){
					preencheBarraTitulo(ModalidadeEnum.fromInteger(idModalidade), TipoConcursoEnum.fromInteger(tipoConcurso));
				}

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
						if (cotasDisponiveis == null || cotasDisponiveis.isEmpty()){
							apresentanEstadoVazio();
						} else {
							preencheListas(cotasDisponiveis);
							if (!cotasDisponiveis.isEmpty()){
								if (cotasDisponiveis.size() > 4) {
									txtFimLista.setVisibility(VISIBLE);
								}
							}
						}
						loading.setVisibility(GONE);
						AlertDialogUtils.dismiss();
					} else {
						acumulouBoloesParaApresentar = true;
						buscarMaisBoloes();
					}
				} else {
					setCotasDisponiveis(payload);

					if (naoTemOndeBuscar()){
						preencheListas(cotasDisponiveis);
						if (!cotasDisponiveis.isEmpty()){
							if (cotasDisponiveis.size() > 4) {
								txtFimLista.setVisibility(VISIBLE);
							}
						}
						loading.setVisibility(GONE);
						AlertDialogUtils.dismiss();
					} else  if (payload.getCotas().size() < PaginacaoFiltro.QTD_POR_PAGINA){
						acumulouBoloesParaApresentar = true;
						buscarMaisBoloes();
					} else {
						preencheListas(cotasDisponiveis);
						loading.setVisibility(GONE);
						AlertDialogUtils.dismiss();
					}
				}

				if(cotasDisponiveis != null && !cotasDisponiveis.isEmpty()){
					preencheBarraTitulo(ModalidadeEnum.fromInteger(idModalidade), TipoConcursoEnum.fromInteger(tipoConcurso));
				}
			}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				apresentaEstadoErro();
			}
		};
	}


	private void preencheListas(List<CotasBolaoDTO> cotas) {
		carrosselList = model.getCarrosselList(cotas);
		itensCardslVisiveis = carrosselList.size();
		setupPaginacaoCarrossel();
		carrosselAdapter = new CarrosselBolaoAdapter(BolaoActivity.this, carrosselList, onCarrosselItemListener());
		carrosselView.setAdapter(carrosselAdapter);
		carrosselView.setItemTransformer(new ScaleTransformer.Builder()
				.setMinScale(0.8f)
				.build());

		if (listAdapter == null){
			bolaoList = model.getBolaoList(cotas);
			listAdapter = new ListaBolaoAdapter(bolaoList, onItemBolaoListener(), BolaoActivity.this);
			listView.setAdapter(listAdapter);
		} else {
			int inicioRange = bolaoList.size();
			bolaoList = model.getBolaoList(cotas);
			if (inicioRange < bolaoList.size()){
				listAdapter.atualizaLista(inicioRange, bolaoList.subList(inicioRange, bolaoList.size()));
			}
		}


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
	}

	private void buscarMaisBoloes() {
		loading.setVisibility(VISIBLE);
		if (isFiltrando){
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
			frameEstadoVazio.setVisibility(GONE);
			scrollView.setVisibility(VISIBLE);

			if (result.getResultCode() == Activity.RESULT_OK) {
				Intent data = result.getData();
				if (data != null) {
					filtro = (FiltroAplicadoMarketplace) data.getSerializableExtra(FiltraMarketplaceActivity.FILTRO_MARKETPLACE_RETORNO);
					if (filtro != null) {
						isFiltrando = true;
						configFiltrar(launcherFiltro, filtro);
						preencheBarraTitulo(filtro.getModalidade(), filtro.getTipoConcurso());
					}
				}
				AlertDialogUtils.show(BolaoActivity.this);
				model.zeraPaginacao();
				cotasDisponiveis = null;

				buscaBoloesFiltro();
				listAdapter = null;
				txtFimLista.setVisibility(GONE);
				scrollView.scrollTo(0,0);
			}
		};
	}

	private OnActivityForResult onAtualizaListasDeCotas() {
		return result -> {
			if (result.getResultCode() == Activity.RESULT_OK) {
				AlertDialogUtils.show(BolaoActivity.this);
				model.zeraPaginacao();
				listAdapter = null;
				cotasDisponiveis = null;
				if (isFiltrando){
					buscaBoloesFiltro();
				} else {
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

			@Override
			public void onFavoritarClick(int position, boolean isFavoritado) {
				favoritar(carrosselList.get(position), isFavoritado);
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
			@Override
			public void onFavoritarClick(int position, boolean isFavoritado) {
				favoritar(bolaoList.get(position), isFavoritado);
			}
		};
	}

	private void favoritar(CotasBolaoDTO bolao, boolean isFavoritado) {

		Long lotericaId = bolao.getLoterica();

		boolean estadoAnterior = bolao.isLotericaFavorita();

		String mensagem = "";

		if(isFavoritado) {
			mensagem = "Deseja incluir " + bolao.getNomeFantasia() + " da sua lista de favoritas?";

		}else {
			mensagem = "Deseja excluir " + bolao.getNomeFantasia() + " na sua lista de favoritas?";

		}
		abrePopupConfirmacaoLotericasFavoritas(mensagem, lotericaId, estadoAnterior);
	}


	private void abrePopupConfirmacaoLotericasFavoritas(String mensagem, Long lotericaId, boolean estadoAnterior) {

		Dialog dialogConfirmacao = ViewUtils.alertTitleNewPositiveNegativeListener(
				this,
				getString(R.string.label_atencao),
				getString(R.string.cancelar),
				getString(R.string.confirmar),
				mensagem,
				(dialog, which) ->{
					AlertDialogUtils.show(this);
					if(estadoAnterior){
						//Atualiza imediatamente o estado do favorito na lista
						model.excluirLotericaFavorita(lotericaId, excluirfavoritaListener(lotericaId, estadoAnterior));
					}else{
						model.incluirLotericaFavorita(lotericaId, incluirfavoritaListener(lotericaId, estadoAnterior));
					}
					atualizaFavoritoTodasListas(lotericaId, !estadoAnterior);
				},
				(dialog, which) -> {
					dialog.dismiss();
					AlertDialogUtils.show(this);
					atualizaFavoritoTodasListas(lotericaId, estadoAnterior);
				}
		);
		dialogConfirmacao.show();
	}

	private OnSilceListener<RetornoPadraoResponse> incluirfavoritaListener(Long lotericaId, boolean estadoAnterior) {
		return new OnSilceListener<RetornoPadraoResponse>() {
			@Override
			public void success(RetornoPadraoResponse payload) {
				//Sucesso na alteração do favorito, nada a fazer pois o estado já foi atualizado
				AlertDialogUtils.dismiss();		}

			@Override
			public void error(VolleyError error) {
				AlertDialogUtils.dismiss();
				try{
					ErrorResponse errorResponse =transformaErroEmErrorResponse(error);
					if(errorResponse.getCodigo().equals("002024")){
						ViewUtils.alertEntendiCustom(BolaoActivity.this, errorResponse.getMensagem());
					}else{
						ViewUtils.alertTitleButton(BolaoActivity.this, R.string.label_atencao, getResources().getString(R.string.nao_concluiu_operacao),
								"Ok", dialogInterface -> {});

					};

				}catch (Exception e){
					e.printStackTrace();
				}
				atualizaFavoritoTodasListas(lotericaId, estadoAnterior);
			}
		};
	}


	private OnSilceListener<RetornoPadraoResponse> excluirfavoritaListener(Long lotericaId, boolean estadoAnterior) {
		return new OnSilceListener<RetornoPadraoResponse>() {
			@Override
			public void success(RetornoPadraoResponse payload) {
				//Sucesso na alteração do favorito, nada a fazer pois o estado já foi atualizado
				AlertDialogUtils.dismiss();
			}

			@Override
			public void error(VolleyError error) {
				//Reverte o estado do favorito em caso de erro
				ViewUtils.alertTitleButton(BolaoActivity.this, R.string.label_atencao, getResources().getString(R.string.nao_concluiu_operacao),
						"Ok", dialogInterface -> {});
				atualizaFavoritoTodasListas(lotericaId, estadoAnterior);
				AlertDialogUtils.dismiss();
			}
		};
	}

	private void atualizaFavoritoTodasListas(Long lotericaId, boolean isFavorita) {
		//Atualiza na lista do carrossel
		for (CotasBolaoDTO bolao : carrosselList) {
			if (bolao.getLoterica().equals(lotericaId)) {
				bolao.setLotericaFavorita(isFavorita);
			}
		}
		if (carrosselAdapter != null) {
			carrosselAdapter.notifyDataSetChanged();
		}

		//Atualiza na lista principal
		for (CotasBolaoDTO bolao : bolaoList) {
			if (bolao.getLoterica().equals(lotericaId)) {
				bolao.setLotericaFavorita(isFavorita);
			}
		}
		if (listAdapter != null) {
			listAdapter.notifyDataSetChanged();
		}
		AlertDialogUtils.dismiss();
	}


	private void detalhesBolao(CotasBolaoDTO bolao) {
		Bundle bundle = new Bundle();
		bundle.putString(DetalhesBolaoActivity.ARG_CODIGO_BOLAO, bolao.getCodigoBolao());
		bundle.putString(DetalhesBolaoActivity.ARG_MODALIDADE, new Gson().toJson(bolao.getModalidade()));
		bundle.putString(DetalhesBolaoActivity.ARG_MODO_VISUALIZACAO, new Gson().toJson(ModoVisualizacaoBolaoEnum.SIMULACAO));
		bundle.putBoolean(DetalhesBolaoActivity.ARG_IS_LOTERICA_FAVORITA, bolao.isLotericaFavorita());
		Intent intent = IntentUtil.getIntentOrigemDestino(this, DetalhesBolaoActivity.class, bundle);
		IntentUtil.startActivityForResult(launcherDetalhes, intent);
	}

	private void adicionarNoCarrinho(CotasBolaoDTO bolao) {
		model.addBolaoCarrinho(bolao);
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