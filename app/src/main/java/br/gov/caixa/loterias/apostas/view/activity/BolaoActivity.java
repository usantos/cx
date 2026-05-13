package br.gov.caixa.loterias.apostas.view.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
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
import java.util.Locale;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.model.model.LotericaFavoritaModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.FavoritarLotericaHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrosselBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaLotericasAdapter;
import br.gov.caixa.loterias.apostas.view.custom.AccessibleSpringDotsIndicator;
import br.gov.caixa.loterias.apostas.view.fragment.BarraTituloFiltroLotericaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.FiltroVazioFragment;
import br.gov.caixa.loterias.apostas.view.fragment.LotericaEmptyFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SomadorCarrinhoFragment;
import br.gov.caixa.loterias.apostas.view.holder.BolaoCarrosselHolder;
import br.gov.caixa.loterias.apostas.view.holder.BolaoHolder;
import br.gov.caixa.loterias.apostas.view.holder.LotericasFavoritasHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnActivityForResult;
import br.gov.caixa.loterias.apostas.view.listener.OnCompraBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemCarrosselBolaoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnItemLotericaFavoritaListener;
import br.gov.caixa.loterias.apostas.view.listener.OnVaiParaCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.OnViewAumentaDiminuiQuantidade;

public class BolaoActivity extends LoteriasAppMarketPlaceActivity implements OnVaiParaCarrinhoListener, OnCompraBolaoListener {
    public static final String ARG_IDMODALIDADE = "ARG_IDMODALIDADE";

    public static final String ARG_TIPO_CONCURSO = "ARG_TIPO_CONCURSO";

    public static final String ARG_FILTRO_MARKETPLACE = "ARG_FILTRO_MARKETPLACE";
    public static final String ARG_DTO_LOTERICA = "ARG_DTO_LOTERICA";
    public static final String ARG_DESATIVAR_FILTRO = "DESATIVAR_FILTRO";

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
    private ActivityResultLauncher<Intent> launcherBolao;

    private TextView txtFimLista;
    private int idMunicipio = -1;
    private int idUf = -1;
    private boolean acumulouBoloesParaApresentar, isFiltrando = false;

