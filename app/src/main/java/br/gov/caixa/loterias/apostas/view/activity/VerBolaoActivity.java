package br.gov.caixa.loterias.apostas.view.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasAppMarketPlaceActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.controllers.FiltraMarketplaceActivity;
import br.gov.caixa.loterias.apostas.model.bean.FiltroAplicadoMarketplace;
import br.gov.caixa.loterias.apostas.model.bean.PaginacaoFiltro;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.FavoritarLotericaHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.BolaoFooterAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.BolaoHeaderAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.BarraTituloFiltroLotericaFragment;
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

public class VerBolaoActivity extends LoteriasAppMarketPlaceActivity implements OnVaiParaCarrinhoListener, OnCompraBolaoListener {
    public static final String ARG_IDMODALIDADE = "ARG_IDMODALIDADE";

    public static final String ARG_TIPO_CONCURSO = "ARG_TIPO_CONCURSO";

    public static final String ARG_DTO_LOTERICA = "ARG_DTO_LOTERICA";

    private ListaBolaoAdapter listAdapter;

    private BolaoModel model;
    private List<CotasBolaoDTO> bolaoList;
    private List<CotasBolaoDTO> carrosselList;
    private SomadorCarrinhoFragment somadorCarrinhoFragment;
    private ActivityResultLauncher<Intent> launcherFiltro;
    private ActivityResultLauncher<Intent> launcherAddCarrinho;
    private ActivityResultLauncher<Intent> launcherCarrinho;
    private ActivityResultLauncher<Intent> launcherDetalhes;

    private int idMunicipio = -1;
    private int idUf = -1;
    private boolean isFiltrando = false;

    private FrameLayout frameEstadoVazio;
    private FiltroVazioFragment filtroVazioFragment;
    private FiltroAplicadoMarketplace filtro = new FiltroAplicadoMarketplace();
    private List<CotasBolaoDTO> cotasDisponiveis;

    private Integer idModalidade;
    private Integer tipoConcurso;
    private LotericaDTO dtoLoterica;

