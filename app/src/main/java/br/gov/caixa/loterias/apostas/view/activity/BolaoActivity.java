package br.gov.caixa.loterias.apostas.view.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.enums.FiltroBolaoResultadoEnum;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.model.model.LotericaFavoritaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrosselBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaLotericasAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.FiltroVazioFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.BolaoCarrosselHolder;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.holder.LotericasFavoritasHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnEstadoVazioListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemLotericaFavoritaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnVaiParaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

public class BolaoActivity extends LoteriasAppMarketPlaceActivity implements OnEstadoVazioListener, OnVaiParaCarrinhoListener, OnCompraBolaoListener {
	public static final String ARG_IDMODALIDADE = "ARG_IDMODALIDADE";

	public static final String ARG_TIPO_CONCURSO = "ARG_TIPO_CONCURSO";

	public static final String ARG_FILTRO_MARKETPLACE = "ARG_FILTRO_MARKETPLACE";

	private DiscreteScrollView carrosselView;
	private CarrosselBolaoAdapter carrosselAdapter;

	private RecyclerView listView;
    private RecyclerView lotericasListView;
	private ProgressBar loading;
	private ListaBolaoAdapter listAdapter;
    private ListaLotericasAdapter lotericasAdapter;

	private BolaoModel model;
    private LotericaFavoritaModel lotericaModel;
	private List<CotasBolaoDTO> bolaoList;
    private List<LotericaFavoritaDTO> lotericasList;
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
        lotericasListView = findViewById(R.id.lista_lotericas);
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

		lotericaModel = new LotericaFavoritaModel(this);

		fragmentSomadorCarrinho();

		AlertDialogUtils.show(this);
		model.buscaDadosUsuario(onBuscaDadosUsuarios());

