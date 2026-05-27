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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

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
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CotasBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListaBolaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoPadraoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.model.BolaoModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.ModoVisualizacaoBolaoEnum;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.helper.FavoritarLotericaHelper;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.CarrosselBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.ListaBolaoAdapter;
import br.gov.caixa.loterias.apostas.view.fragment.BarraTituloFiltroLotericaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.FiltroVazioFragment;
import br.gov.caixa.loterias.apostas.view.fragment.LotericaEmptyFragment;
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

    public static final String ARG_FILTRO_MARKETPLACE = "ARG_FILTRO_MARKETPLACE";
    public static final String ARG_DTO_LOTERICA = "ARG_DTO_LOTERICA";
    public static final String ARG_DESATIVAR_FILTRO = "DESATIVAR_FILTRO";

    private DiscreteScrollView carrosselView;

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

    private Integer idModalidade;
    private Integer tipoConcurso;
    private LotericaDTO dtoLoterica;

    private int itensCardslVisiveis = 0;
    private ImageView[] itemPaginacao;
    private LinearLayout layoutPaginacaoCarrossel;

    private ParametroJogoDTO parametroJogo;
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

            if (idModalidade != null) {
                loadParametroJogo(ModalidadeEnum.fromInteger(idModalidade));
            }
        }
        launcherFiltro = IntentUtil.registerLauncherActivityForResult(this, OnActivityForResult());
        launcherAddCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        launcherCarrinho = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        launcherDetalhes = IntentUtil.registerLauncherActivityForResult(this, onAtualizaListasDeCotas());
        setTitle("");
        TextView toolBarTxt = findViewById(R.id.toolbar_title);
        configFiltrar(launcherFiltro, filtro, true);
        toolBarTxt.setVisibility(VISIBLE);
        listView = findViewById(R.id.lista_bolao);
        ConstraintLayout contentHeaderMkp = findViewById(R.id.content_header_mkp);
        carrosselView = findViewById(R.id.bolao_carousel);
        scrollView = findViewById(R.id.id_scrollview);
        frameEstadoVazio = findViewById(R.id.id_fraf_estado_vazio);
        txtFimLista = findViewById(R.id.id_fim_lista);
        loading = findViewById(R.id.progress_loading);

        layoutPaginacaoCarrossel = findViewById(R.id.layout_paginacao_carousel);

        fragBarraTituloFiltroLoterica = FragmentUtils.startFragmentBarraTituloFiltroLoterica(getSupportFragmentManager(), R.id.barra_titulo_mkp, filtro, true);
        contentHeaderMkp.setVisibility(GONE);
        toolBarTxt.setText(ViewUtils.textCaixaSTDBoldTitle(this, getString(R.string.loterica_favorita_bold)));
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

        fragmentSomadorCarrinho();
        fragmentRegistraRetorno();

        AlertDialogUtils.show(this);
        model.buscaDadosUsuario(onBuscaDadosUsuarios());
        firstTimeFiltrar();
    }


    private void loadParametroJogo(ModalidadeEnum modalidade) {
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametro = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidade);
        if (parametro != null) {
            parametroJogo = parametro.getParametroJogo();
        }
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

    private void setupPaginacaoCarrossel() {

        if (itensCardslVisiveis <= 0 || layoutPaginacaoCarrossel == null) {
            itemPaginacao = new ImageView[0];
            return;
        }
        itemPaginacao = new ImageView[itensCardslVisiveis];
        layoutPaginacaoCarrossel.removeAllViews(); //Limpa os itens caso ja existam

        for (int i = 0; i < itensCardslVisiveis; i++) {
            itemPaginacao[i] = new ImageView(this);
            itemPaginacao[i].setImageResource(R.drawable.pontovazio);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(8, 0, 8, 0);

            layoutPaginacaoCarrossel.addView(itemPaginacao[i], params);
        }

        //Ativa o primeiro ponto inicialmente
        if (itemPaginacao.length > 0) {
            itemPaginacao[0].setImageResource(R.drawable.pontopreenchido);
        }

    }

    private void atualizaIndicadorPaginacaoCarrossel(int posicaoAtual) {

        if (itemPaginacao == null || posicaoAtual < 0 || posicaoAtual >= itemPaginacao.length)
            return;

        for (int i = 0; i < itemPaginacao.length; i++) {
            if (i == posicaoAtual) {
                itemPaginacao[i].setImageResource(R.drawable.pontopreenchido);
            } else {
                itemPaginacao[i].setImageResource(R.drawable.pontovazio);
            }
        }
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
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
                apresentaEstadoVazio();
            }
        };
    }


    private void initFragmentFiltroVazio() {
        if (filtroVazioFragment == null) {
            filtroVazioFragment = FragmentUtils.startFiltroVazio(getSupportFragmentManager(), R.id.id_fraf_estado_vazio);
        }
        frameEstadoVazio.setVisibility(VISIBLE);
        scrollView.setVisibility(GONE);
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
        CarrosselBolaoAdapter carrosselAdapter;
        carrosselAdapter = new CarrosselBolaoAdapter(VerBolaoActivity.this, carrosselList, onCarrosselItemListener(), filtro, true);
        carrosselView.setAdapter(carrosselAdapter);
        carrosselView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        if (listAdapter == null) {
            bolaoList = model.getBolaoList(cotas);
            listAdapter = new ListaBolaoAdapter(bolaoList, onItemBolaoListener(), VerBolaoActivity.this, filtro);
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
            cotasDisponiveis.addAll(payload.getCotas());
        }
    }

    private void buscarMaisBoloes() {
        loading.setVisibility(VISIBLE);
        buscaBoloesFiltro();
    }

    private boolean naoTemOndeBuscar() {
        return model.isAcabouRegistros();
    }

    private OnActivityForResult OnActivityForResult() {
        return result -> {
            seletorBoloes();
            frameEstadoVazio.setVisibility(GONE);
            scrollView.setVisibility(VISIBLE);

            if (result.getResultCode() == Activity.RESULT_OK) {
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
                buscaBoloesFiltro();
                listAdapter = null;
                txtFimLista.setVisibility(GONE);
                scrollView.scrollTo(0, 0);

            }
        };
    }

    private void finalizarComRetorno(Long lotericaId, boolean novoEstado) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ID_LOTERICA", lotericaId);
        returnIntent.putExtra("NOVO_ESTADO", novoEstado);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private OnActivityForResult onAtualizaListasDeCotas() {
        return result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                AlertDialogUtils.show(VerBolaoActivity.this);
                model.zeraPaginacao();
                listAdapter = null;
                cotasDisponiveis = null;
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
            somadorCarrinhoFragment = FragmentUtils.startSomadorCarrinhoFragment(getSupportFragmentManager(), somadorCarrinhoFragment, R.id.id_fg_somador_carrinho, true);
        } else {
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