    private int itensCardslVisiveis = 0;
    private RecyclerView rvConteudoBoloes;
    private ConcatAdapter concatBolaoAdapter;
    private BolaoHeaderAdapter bolaoHeaderAdapter;
    private BolaoFooterAdapter bolaoFooterAdapter;
    private BarraTituloFiltroLotericaFragment fragBarraTituloFiltroLoterica;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_bolao);
        configToolbar(R.id.toolbar);

        if (getIntent() != null && getIntent().getExtras() != null) {
            idModalidade = getIntent().getExtras().getInt(ARG_IDMODALIDADE);
            tipoConcurso = getIntent().getExtras().getInt(ARG_TIPO_CONCURSO);
            dtoLoterica = (LotericaDTO) getIntent().getSerializableExtra(ARG_DTO_LOTERICA);
            filtro.setLotericaDTO(dtoLoterica);
        } else {
            filtro.setTodasModalidades(true);
        }

        launcherFiltro = IntentUtil.registerLauncherActivityForResult(this, OnActivityForResult());
        launcherAddCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        launcherCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        launcherDetalhes = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        setTitle("");
        TextView toolBarTxt = findViewById(R.id.toolbar_title);
        configFiltrar(launcherFiltro, filtro, true);
        toolBarTxt.setVisibility(VISIBLE);
        frameEstadoVazio = findViewById(R.id.id_fraf_estado_vazio);
        fragBarraTituloFiltroLoterica = FragmentUtils.startFragmentBarraTituloFiltroLoterica(getSupportFragmentManager(), R.id.barra_titulo_mkp, filtro, true);
        toolBarTxt.setText(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.loterica_favorita_bold)));
        ViewCompat.setAccessibilityHeading(toolBarTxt,true);

        model = new BolaoModel(this);

        bolaoList = new ArrayList<>();
        carrosselList = new ArrayList<>();

        rvConteudoBoloes = findViewById(R.id.rv_conteudo_boloes);
        frameEstadoVazio = findViewById(R.id.id_fraf_estado_vazio);

        model = new BolaoModel(this);

        fragmentSomadorCarrinho();
        fragmentRegistraRetorno();

        AlertDialogUtils.show(this);
        model.buscaDadosUsuario(onBuscaDadosUsuarios());
        firstTimeFiltrar();

        rvConteudoBoloes.setLayoutManager(new LinearLayoutManager(this));
        rvConteudoBoloes.setHasFixedSize(false);
        rvConteudoBoloes.setItemAnimator(null);
        rvConteudoBoloes.setNestedScrollingEnabled(false);

        bolaoHeaderAdapter = new BolaoHeaderAdapter(
                this,
                onCarrosselItemListener(),
                filtro,
                true // se quiser um overload pra VerBolao
        );

        bolaoFooterAdapter = new BolaoFooterAdapter();

        listAdapter = new ListaBolaoAdapter(
                bolaoList,
                onItemBolaoListener(),
                this,
                filtro
        );

        concatBolaoAdapter = new ConcatAdapter(
                bolaoHeaderAdapter,
                listAdapter,
                bolaoFooterAdapter
        );

        rvConteudoBoloes.setAdapter(concatBolaoAdapter);

        rvConteudoBoloes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && !recyclerView.canScrollVertically(1)) {
                    if (!model.isAcabouRegistros()) {
                        buscarMaisBoloes();
                    }
                }
            }
        });
    }

    private void seletorBoloes() {
        if (cotasDisponiveis == null || cotasDisponiveis.isEmpty()) {
            apresentaEstadoVazio();
        }
    }

    private int getPosicaoReal(int posicao) {
        if (itensCardslVisiveis == 0) return 0;
        return posicao % itensCardslVisiveis;
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
                buscaBoloesFiltro();
                bolaoFooterAdapter.mostrarLoading(false);
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                apresentaEstadoVazio();
                bolaoFooterAdapter.mostrarLoading(false);
            }
        };
    }


    private void initFragmentFiltroVazio() {
        if (filtroVazioFragment == null) {
            filtroVazioFragment = FragmentUtils.startFiltroVazio(
                    getSupportFragmentManager(),
                    R.id.id_fraf_estado_vazio
            );
        }

        frameEstadoVazio.setVisibility(VISIBLE);
        rvConteudoBoloes.setVisibility(GONE);
    }

    private void apresentaEstadoSemBolao() {
        initFragmentFiltroVazio();
        filtroVazioFragment.estadoSemBolao();
    }

    private void apresentaEstadoVazio() {
        initFragmentFiltroVazio();
        filtroVazioFragment.estadoVazio();
    }

    private void apresentaEstadoErro() {
        initFragmentFiltroVazio();
        filtroVazioFragment.estadoErro();
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

                if (payload.getCotas().isEmpty()) {
                    if (naoTemOndeBuscar()) {
                        if (cotasDisponiveis == null || cotasDisponiveis.isEmpty()) {
                            if (isFiltrando) {
                                apresentaEstadoVazio();
                            } else {
                                apresentaEstadoSemBolao();
                            }
                        } else {
                            preencheListas(cotasDisponiveis);
                            if (!cotasDisponiveis.isEmpty()) {
                                if (cotasDisponiveis.size() > 4) {
                                    bolaoFooterAdapter.mostrarFimLista(true);                                }
                            }
                        }
                        bolaoFooterAdapter.mostrarLoading(false);
                        AlertDialogUtils.dismiss();
                    } else {
                        buscarMaisBoloes();
                    }
                } else {
                    setCotasDisponiveis(payload);

                    if (naoTemOndeBuscar()) {
                        preencheListas(cotasDisponiveis);
                        if (!cotasDisponiveis.isEmpty()) {
                            if (cotasDisponiveis.size() > 4) {
                                bolaoFooterAdapter.mostrarFimLista(true);
                            }
                        }
                        bolaoFooterAdapter.mostrarLoading(false);
                        AlertDialogUtils.dismiss();
                    } else if (payload.getCotas().size() < PaginacaoFiltro.QTD_POR_PAGINA) {
                        buscarMaisBoloes();
                    } else {
                        preencheListas(cotasDisponiveis);
                        bolaoFooterAdapter.mostrarLoading(false);
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
        carrosselList.clear();
        carrosselList.addAll(model.getCarrosselList(cotas));

        bolaoHeaderAdapter.atualizaHeader(carrosselList, filtro);

        List<CotasBolaoDTO> novaListaCompleta = model.getBolaoList(cotas);

        bolaoList.clear();
        bolaoList.addAll(novaListaCompleta);

        listAdapter.atualizarFiltro(filtro);

        bolaoFooterAdapter.mostrarLoading(false);
        bolaoFooterAdapter.mostrarFimLista(!bolaoList.isEmpty() && model.isAcabouRegistros());

    }

    private void setCotasDisponiveis(ListaBolaoDTO payload) {
        if (cotasDisponiveis == null) {
            cotasDisponiveis = payload.getCotas();
        } else {
            cotasDisponiveis.addAll(payload.getCotas());
        }
    }

    private void buscarMaisBoloes() {
        bolaoFooterAdapter.mostrarLoading(true);
        buscaBoloesFiltro();
    }


    private boolean naoTemOndeBuscar() {
        return model.isAcabouRegistros();
    }
    private void resetListaBoloes() {
        cotasDisponiveis = null;
        carrosselList.clear();
        bolaoList.clear();

        if (bolaoHeaderAdapter != null) {
            bolaoHeaderAdapter.atualizaHeader(new ArrayList<>(), filtro);
        }

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }

        if (bolaoFooterAdapter != null) {
            bolaoFooterAdapter.mostrarLoading(false);
            bolaoFooterAdapter.mostrarFimLista(false);
        }

        frameEstadoVazio.setVisibility(GONE);
        rvConteudoBoloes.setVisibility(VISIBLE);
        rvConteudoBoloes.scrollToPosition(0);

    }

    private OnActivityForResult OnActivityForResult() {
        return result -> {
            seletorBoloes();
            frameEstadoVazio.setVisibility(GONE);
            rvConteudoBoloes.setVisibility(VISIBLE);

            if (result.getResultCode() == RESULT_OK || result.getResultCode() == ResultadoNavegacaoAposta.RESULT_RESET) {
                Intent data = result.getData();
                if (data != null) {
                    filtro = (FiltroAplicadoMarketplace) data.getSerializableExtra(FiltraMarketplaceActivity.FILTRO_MARKETPLACE_RETORNO);
                    if (filtro != null) {
                        configFiltrar(launcherFiltro, filtro, true);
                        if (filtro.getModalidade() != null) {
                            idModalidade = ModalidadeEnum.fromStringToIdModalidade(filtro.getModalidade());
                            tipoConcurso = TipoConcursoEnum.fromStringToIdTipoConcurso(filtro.getTipoConcurso());
                        }
                    }
                    if(filtro == null){
                        filtro = new FiltroAplicadoMarketplace();
                        filtro.setLotericaDTO(dtoLoterica);
                    }
                    isFiltrando = data.getBooleanExtra(FiltraMarketplaceActivity.IS_FILTRANDO_RETORNO,false);
                    if(!isFiltrando){
                        firstTimeFiltrar();
                    }
                }
                AlertDialogUtils.show(VerBolaoActivity.this);
                model.zeraPaginacao();
                cotasDisponiveis = null;
                resetListaBoloes();
                buscaBoloesFiltro();
            }
        };
    }

    private void finalizarComRetorno(Long lotericaId, boolean novoEstado) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ID_LOTERICA", lotericaId);
        returnIntent.putExtra("NOVO_ESTADO", novoEstado);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private OnActivityForResult onAtualizaListasDeCotas() {
        return result -> {
            if (result.getResultCode() == RESULT_OK || result.getResultCode() == ResultadoNavegacaoAposta.RESULT_RESET) {
                AlertDialogUtils.show(VerBolaoActivity.this);
                model.zeraPaginacao();
                cotasDisponiveis = null;
                bolaoList.clear();
                carrosselList.clear();
                buscaBoloesFiltro();
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
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_somador_carrinho, true);
        } else {
            somadorCarrinhoFragment.atualizaValorTotal(CarrinhoSingleton.getInstance().getCarrinho());
        }
    }

    private OnItemBolaoListener onItemBolaoListener() {
        return new OnItemBolaoListener() {
            @Override
            public void verMais(BolaoHolder holder, int position) {
                bolaoList.get(position).setAbertoParaMostrarDetalhes(!bolaoList.get(position).isAbertoParaMostrarDetalhes());
                holder.mostrarDetalhes(bolaoList.get(position), filtro);
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
                    }

                    @Override
                    public void excluir(Long id, OnSilceListener<RetornoPadraoResponse> listener) {
                        if (model != null) {
                            model.excluirLotericaFavorita(id, listener);
                        }
                    }
                },
                novoEstado -> finalizarComRetorno(lotericaId, novoEstado)
        );
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
        IntentUtil.startActivityForResult(launcherCarrinho, IntentUtil.getIntentOrigemDestino(VerBolaoActivity.this, CarrinhoActivity.class));
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

                    CotasBolaoDTO bolao = new CotasBolaoDTO();
                    assert lotericaDTO != null;
                    bolao.setLoterica(lotericaDTO.getId());
                    bolao.setNomeFantasia(lotericaDTO.getNomeFantasia().trim());
                    bolao.setLotericaFavorita(true);

                    favoritar(bolao);
                }
        );
    }
}