    private NestedScrollView scrollView;
    private FrameLayout frameEstadoVazio;
    private FiltroVazioFragment filtroVazioFragment;
    private LotericaEmptyFragment lotericaVaziaFragment;
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
    private BarraTituloFiltroLotericaFragment fragBarraTituloFiltroLoterica;
    AccessibleSpringDotsIndicator dotsIndicator;
    String numeroConcurso;
    String nomeModalidade;

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
            if (idModalidade != null) {
                ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(idModalidade);
                nomeModalidade = modalidade != null
                        ? ModalidadeEnum.fromString(modalidade)
                        : "";

                loadParametroJogo(ModalidadeEnum.fromInteger(idModalidade));
            }
        }
        launcherBolao = IntentUtil.registerLauncherActivityForResult(this, OnReturnActivity());
        launcherFiltro = IntentUtil.registerLauncherActivityForResult(this, OnActivityForResult());
        launcherAddCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        launcherCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        launcherDetalhes = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        setTitle("");
        TextView toolBarTxt = findViewById(R.id.toolbar_title);
        configFiltrar(launcherFiltro, filtro);
        toolBarTxt.setVisibility(VISIBLE);
        listView = findViewById(R.id.lista_bolao);
        lotericasListView = findViewById(R.id.lista_lotericas);
        carrosselView = findViewById(R.id.bolao_carousel);
        scrollView = findViewById(R.id.id_scrollview);
        frameEstadoVazio = findViewById(R.id.id_fraf_estado_vazio);
        txtFimLista = findViewById(R.id.id_fim_lista);
        loading = findViewById(R.id.progress_loading);


        btnBoloes = findViewById(R.id.btn_boloes);
        btnLotericasFavoritas = findViewById(R.id.btn_lotericas_favoritas);
        layoutBoloes = findViewById(R.id.contentBoloes);
        layoutLotericasFavoritas = findViewById(R.id.contentLotericasFavoritas);

        if (idModalidadeInicial != null)
            filtro.setModalidade(ModalidadeEnum.fromInteger(idModalidadeInicial));
        if (tipoConcursoInicial != null)
            filtro.setTipoConcurso(TipoConcursoEnum.fromInteger(tipoConcursoInicial));
        toolBarTxt.setText(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.label_bolao_bar_marketplace)));
        ViewCompat.setAccessibilityHeading(toolBarTxt,true);

        scrollView.setOnScrollChangeListener((NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            View view = v.getChildAt(v.getChildCount() - 1);
            int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));
            // if diff is zero, then the bottom has been reached
            if (diff == 0) {
                if (!model.isAcabouRegistros()) {
                    buscarMaisBoloes();
                }
            }
        });

    dotsIndicator = findViewById(R.id.dotsIndicator);

        carrosselView.addOnItemChangedListener((viewHolder, position) -> {
            AlertDialogUtils.dismiss();
            dotsIndicator.setSelectedIndex(position);
        });

        dotsIndicator.setOnDotClickListener(index -> {
            carrosselView.smoothScrollToPosition(index);
        });

        model = new BolaoModel(this);
        btnBoloes.setOnClickListener(v -> seletorBoloes());
        btnLotericasFavoritas.setOnClickListener(v -> seletorLotericasFavoritas());

        lotericaModel = new LotericaFavoritaModel(this);

        fragmentSomadorCarrinho();
        fragmentRegistraRetorno();

        AlertDialogUtils.show(this);
        model.buscaDadosUsuario(onBuscaDadosUsuarios());

    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsHelper.getInstance().logViewScreenAposta(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.BOLAO,
                AnalyticsHelper.Tela.BOLAO,
                nomeModalidade,
                numeroConcurso
        );
    }


    private void loadParametroJogo(ModalidadeEnum modalidade) {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametro = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidade);
        if (parametro != null) {
            parametroJogo = parametro.getParametroJogo();
        }
    }

    private void preencheBarraTitulo(ModalidadeEnum modalidade, TipoConcursoEnum tipoConcurso) {
        if (parametroJogo == null || parametroJogo.getConcurso() == null || parametroJogo.getConcurso().getNumero() == null) {
            return;
        }
        numeroConcurso = parametroJogo.getConcurso().getNumero().toString();
        if (isFiltradoLoterica()) {
            fragBarraTituloFiltroLoterica = FragmentUtils.startFragmentBarraTituloFiltroLoterica(getSupportFragmentManager(), R.id.barra_titulo_mkp, filtro, cotasDisponiveis.get(0).isLotericaFavorita());
            selectFiltroLoterica();
        } else {
            EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
            boolean isEspecial = tipoConcurso == TipoConcursoEnum.ESPECIAL;
            FragmentUtils.startFragmentBarraTituloBolao(
                    getSupportFragmentManager(),
                    R.id.barra_titulo_mkp,
                    modalidade,
                    numeroConcurso,
                    getDataSorteio(),
                    parametroJogo.getConcurso().getEstimativa(),
                    isEspecial,
                    estilo.getCorClara(),
                    estilo.getCorEscura());
            unSelectFiltroLoterica();
        }
    }

    private boolean isFiltradoLoterica() {
        return (filtro != null && filtro.getLotericaDTO() != null && filtro.getLotericaDTO().getCodigo() != null);
    }

    private String getDataSorteio() {
        String dataSorteio = "";
        SimpleDateFormat formatterDateFinal = new SimpleDateFormat(getResources().getString(R.string.dd_mm));
        DateFormat formatterDate = new SimpleDateFormat(getResources().getString(R.string.dd_mm_yyyy_hh_mm_ss));

        try {
            if (parametroJogo != null && parametroJogo.getConcurso() != null && parametroJogo.getConcurso().getDataHoraSorteio() != null) {
                Date dataFormatada = formatterDate.parse(parametroJogo.getConcurso().getDataHoraSorteio());
                assert dataFormatada != null;
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
        if (lotericasList == null || lotericasList.isEmpty()) {
            initLotericasVazia();
        }
        hideFiltrar(true);
    }

    private void seletorBoloes() {
        btnBoloes.setChecked(Boolean.TRUE);
        layoutBoloes.setVisibility(VISIBLE);
        unSelectLotericasFavoritas();
        if (cotasDisponiveis == null || cotasDisponiveis.isEmpty()) {
            apresentanEstadoVazio();
        }
        hideFiltrar(false);
    }


    private void unSelectBoloes() {
        btnBoloes.setChecked(Boolean.FALSE);
        layoutBoloes.setVisibility(GONE);

        unSelectEstadoVazio();
    }

    private void unSelectLotericasFavoritas() {
        btnLotericasFavoritas.setChecked(Boolean.FALSE);
        layoutLotericasFavoritas.setVisibility(GONE);

        unSelectEstadoVazio();
    }


    private void unSelectEstadoVazio() {
        frameEstadoVazio.setVisibility(GONE);
        scrollView.setVisibility(VISIBLE);

        if (filtroVazioFragment != null) {
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(filtroVazioFragment)
                        .commit();
            } catch (IllegalStateException e) {
                // Em caso de state loss, você pode trocar por commitAllowingStateLoss()
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(filtroVazioFragment)
                        .commitAllowingStateLoss();
            } finally {
                filtroVazioFragment = null;
            }
        }

        if (lotericaVaziaFragment != null) {
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(lotericaVaziaFragment)
                        .commit();
            } catch (IllegalStateException e) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(lotericaVaziaFragment)
                        .commitAllowingStateLoss();
            } finally {
                lotericaVaziaFragment = null;
            }
        }
    }

    private void selectFiltroLoterica() {
        btnBoloes.setText(R.string.label_boloes_loterica);
    }

    private void unSelectFiltroLoterica() {
        btnBoloes.setText(R.string.label_boloes);
    }


    private int getPosicaoReal(int posicao) {
        if (itensCardslVisiveis == 0) return 0;
        return posicao % itensCardslVisiveis;
    }


    private void buscaBoloes() {
        AlertDialogUtils.show(BolaoActivity.this);
        model.buscaBoloes(onBoloesListener());
    }

    private void buscaLotericas() {
        AlertDialogUtils.show(BolaoActivity.this);

        lotericaModel.buscaLotericas(new OnSilceListener<List<LotericaFavoritaDTO>>() {
            @Override
            public void success(List<LotericaFavoritaDTO> payload) {
                AlertDialogUtils.dismiss();
                loading.setVisibility(GONE);
                if (payload == null) {
                    payload = simulaPayloadLoterica();
                }
                if (lotericasAdapter == null) {
                    setLotericasFavoritas(payload);
                    configuraListView();
                }

                if (lotericasList == null || lotericasList.isEmpty()) {
                    initLotericasVazia();
                }
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                loading.setVisibility(GONE);
                initLotericasVazia();
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
                if (!isFiltrando) {
                    buscaBoloes();
                    buscaLotericas();
                } else {
                    buscaBoloesFiltro();
                }
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                apresentanEstadoVazio();
            }
        };
    }


    private void initFragmentFiltroVazio() {
        if (!btnBoloes.isChecked()) {
            unSelectEstadoVazio();
            return;
        }

        if (filtroVazioFragment == null) {
            filtroVazioFragment = FragmentUtils.startFiltroVazio(getSupportFragmentManager(), R.id.id_fraf_estado_vazio);
        }
        frameEstadoVazio.setVisibility(VISIBLE);
        scrollView.setVisibility(GONE);
        layoutBoloes.setVisibility(GONE);

        if (isFiltradoLoterica()) {
            boolean fav = false;

            if (lotericasList != null) {
                for (LotericaFavoritaDTO loterica : lotericasList) {
                    if (filtro.getLotericaDTO().getId().equals(loterica.getCodigo())) {
                        fav = true;
                        break;
                    }
                }
            }

            fragBarraTituloFiltroLoterica = FragmentUtils.startFragmentBarraTituloFiltroLoterica(
                    getSupportFragmentManager(),
                    R.id.barra_titulo_mkp,
                    filtro,
                    fav
            );

            layoutBoloes.setVisibility(View.VISIBLE);
        }

    }

    private void initLotericasVazia() {
        if (btnLotericasFavoritas.isChecked()) {
            if (lotericaVaziaFragment == null) {
                lotericaVaziaFragment = FragmentUtils.startLotericaVazia(getSupportFragmentManager(), R.id.id_fraf_estado_vazio);
                frameEstadoVazio.setVisibility(VISIBLE);
                scrollView.setVisibility(GONE);
                layoutBoloes.setVisibility(GONE);
            } else {
                frameEstadoVazio.setVisibility(VISIBLE);
                scrollView.setVisibility(GONE);
                layoutBoloes.setVisibility(GONE);
            }
        } else {
            unSelectEstadoVazio();
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

                if (naoTemOndeBuscar()) {
                    loading.setVisibility(GONE);
                    if (!payload.getCotas().isEmpty() || acumulouBoloesParaApresentar) {
                        preencheListas(cotasDisponiveis);
                    }
                    if (!cotasDisponiveis.isEmpty()) {
                        txtFimLista.setVisibility(VISIBLE);
                    }

                    if (cotasDisponiveis == null || cotasDisponiveis.isEmpty()) {
                        apresentanEstadoVazio();
                    }
                    AlertDialogUtils.dismiss();
                } else if (payload.getCotas().isEmpty() || payload.getCotas().size() < PaginacaoFiltro.QTD_POR_PAGINA) {
                    acumulouBoloesParaApresentar = true;
                    buscarMaisBoloes();
                } else {
                    preencheListas(cotasDisponiveis);
                    AlertDialogUtils.dismiss();
                    loading.setVisibility(GONE);
                }

                if (cotasDisponiveis != null && !cotasDisponiveis.isEmpty()) {
                    if (idModalidade != null && tipoConcurso != null) {
                        preencheBarraTitulo(ModalidadeEnum.fromInteger(idModalidade), TipoConcursoEnum.fromInteger(tipoConcurso));
                    }
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
        payload.setCotas(new ArrayList<>());
        return payload;
    }

    private List<LotericaFavoritaDTO> simulaPayloadLoterica() {
        return new List<LotericaFavoritaDTO>() {
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
    }

    private OnSilceListener<ListaBolaoDTO> onBoloesFiltradosListener() {
        return new OnSilceListener<ListaBolaoDTO>() {
            @Override
            public void success(ListaBolaoDTO payload) {


                if (payload == null) {
                    payload = simulaPayloadZerado();
                }

                model.atualizaPaginacao(payload);

                if (payload.getCotas().isEmpty()) {
                    if (naoTemOndeBuscar()) {
                        if (cotasDisponiveis == null || cotasDisponiveis.isEmpty()) {
                            apresentanEstadoVazio();
                        } else {
                            preencheListas(cotasDisponiveis);
                            if (!cotasDisponiveis.isEmpty()) {
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

                    if (naoTemOndeBuscar()) {
                        preencheListas(cotasDisponiveis);
                        if (!cotasDisponiveis.isEmpty()) {
                            if (cotasDisponiveis.size() > 4) {
                                txtFimLista.setVisibility(VISIBLE);
                            }
                        }
                        loading.setVisibility(GONE);
                        AlertDialogUtils.dismiss();
                    } else if (payload.getCotas().size() < PaginacaoFiltro.QTD_POR_PAGINA) {
                        acumulouBoloesParaApresentar = true;
                        buscarMaisBoloes();
                    } else {
                        preencheListas(cotasDisponiveis);
                        loading.setVisibility(GONE);
                        AlertDialogUtils.dismiss();
                    }
                }

                if (cotasDisponiveis != null && !cotasDisponiveis.isEmpty()) {
                    if (idModalidade != null && tipoConcurso != null) {
                        preencheBarraTitulo(ModalidadeEnum.fromInteger(idModalidade), TipoConcursoEnum.fromInteger(tipoConcurso));
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
        carrosselAdapter = new CarrosselBolaoAdapter(BolaoActivity.this, carrosselList, onCarrosselItemListener(), filtro);
        carrosselView.setAdapter(carrosselAdapter);
        carrosselView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        int total = carrosselAdapter.getItemCount();
        dotsIndicator.setDotCount(total);
        dotsIndicator.setSelectedIndex(0);


        if (listAdapter == null) {
            bolaoList = model.getBolaoList(cotas);
            listAdapter = new ListaBolaoAdapter(bolaoList, onItemBolaoListener(), BolaoActivity.this, filtro);
            listView.setAdapter(listAdapter);
        } else {
            int inicioRange = bolaoList.size();
            bolaoList = model.getBolaoList(cotas);
            if (inicioRange < bolaoList.size()) {
                listAdapter.atualizaLista(inicioRange, bolaoList.subList(inicioRange, bolaoList.size()), filtro);
            }
        }


        acumulouBoloesParaApresentar = false;
    }

    private void setCotasDisponiveis(ListaBolaoDTO payload) {
        if (cotasDisponiveis == null) {
            cotasDisponiveis = payload.getCotas();
        } else {
            for (CotasBolaoDTO cota : payload.getCotas()) {
                cotasDisponiveis.add(cota);
            }
        }
    }

    private void setLotericasFavoritas(List<LotericaFavoritaDTO> payload) {
        lotericasList = payload;
        lotericaModel.setListLotericas(lotericasList);
    }

    private void buscarMaisBoloes() {
        loading.setVisibility(VISIBLE);
        if (isFiltrando) {
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
            seletorBoloes();
            frameEstadoVazio.setVisibility(GONE);
            scrollView.setVisibility(VISIBLE);
            layoutBoloes.setVisibility(VISIBLE);

            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    filtro = (FiltroAplicadoMarketplace) data.getSerializableExtra(FiltraMarketplaceActivity.FILTRO_MARKETPLACE_RETORNO);
                    if (filtro != null) {
                        isFiltrando = true;
                        configFiltrar(launcherFiltro, filtro);
                        if (filtro.getModalidade() != null) {
                            idModalidade = ModalidadeEnum.fromStringToIdModalidade(filtro.getModalidade());
                            tipoConcurso = TipoConcursoEnum.fromStringToIdTipoConcurso(filtro.getTipoConcurso());
                        }
                    }
                }
                AlertDialogUtils.show(BolaoActivity.this);
                model.zeraPaginacao();
                cotasDisponiveis = null;

                buscaBoloesFiltro();
                listAdapter = null;
                txtFimLista.setVisibility(GONE);
                scrollView.scrollTo(0, 0);
            }
        };
    }

    private OnActivityForResult OnReturnActivity() {
        return result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Long id = data.getLongExtra("ID_LOTERICA", -1L);
                    boolean novoEstado = data.getBooleanExtra("NOVO_ESTADO", false);
                    if(lotericasList != null) {
                        for (Iterator<LotericaFavoritaDTO> it = lotericasList.iterator(); it.hasNext(); ) {
                            if (id.equals(it.next().getCodigo())) {
                                it.remove();
                            }
                        }
                    }
                    assert lotericasList != null;
                    lotericaModel.setListLotericas(new ArrayList<>(lotericasList));
                    lotericasAdapter.replaceAll(new ArrayList<>(lotericasList));
                    atualizaFavoritoTodasListas(id, novoEstado);
                    if (novoEstado) {
                        loading.setVisibility(VISIBLE);
                        AlertDialogUtils.show(BolaoActivity.this);

                        lotericaModel.buscaLotericas(new OnSilceListener<List<LotericaFavoritaDTO>>() {
                            @Override
                            public void success(List<LotericaFavoritaDTO> payload) {

                                if (payload == null) {
                                    payload = simulaPayloadLoterica();
                                }
                                if (payload.isEmpty()) {
                                    initLotericasVazia();
                                }
                                lotericasList = payload;
                                lotericaModel.setListLotericas(new ArrayList<>(lotericasList));
                                lotericasAdapter.replaceAll(new ArrayList<>(lotericasList));
                                seletorLotericasFavoritas();
                                AlertDialogUtils.dismiss();
                                loading.setVisibility(GONE);
                            }

                            @Override
                            public void error(VolleyError error) {
                                AlertDialogUtils.dismiss();
                                loading.setVisibility(GONE);
                                seletorLotericasFavoritas();
                                initLotericasVazia();
                            }
                        });
                    }
                }
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
                if (isFiltrando) {
                    buscaBoloesFiltro();
                } else {
                    buscaBoloes();
                }
            }
        };
    }

    private void buscaBoloesFiltro() {
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
                diminuiQuantidade(carrosselList, position, (BolaoCarrosselHolder) holder);
            }

            @Override
            public void aumenta(BolaoCarrosselHolder holder, int position) {
                aumentaQuantidade(carrosselList, position, holder);
            }

            @Override
            public void onFavoritarClick(int position) {
                if (carrosselList != null && position >= 0 && position < carrosselList.size()) {
                    favoritar(carrosselList.get(position));
                }
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
        if (somadorCarrinhoFragment == null) {
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, true);
        } else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }
    }

    private OnItemLotericaFavoritaListener onItemLotericaFavoritaListener() {
        return new OnItemLotericaFavoritaListener() {

            public void verBoloes(LotericasFavoritasHolder holder, int position) {
                LotericaDTO lotericaDTO = new LotericaDTO(lotericasList.get(position));
                Intent intent = new Intent(BolaoActivity.this, VerBolaoActivity.class);
                intent.putExtra(BolaoActivity.ARG_DTO_LOTERICA, lotericaDTO);
                launcherBolao.launch(intent);
            }


            @Override
            public void excluir(LotericasFavoritasHolder holder, int position) {
                Long id = lotericaModel.getIdLotericaByPosition(position);
                model.excluirLotericaFavorita(
                        id,
                        new OnSilceListener<RetornoPadraoResponse>() {
                            @Override
                            public void success(RetornoPadraoResponse payload) {

                                if (position >= 0 && position < lotericasList.size()) {
                                    lotericasList.remove(position);
                                    lotericaModel.setListLotericas(new ArrayList<>(lotericasList));
                                    lotericasAdapter.replaceAll(new ArrayList<>(lotericasList));
                                }

                                atualizaFavoritoTodasListas(id, false);
                                if (lotericasList.isEmpty()) {
                                    initLotericasVazia();
                                }
                            }

                            @Override
                            public void error(VolleyError error) {
                            }
                        }
                );
            }
        };
    }


    public void configuraListView() {
        if (lotericasAdapter == null) {
            lotericasAdapter = new ListaLotericasAdapter(lotericasList, onItemLotericaFavoritaListener(), BolaoActivity.this);
            lotericasListView.setAdapter(lotericasAdapter);
        } else {
            lotericasAdapter.replaceAll(lotericasList);
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

            @Override
            public void onFavoritarClick(int position) {
                if (bolaoList != null && position >= 0 && position < bolaoList.size()) {
                    favoritar(bolaoList.get(position));
                }
            }
        };
    }

    private void favoritar(CotasBolaoDTO bolao) {

        if (bolao == null) return;

        Long lotericaId = bolao.getLoterica();
        boolean estadoAtual = bolao.isLotericaFavorita();

        FavoritarLotericaHelper.confirmar(
                this,
                lotericaId,
                bolao.getNomeFantasia(),
                estadoAtual,
                new FavoritarLotericaHelper.Acoes() {
                    @Override
                    public void incluir(Long id, OnSilceListener<RetornoPadraoResponse> listener) {
                        if (model != null) model.incluirLotericaFavorita(id, listener);
                    }

                    @Override
                    public void excluir(Long id, OnSilceListener<RetornoPadraoResponse> listener) {
                        if (model != null) {
                            model.excluirLotericaFavorita(id, listener);
                            for (Iterator<LotericaFavoritaDTO> it = lotericasList.iterator(); it.hasNext(); ) {
                                if (id.equals(it.next().getCodigo())) {
                                    it.remove();
                                }
                            }
                            lotericaModel.setListLotericas(new ArrayList<>(lotericasList));
                            lotericasAdapter.replaceAll(new ArrayList<>(lotericasList));
                        }
                    }
                },
                novoEstado -> {
                    // Atualiza todas as listas após sucesso
                    if (lotericaId != null) {
                        atualizaFavoritoTodasListas(lotericaId, novoEstado);
                        if (novoEstado) {
                            loading.setVisibility(VISIBLE);
                            AlertDialogUtils.show(BolaoActivity.this);

                            lotericaModel.buscaLotericas(new OnSilceListener<List<LotericaFavoritaDTO>>() {
                                @Override
                                public void success(List<LotericaFavoritaDTO> payload) {

                                    if (payload == null) {
                                        payload = simulaPayloadLoterica();
                                    }
                                    if (payload.isEmpty()) {
                                        initLotericasVazia();
                                    }
                                    lotericasList = payload;
                                    lotericaModel.setListLotericas(new ArrayList<>(lotericasList));
                                    lotericasAdapter.replaceAll(new ArrayList<>(lotericasList));
                                    seletorLotericasFavoritas();
                                    AlertDialogUtils.dismiss();
                                    loading.setVisibility(GONE);
                                }

                                @Override
                                public void error(VolleyError error) {
                                    AlertDialogUtils.dismiss();
                                    loading.setVisibility(GONE);
                                    seletorLotericasFavoritas();
                                    initLotericasVazia();
                                }
                            });
                        }
                    }
                }
        );
    }

    private void atualizaFavoritoTodasListas(Long lotericaId, boolean isFavorita) {
        //Atualiza na lista do carrossel
        if (carrosselList != null) {
            for (CotasBolaoDTO bolao : carrosselList) {
                if (bolao.getLoterica().equals(lotericaId)) {
                    bolao.setLotericaFavorita(isFavorita);
                }
            }
        }
        if (carrosselAdapter != null) {
            carrosselAdapter.notifyDataSetChanged();
        }

        //Atualiza na lista principal
        if (bolaoList != null) {
            for (CotasBolaoDTO bolao : bolaoList) {
                if (bolao.getLoterica().equals(lotericaId)) {
                    bolao.setLotericaFavorita(isFavorita);
                }
            }
        }
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (fragBarraTituloFiltroLoterica != null) {
            fragBarraTituloFiltroLoterica.setFavoritar(isFavorita);
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

    private LotericaFavoritaDTO findLoterica(Long codigo) {
        for (LotericaFavoritaDTO loterica : lotericasList) {
            if (codigo.equals(loterica.getCodigo())) {
                return loterica;
            }
        }
        return null;
    }

    private void adicionarNoCarrinho(CotasBolaoDTO bolao) {

        LotericaFavoritaDTO lotteryShop = findLoterica(bolao.getLoterica());

        if (lotteryShop != null) {
            String cidadeUfLoterica =
                    StringUtils.capitalizerNovo(bolao.getMunicipio().getNome())
                            + " - "
                            + bolao.getMunicipio().getUf().getSigla();

            AnalyticsHelper.getInstance().logInteractionAddBolao(
                    AnalyticsHelper.Tela.BOLAO,
                    ModalidadeEnum.fromString(ModalidadeEnum.fromInteger(idModalidade)),
                    AnalyticsHelper.EventCategoryParams.CTA,
                    AnalyticsHelper.EventActionParams.CLICAR,
                    AnalyticsHelper.EventLabelParams.ADD_BOLAO,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.BOLAO,
                    bolao.getLoterica(),
                    lotteryShop.getNomeFantasia(),
                    cidadeUfLoterica,
                    String.valueOf(bolao.getQtdApostas()),
                    String.valueOf(bolao.getQtdNumeros()),
                    ViewUtils.getMoedaFormat(bolao.getVrCotaSemTarifa()),
                    ViewUtils.getMoedaFormat(bolao.getValorTarifaServico()),
                    ViewUtils.getMoedaFormat(bolao.getVrCotaComTarifa()),
                    String.valueOf(bolao.getQtdCotaTotal()),
                    String.valueOf(bolao.getQtdCotasBolao())
            );
        }

        model.addBolaoCarrinho(bolao);
    }


    @Override
    public void vaiParaCarrinho() {
        IntentUtil.startActivityForResult(launcherCarrinho, IntentUtil.getIntentOrigemDestino(BolaoActivity.this, CarrinhoActivity.class));
    }

    @Override
    public void compraSucesso(Intent intent) {
        IntentUtil.startActivityForResult(launcherAddCarrinho, intent);
    }


    private void fragmentRegistraRetorno() {
        getSupportFragmentManager().setFragmentResultListener(
                "favoritar_result",
                this,
                (requestKey, result) -> {
                    LotericaDTO lotericaDTO = (LotericaDTO) result.getSerializable("lotericaDTO");

                    if (lotericaDTO == null) return;

                    CotasBolaoDTO bolao = new CotasBolaoDTO();
                    bolao.setLoterica(lotericaDTO.getId());
                    bolao.setNomeFantasia(lotericaDTO.getNomeFantasia().trim());

                    if (cotasDisponiveis != null && !cotasDisponiveis.isEmpty()) {
                        bolao.setLotericaFavorita(cotasDisponiveis.get(0).isLotericaFavorita());
                    } else {
                        bolao.setLotericaFavorita(false);
                    }

                    if (lotericasList != null) {
                        for (LotericaFavoritaDTO favorita : lotericasList) {
                            if (lotericaDTO.getId().equals(favorita.getCodigo())) {
                                bolao.setLotericaFavorita(true);
                                break;
                            }
                        }
                    }

                    favoritar(bolao);
                }
        );
    }
}