		preencheBarraTitulo(ModalidadeEnum.fromInteger(idModalidadeInicial), TipoConcursoEnum.fromInteger(tipoConcurso));
    }

	private void loadParametroJogo(ModalidadeEnum modalidade) {
		SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
		parametroJogo = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidade).getParametroJogo();
	}

	private void preencheBarraTitulo(ModalidadeEnum modalidade, TipoConcursoEnum tipoConcurso) {
		EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
		boolean isEspecial = tipoConcurso == TipoConcursoEnum.ESPECIAL;
		FragmentUtils.startFragmentBarraTituloBolao(getSupportFragmentManager(), R.id.barra_titulo_mkp,
				modalidade, parametroJogo.getConcurso().getNumero().toString(), getDataSorteio(), parametroJogo.getConcurso().getEstimativa(),  isEspecial, estilo.getCorClara(), estilo.getCorEscura());
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

    private void buscaLotericas(){
        lotericaModel.buscaLotericas(new OnSilceListener<List<LotericaFavoritaDTO>>() {
            @Override
            public void success(List<LotericaFavoritaDTO> payload) {
                if (payload == null) {
                    payload = simulaPayloadLoterica();
                }
                if (lotericasAdapter == null) {
                    setLotericasFavoritas(payload);
                    configuraListView(lotericasList);
                }
            }

            @Override
            public void error(VolleyError error) {

            }
        });
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
                buscaLotericas();
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

    private OnSilceListener<List<LotericaFavoritaDTO>> onLotericasListener(){

        return new OnSilceListener<List<LotericaFavoritaDTO>>() {
            @Override
            public void success(List<LotericaFavoritaDTO> payload) {
                if (payload == null) {
                    payload = simulaPayloadLoterica();
                }
                if (lotericasAdapter == null) {
                    setLotericasFavoritas(payload);
                    configuraListView(lotericasList);
                }
            }

            @Override
            public void error(VolleyError error) {

            }
        };
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

    private List<LotericaFavoritaDTO> simulaPayloadLoterica(){
        List<LotericaFavoritaDTO> payload = new List<LotericaFavoritaDTO>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<LotericaFavoritaDTO> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] ts) {
                return null;
            }

            @Override
            public boolean add(LotericaFavoritaDTO lotericaFavoritaDTO) {
                return false;
            }

            @Override
            public boolean remove(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends LotericaFavoritaDTO> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, @NonNull Collection<? extends LotericaFavoritaDTO> collection) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public LotericaFavoritaDTO get(int i) {
                return null;
            }

            @Override
            public LotericaFavoritaDTO set(int i, LotericaFavoritaDTO lotericaFavoritaDTO) {
                return null;
            }

            @Override
            public void add(int i, LotericaFavoritaDTO lotericaFavoritaDTO) {

            }

            @Override
            public LotericaFavoritaDTO remove(int i) {
                return null;
            }

            @Override
            public int indexOf(@Nullable Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(@Nullable Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<LotericaFavoritaDTO> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<LotericaFavoritaDTO> listIterator(int i) {
                return null;
            }

            @NonNull
            @Override
            public List<LotericaFavoritaDTO> subList(int i, int i1) {
                return Collections.emptyList();
            }
        };
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

    private void setLotericasFavoritas(List<LotericaFavoritaDTO> payload) {
        lotericaModel.setListLotericas(payload);
        if (lotericasList == null){
            lotericasList = payload;
        } else {
            lotericasList.addAll(payload);
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
				diminuiQuantidade(carrosselList, position, (BolaoCarrosselHolder)holder);
			}

			@Override
			public void aumenta(BolaoCarrosselHolder holder, int position) {
				aumentaQuantidade(carrosselList, position, holder);
			}
		};
	}

	private void aumentaQuantidade(List<CotasBolaoDTO> list, int position, OnViewAumentaDiminuiQuantidade holder) {
		list.get(position).aumentaQuantidade();
		holder.atualizaQuantidadeTextView(list.get(position).getQtdCotasBolao(), list.get(position).getQtdCotaDisponivel());
		holder.atualizaValorTotal(list.get(position));
	}

	private void diminuiQuantidade(List<CotasBolaoDTO> list, int position, OnViewAumentaDiminuiQuantidade holder) {
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

    private OnItemLotericaFavoritaListener onItemLotericaFavoritaListener() {
        return new OnItemLotericaFavoritaListener() {
            @Override
            public void verMais(LotericasFavoritasHolder holder, int position) {
            }

            @Override
            public void excluir(LotericasFavoritasHolder holder, int position) {
                lotericaModel.excluirLoterica(
                        lotericaModel.getIdLotericaByPosition(position),
                        position,
                        new OnSilceListener<RetornoPadraoResponse>() {
                            @Override
                            public void success(RetornoPadraoResponse payload) {
                                if (position >= 0 && position < lotericasList.size()) {
                                    lotericasList.remove(position);
                                }

                                if (lotericasAdapter != null) {
                                    lotericasAdapter.removeAt(position);
                                }

                                setLotericasFavoritas(lotericasList);
                            }

                            @Override
                            public void error(VolleyError error) {
                            }
                        }
                );
            }
        };
    }



    public void configuraListView(List<LotericaFavoritaDTO> list) {
        if (lotericasAdapter == null) {
            lotericasAdapter = new ListaLotericasAdapter(list, onItemLotericaFavoritaListener(), BolaoActivity.this);
            lotericasListView.setAdapter(lotericasAdapter);
        } else {
            lotericasAdapter.replaceAll(list);
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
				diminuiQuantidade(bolaoList, position, holder);
			}

			@Override
			public void aumenta(BolaoHolder holder, int position) {
				aumentaQuantidade(bolaoList, position, holder);
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
				Bundle args = new Bundle();
				args.putSerializable(ARG_FILTRO_MARKETPLACE, filtro);
				Intent intent = IntentUtil.getIntentOrigemDestino(BolaoActivity.this, FiltraMarketplaceActivity.class);
				intent.putExtras(args);
				IntentUtil.startActivityForResult(launcherFiltro, intent);
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
				frameEstadoVazio.setVisibility(GONE);
				scrollView.setVisibility(VISIBLE);
				if(!Objects.equals(ModalidadeEnum.fromStringToIdModalidade(filtro.getModalidade()), idModalidadeInicial)
						|| !Objects.equals(TipoConcursoEnum.fromStringToIdTipoConcurso(filtro.getTipoConcurso()), tipoConcursoInicial)){
					model.setIdModalidade(idModalidadeInicial);
					model.setIdTipoConcurso(tipoConcursoInicial);
					buscaBoloes();
				}
				break;
			case ERRO:
				if(!Objects.equals(ModalidadeEnum.fromStringToIdModalidade(filtro.getModalidade()), idModalidadeInicial)
						|| !Objects.equals(TipoConcursoEnum.fromStringToIdTipoConcurso(filtro.getTipoConcurso()), tipoConcursoInicial)){
					model.setIdModalidade(idModalidadeInicial);
					model.setIdTipoConcurso(tipoConcursoInicial);
				}
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