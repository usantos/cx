package br.gov.caixa.loterias.apostas.view.features.volantenovo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.CarrinhoEffect;
import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.model.SimularApostaModel;
import br.gov.caixa.loterias.apostas.model.model.SurpresinhaApostaModel;
import br.gov.caixa.loterias.apostas.novo.features.carrinho.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.utils.AccessibilityUtils;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.FragmentUtils;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.utils.ResultadoNavegacaoAposta;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.utils.TextViewUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LottieManager;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.LotecaViewModel;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation.SimulaUiState;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment.LotecaFragment;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment.SurpresinhaFragment2;
import br.gov.caixa.loterias.apostas.view.features.volantenovo.fragment.TimesFragment;
import br.gov.caixa.loterias.apostas.view.fragment.EtapaFragment;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.SurpresinhaFragmentListener;

public class LotecaActivity extends LoteriasBaseAppActivity implements CartelaFragmentListener, SurpresinhaFragmentListener {

    public static String COR_FONTE_FUNDO_BRANCO = "COR_FONTE_FUNDO_BRANCO";
    public static String COR_FONTE_FUNDO_CLARO = "COR_FONTE_FUNDO_CLARO";
    public static String COR_FONTE_FUNDO_ESCURO = "COR_FONTE_FUNDO_ESCURO";

    public final static String APOSTA_EXTRA = "aposta";
    private int valNumeroConcurso;
    private ConstraintLayout toolBarLoteca;
    private Button botaoLimparAposta;
    private Button botaoCompletar;
    private Button botaoAdicionar;
    private Button botaoNextStep;
    private TextView labelValueJogosLoteca;
    private TextView labelValueSimplesLoteca;
    private TextView labelValueDuplasLoteca;
    private TextView labelValueTriplasLoteca;

    protected IdentificaoDeUmaApostaDas8Modalidades<?> aposta;

    // Fragments
    private FragmentManager fragmentManager;
    private LotecaFragment cartelaFragment;
    private SurpresinhaFragment2 surpresinhaFragment;

    private TimesFragment timesFragment;
    private int typeGameColorLight;
    private int typeGameColorDark;
    private List<String> qtdNumerosList;
    private List<String> qtdConcursosList;
    private ModalidadeEnum tipoJogo;
    private ParametroJogoDTO parametroSimulacao;
    private Boolean isEspecial = false;

    //TODO: MEGA 30 ANOS//
    public EstiloModalidadeMKP estilo;
    private LotecaViewModel viewModel;
    private LottieManager lottieManager;
    private SimularApostaModel model;
    private TextView tvValorAposta;
    private TextView tvValorCarrinho;
    private LinearLayout containerComoJogar;
    private MaterialButton btnTeimosinha, btnQuantidadeNumeros, btnQuantidadeTrevos, botaoFavoritar, btnPalpites;
    private List<Integer> qtdPrognosticosQuantidadeNumerosList;
    private List<Integer> qtdPalpitesLotecaList;
    private List<String> labelsPalpitesLoteca;
    private Integer minimoPalpitesParaAtualizar;
    private TextView textSelecionados;
    private FrameLayout frameCarrinho;
    private TextView tvQuantidadeApostas;
    private LinearLayout opcaoOutrosNumerosLayout;
    private boolean mudouTamanhoValor = false;
    String numeroConcurso = "";
    public BarraTituloDTO barraTituloDTO = new BarraTituloDTO();
    private TextView textInfoEtapa;
    private EtapaAposta etapaRenderizada;
    private boolean renderizacaoSurpresinhaTime;
    private ConstraintLayout simulaToolbar;
    private ConstraintLayout lotecaToolbar;
    private SurpresinhaApostaModel surpresinhaModel = new SurpresinhaApostaModel(this);
    private final ActivityResultLauncher<Intent> fluxoApostaLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode()
                                == ResultadoNavegacaoAposta.RESULT_RESET) {

                            resetarTelaAposRetornoDeActivity();
                        }
                    }
            );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simula_aposta);
        init();
        onBackPressedDispatcher();
    }
    private void onBackPressedDispatcher(){
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        InputUtils.closeKeyboard(
                                LotecaActivity.this
                        );

                        SimulaUiState state =
                                viewModel.getUiState().getValue();

                        if (state != null
                                && state.isEscolhaTimeCoracaoSurpresinha()) {

                            viewModel.onEvent(
                                    new SimulaUiEvent.VoltarParaSurpresinha()
                            );
                            return;
                        }

                        if (possuiApostaEmAndamento()) {
                            exibirDialogConfirmacaoSaida();
                            return;
                        }

                        finish();
                    }
                }
        );
    }

    protected void init() {
        model = new SimularApostaModel(this);

        getExtras();
        numeroConcurso = parametroSimulacao.getConcurso().getNumero().toString();

        estilo = new EstiloModalidadeMKP(tipoJogo);

        setViews();

        viewModel = new ViewModelProvider(this).get(LotecaViewModel.class);
        observeState();
        if(frameCarrinho != null){
            AccessibilityUtils.setBotao(frameCarrinho);
        }
        textSelecionados.setVisibility(View.GONE);

        estilo = new EstiloModalidadeMKP(tipoJogo);

        zerarApostas();

        inicializarTela();
    }
    private void inicializarTela(){
        viewModel.onEvent(new SimulaUiEvent.TelaInicializada(tipoJogo, isEspecial, parametroSimulacao));

        viewModel.onEvent(
                new SimulaUiEvent.InicializarParametrosAposta(
                        parametroSimulacao.getQuantidadeMinima() != null
                                ? parametroSimulacao.getQuantidadeMinima()
                                : 0,
                        parametroSimulacao.getTeimosinhas() != null
                                ? parametroSimulacao.getTeimosinhas().get(0)
                                : 0
                ));

        inicializarEstadoTeimosinhas();
        inicializarEstadoQuantidadeNumeros();
        etapaRenderizada = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lottieManager != null) {
            lottieManager.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lottieManager != null) {
            lottieManager.pause();
        }
    }

    private void setViews() {
        this.toolBarLoteca = findViewById(R.id.toolBarLoteca);
        this.botaoLimparAposta = findViewById(R.id.btnLimpar);
        this.botaoCompletar = findViewById(R.id.btnCompletar);
        this.botaoAdicionar = findViewById(R.id.btnAdicionarCarrinho);
        this.labelValueJogosLoteca = findViewById(R.id.txtPartidas);
        this.labelValueSimplesLoteca = findViewById(R.id.txtSimples);
        this.labelValueDuplasLoteca = findViewById(R.id.txtDuplas);
        this.labelValueTriplasLoteca = findViewById(R.id.txtTriplas);
        this.tvValorAposta = findViewById(R.id.tvValorAposta);
        this.tvValorCarrinho = findViewById(R.id.tvTotalCarrinho);
        this.containerComoJogar = findViewById(R.id.containerComoJogarLoteca);
        this.btnQuantidadeNumeros = findViewById(R.id.btnQuantidadeNumeros);
        this.btnQuantidadeTrevos = findViewById(R.id.btnQuantidadeTrevos);
        this.btnTeimosinha = findViewById(R.id.btnTeimosinha);
        this.btnPalpites = findViewById(R.id.btnQuantidadePalpites);
        this.textSelecionados = findViewById(R.id.textSelecionados);
        this.frameCarrinho = findViewById(R.id.frameCarrinho);
        this.tvQuantidadeApostas = findViewById(R.id.tvQuantidadeApostas);
        this.botaoFavoritar = findViewById(R.id.favoritar_aposta);
        this.botaoNextStep = findViewById(R.id.btnNextStep);
        this.opcaoOutrosNumerosLayout = findViewById(R.id.opcaoOutrosNumerosLayout);
        this.simulaToolbar = findViewById(R.id.simulaToolbar);
        this.lotecaToolbar = findViewById(R.id.lotecaToolbar);
        simulaToolbar.setVisibility(View.GONE);
        lotecaToolbar.setVisibility(View.VISIBLE);
        textInfoEtapa = findViewById(R.id.textInfoEtapa);
        opcaoOutrosNumerosLayout.setVisibility(View.GONE);
    }


    private void observeState() {
        viewModel.getUiState().observe(this, this::render);
        viewModel.getUiState().observe(this, this::triggerAnalytics);
    }

    private void renderAnimacao(
            SimulaUiState state
    ) {
        AnimacaoEffect comando =
                state.getComandoAnimacao();

        if (comando == null) {
            return;
        }

        if (comando instanceof
                AnimacaoEffect.Exibir exibir) {

            if (lottieManager != null) {
                lottieManager.destroy();
            }

            lottieManager =
                    new LottieManager(
                            this,
                            exibir.getAnimacaoRes()
                    );

            lottieManager.setSpeed(
                    exibir.getVelocidade()
            );

            lottieManager.start();
        }

        viewModel.onEvent(
                new SimulaUiEvent
                        .ComandoAnimacaoConsumido()
        );
    }

    private void sincronizarFragmento(
            SimulaUiState state
    ) {

        EtapaAposta etapaAtual =
                state.getEtapaAposta();

        if (etapaAtual == null) {
            etapaAtual = EtapaAposta.NUMEROS;
        }

        boolean escolhaTimeSurpresinha =
                state.isEscolhaTimeCoracaoSurpresinha();

        if (etapaRenderizada == etapaAtual
                && renderizacaoSurpresinhaTime == escolhaTimeSurpresinha) {
            return;
        }

        renderizacaoSurpresinhaTime =
                escolhaTimeSurpresinha;


        etapaRenderizada = etapaAtual;

        renderEtapaAtual(
                etapaAtual,
                state
        );
    }

    private void render(SimulaUiState state) {
        sincronizarFragmento(state);
        renderAnimacao(state);
        barraTituloDTO = state.getBarraTituloDTO();
        if (barraTituloDTO != null) {
            renderCabecalho(barraTituloDTO);
        }
        renderParametrosAposta(state);
        renderCarrinho(state);
        renderComandoCarrinho(state.getComandoCarrinho());
        renderDialogComoJogar(state);
        renderAbrirCarrinho(state);
        renderComandoTelaSimula(state);
        renderEstadoBotoes(state);
        renderValorAposta(state);
        renderInfoEtapa(state);
        renderQtdApostas(state);
        renderDialogAumentosPalpitesLoteca(state);
    }

    private void renderInfoEtapa(
            SimulaUiState state
    ) {
        if (textInfoEtapa == null) {
            return;
        }
        boolean ocultarInfo =
                state.isSurpresinhaHabilitada();

        boolean exibirInfoEtapa = state.getExibirInfoEtapa();

        textInfoEtapa.setVisibility(
                !ocultarInfo && exibirInfoEtapa
                        ? View.VISIBLE
                        : View.GONE
        );

        if (!TextUtils.isEmpty(state.getTextoInfoEtapa())) {
            textInfoEtapa.setText(state.getTextoInfoEtapa());
            return;
        }

        String texto;
        switch (state.getEtapaAposta()) {
            case TREVOS,MES_SORTE,TIME_CORACAO:
                texto = "2/2";
                break;
            case NUMEROS:
            default:
                texto = "1/2";
                break;
        }

        textInfoEtapa.setText(texto);
    }

    private void triggerAnalytics(
            SimulaUiState state
    ) {

        AnalyticsEffect comando =
                state.getComandoAnalytics();

        if (comando == null) {
            return;
        }

        if (comando
                instanceof AnalyticsEffect
                .QuantidadeNumerosConfirmada c) {

            AnalyticsHelper.getInstance().logSelectContent(
                    AnalyticsHelper.Tela.MONTAR_APOSTA,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                    AnalyticsHelper.ContentCategoryParams.CONFIG,
                    c.getModalidade(),
                    c.getValorSelecionado()
            );
        } else if (comando
                instanceof AnalyticsEffect
                .TeimosinhaConfirmada c) {


            AnalyticsHelper.getInstance().logSelectContent(
                    AnalyticsHelper.Tela.MONTAR_APOSTA,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                    AnalyticsHelper.ContentCategoryParams.CONFIG,
                    c.getModalidade(),
                    c.getValorSelecionado()
            );
        }
        else if (comando
                instanceof AnalyticsEffect
                .AdicionarCarrinho) {

            logAdicionarAoCarrinho();
        }
        else if(comando instanceof  AnalyticsEffect.QtdSurpresinhas c){

            AnalyticsHelper.getInstance().logSelectContent(
                    AnalyticsHelper.Tela.MONTAR_APOSTA,
                    AnalyticsHelper.JourneyParams.APOSTAR,
                    AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                    AnalyticsHelper.ContentCategoryParams.CONFIG,
                    ModalidadeEnum.fromString(tipoJogo),
                    String.format(Locale.getDefault(), "%d", c.getQtdSurpresinhas())
            );
        }

        viewModel.onEvent(
                new SimulaUiEvent.ComandoAnalyticsConsumido()
        );
    }

    private void renderValorAposta(SimulaUiState state) {

        BigDecimal valor = state.getValorAposta() != null ? state.getValorAposta() : BigDecimal.ZERO;

        if (state.isValorGrande()) {

            if (!mudouTamanhoValor) {
                TextViewUtils.mudarTamanhoPorPorcentagem(tvValorAposta, -30);

                mudouTamanhoValor = true;
            }

        } else {

            if (mudouTamanhoValor) {
                TextViewUtils.mudarTamanhoPorPorcentagem(tvValorAposta, 30);

                mudouTamanhoValor = false;
            }
        }

        ViewUtils.setMoedaFormatHtml(valor, tvValorAposta);

    }

    private void renderQtdApostas(SimulaUiState state) {
        this.setLabelValueJogosLoteca(String.valueOf(state.getJogosLoteca()));
        this.setLabelValueSimplesLoteca(String.valueOf(state.getSimplesLoteca()));
        this.setLabelValueDuplasLoteca(String.valueOf(state.getDuplasLoteca()));
        this.setLabelValueTriplasLoteca(String.valueOf(state.getTriplasLoteca()));
    }

    private void renderDialogAumentosPalpitesLoteca(SimulaUiState state) {
        if (state == null || !state.isAvisouAumentoPalpitesLoteca()) {
            return;
        }

        DialogUtils.dialogEntendi(
                this,
                getString(R.string.msg_qtd_max_loteca)
        );

        // Reset do flag após mostrar o dialog
        viewModel.onEvent(
                new SimulaUiEvent.ResetarAvisoPalpitesLoteca()
        );
    }

    private void renderEstadoBotoes(SimulaUiState state) {
        boolean isLoteca = state.getTipoJogo() == ModalidadeEnum.LOTECA;


        boolean mostrarLimpar =
                true;

        boolean mostrarAdicionar =
                true;

        if(btnTeimosinha != null){

            btnTeimosinha.setVisibility(
                    View.GONE
            );
        }
        if(btnPalpites != null){

            btnPalpites.setVisibility(
                    isLoteca
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if (btnQuantidadeNumeros != null) {

            btnQuantidadeNumeros.setVisibility(
                   View.GONE
            );
        }

        if (btnQuantidadeTrevos != null) {

            btnQuantidadeTrevos.setVisibility(
                    View.GONE
            );

        }


        if (botaoNextStep != null) {
            botaoNextStep.setVisibility(View.GONE);
        }

        if (botaoAdicionar != null) {
            botaoAdicionar.setVisibility(mostrarAdicionar ? View.VISIBLE : View.GONE);

            botaoAdicionar.setEnabled(state.isBotaoAdicionarHabilitado());

            if (botaoAdicionar instanceof MaterialButton botaoAdd) {
                visualModalidadeBotao(state.isBotaoAdicionarHabilitado(), botaoAdd);
                fonteBotaoBold(state.isBotaoAdicionarHabilitado(), botaoAdd);
            }
        }

        if (botaoFavoritar != null) {
            botaoFavoritar.setVisibility(View.GONE);
        }

        if (botaoCompletar != null) {
            botaoCompletar.setVisibility(View.VISIBLE);

            botaoCompletar.setEnabled(state.isBotaoCompletarHabilitado());
            botaoCompletar.setText(
                    state.getTextoCompletar()
            );
        }

        if (botaoLimparAposta != null) {
            botaoLimparAposta.setVisibility(mostrarLimpar ? View.VISIBLE : View.GONE);
            botaoLimparAposta.setEnabled(state.isBotaoLimparHabilitado());
        }
    }
    private void fonteBotaoBold(boolean habilitado, MaterialButton button){
        if(habilitado){
            button.setTypeface(
                    ResourcesCompat.getFont(
                            this,
                            R.font.caixa_std_semi_bold
                    )
            );
        }
        else {
            button.setTypeface(
                    ResourcesCompat.getFont(
                            this,
                            R.font.caixa_std_regular
                    )
            );
        }
    }

    private void visualModalidadeBotao(boolean habilitado, MaterialButton button) {
        if (habilitado) {

            button.setBackgroundTintList(ContextCompat.getColorStateList(this, estilo.getCorClara()));

            button.setTextColor(ContextCompat.getColor(this, estilo.getCorFonteFundoClaro()));

            button.setStrokeColor(ContextCompat.getColorStateList(this, estilo.getCorClara()));

        } else {


            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.disabled)));

            button.setTextColor(ContextCompat.getColorStateList(this, R.color.text_rodape));

            button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cinza70)));
        }
        button.setStateListAnimator(null);
        button.setElevation(0f);
        button.setTranslationZ(0f);
    }

    private void renderComandoTelaSimula(SimulaUiState state) {
        ScreenSimulaEffect effect = state.getComandoTelaSimula();

        if (effect == null) {
            return;
        }

        if (effect instanceof ScreenSimulaEffect.AbrirSurpresinha) {


            executarAbrirSurpresinha();

            viewModel.onEvent(new SimulaUiEvent.ComandoTelaSimulaConsumido());

            return;
        }
        if (effect instanceof ScreenSimulaEffect.AtualizarQuantidadeTrevos atualizarTrevos) {

            EtapaFragment etapa =
                    getEtapaAtual();

            if (etapa != null) {

                etapa.atualizarQuantidadeTrevos(
                        atualizarTrevos.getValorSelecionado()
                );

            }

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoTelaSimulaConsumido()
            );

            return;
        }

        if (effect instanceof ScreenSimulaEffect.VoltarEtapa voltarEtapa) {

            etapaRenderizada = null;

            renderEtapaAtual(
                    voltarEtapa
                            .getEtapa(),
                    state
            );

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoTelaSimulaConsumido()
            );

        }
    }

    private void renderEtapaAtual(
            EtapaAposta etapa,
            SimulaUiState state
    ) {

        switch (etapa) {

            case TREVOS:
                trevosFragment(state.getDezenasSelecionadas());
                break;

            case MES_SORTE:
                mesDeSorteFragment(state.getDezenasSelecionadas());
                break;

            case TIME_CORACAO:
                if (state.isEscolhaTimeCoracaoSurpresinha()) {
                    timesFragmentSurpresinha();
                } else {
                    timesFragment(state.getDezenasSelecionadas());
                }
                break;

            default:
                timesFragment = null;

                abrirTelaCartela();

                break;
        }
    }

    private void abrirTelaSurpresinha() {
        FragmentUtils.startFragmentAllowingStateLoss(fragmentManager, R.id.fragmentSimularApostas, surpresinhaFragment);
    }
    private void timesFragmentSurpresinha() {
        timesFragment =
                TimesFragment.newInstance(
                        parametroSimulacao,
                        new ArrayList<>(),
                        true
                );

        FragmentUtils.startFragmentAllowingStateLoss(
                fragmentManager,
                R.id.fragmentSimularApostas,
                timesFragment
        );
    }

    private void executarAbrirSurpresinha() {

        if (surpresinhaFragment == null) {
            return;
        }

        surpresinhaFragment.setArguments(new Bundle());

        abrirTelaSurpresinha();

    }

    private void renderDialogComoJogar(SimulaUiState state) {

        if (!state.isExibirDialogComoJogar()) {
            return;
        }

        DialogUtils.dialogHtml(this, state.getTitleComoJogar(), state.getMensagemComoJogar());

        viewModel.onEvent(new SimulaUiEvent.DialogComoJogarConsumido());
    }

    private void renderParametrosAposta(SimulaUiState state) {
        if (btnPalpites == null || state == null) {
            return;
        }

        btnPalpites.setText(
                state.getPalpitesLoteca() + " palpites"
        );
    }

    private void renderComandoCarrinho(CarrinhoEffect comando) {

        if (comando == null) {
            return;
        }

        if (comando instanceof CarrinhoEffect.MostrarConfirmacaoLimiteDiario) {

            DialogUtils.dialogSim(this, getString(R.string.MA014), (dialog, which) -> viewModel.onEvent(new SimulaUiEvent.ConfirmarAdicionarCarrinho()));

            viewModel.onEvent(new SimulaUiEvent.ComandoCarrinhoConsumido());

            return;
        }

        if (comando instanceof CarrinhoEffect.ExecutarAdicionarCarrinhoEffect executarAdd) {

            model.adicionaApostaNoCarrinho(this, executarAdd.getAposta(), executarAdd.getBarraTituloDTO());

            viewModel.onEvent(new SimulaUiEvent.ComandoCarrinhoConsumido());

            return;
        }
        if (comando instanceof
                CarrinhoEffect.AdicionarSurpresinha adicionarSurpresinha) {

            if (surpresinhaModel == null) {
                surpresinhaModel =
                        new SurpresinhaApostaModel(this);
            }

            surpresinhaModel.addSurpresinhaCarrinho(
                    adicionarSurpresinha
                            .getDto()
            );

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoCarrinhoConsumido()
            );

            return;
        }


        if (comando instanceof CarrinhoEffect.AdicionarTimeSurpresa timeSurpresa) {

            adicionarTimeSurpresa(timeSurpresa.getEquipeSelecionada());

            viewModel.onEvent(new SimulaUiEvent.ComandoCarrinhoConsumido());
        }
    }

    private void renderCabecalho(BarraTituloDTO cabecalho) {

        FragmentUtils.startCabecalhoSimulacao(getSupportFragmentManager(), R.id.id_fg_barra_titulo, tipoJogo, cabecalho.getNumeroConcurso(), cabecalho.getDataSorteio(), cabecalho.isEspecial(), getCorBackgroundBarraTitulo(), true);
    }

    private int getCorBackgroundBarraTitulo() {
        return estilo.getCorClara();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //TODO: MEGA 30 ANOS//
            if (extras.containsKey(APOSTA_EXTRA)) {
                this.aposta = ((IdentificaoDeUmaApostaDas8Modalidades) extras.getSerializable(APOSTA_EXTRA));
            }
            if (extras.containsKey("NUMERO_CONCURSO")) {
                this.valNumeroConcurso = extras.getInt("NUMERO_CONCURSO");
            }
            isEspecial = extras.getBoolean(getResources().getString(R.string.extra_especial), false);
            tipoJogo = (ModalidadeEnum) extras.getSerializable(getResources().getString(R.string.tipoAposta));
            parametroSimulacao = new Gson().fromJson(extras.getString(getResources().getString(R.string.extra_modalidade)), ParametroJogoDTO.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsHelper.getInstance().logViewScreenAposta(
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                AnalyticsHelper.Tela.MONTAR_APOSTA,
                ModalidadeEnum.fromString(tipoJogo),
                numeroConcurso
        );

        viewModel.onEvent(
                new SimulaUiEvent.CarregarCarrinho()
        );
    }

    public void zerarApostas() {
        qtdNumerosList = new ArrayList<>();
        qtdPrognosticosQuantidadeNumerosList = new ArrayList<>();
        qtdPalpitesLotecaList = new ArrayList<>();
        labelsPalpitesLoteca = new ArrayList<>();
        qtdConcursosList = new ArrayList<>();
        configuraParametrosJogo();
        configuraFragments();
        configuraActivity();
        zerarApostasLoteca();
    }

    public void zerarApostasLoteca() {
        this.setLabelValueJogosLoteca(Constantes.ZERO_STRING);
        this.setLabelValueSimplesLoteca(Constantes.ZERO_STRING);
        this.setLabelValueDuplasLoteca(Constantes.ZERO_STRING);
        this.setLabelValueTriplasLoteca(Constantes.ZERO_STRING);
        EtapaFragment etapa = getEtapaAtual();
        if(etapa != null){
            etapa.onLimparRodapeClicado();
        }
    }


    private void renderAbrirCarrinho(SimulaUiState state) {

        if (!state.isAbrirCarrinho()) {
            return;
        }

        abrirCarrinho();

        viewModel.onEvent(new SimulaUiEvent.AbrirCarrinhoConsumido());
    }

    private void abrirCarrinho() {

        AlertDialogUtils.show(this);

        ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {

            @Override
            public void onResponse(CarrinhoDTOResponse response) {

                AlertDialogUtils.dismiss();

                Bundle args = new Bundle();

                args.putSerializable(CarrinhoActivity.CARRINHO, response.getPayload());
                Intent intent = new Intent(LotecaActivity.this, CarrinhoActivity.class).putExtras(args);
                abrirFluxoApostaComResultado(intent);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                AlertDialogUtils.dismiss();

                if (MensagensNetwork.isUnauthorizedError(error)) {
                    Intent intent = new Intent(LotecaActivity.this, CarrinhoActivity.class).putExtra(CarrinhoActivity.LER_CARRINHO_LOCAL, true);
                    abrirFluxoApostaComResultado(intent);

                } else {

                    RedirectNetwork.checkRedirect(error, LotecaActivity.this);
                }
            }
        });
    }


    public ParametroJogoDTO getParametroSimulacao() {
        return parametroSimulacao;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public int verificaNovoQtdDezenasMax(int qtdDezenasAtual) {

        if (parametroSimulacao == null || parametroSimulacao.getQuantidadeMaxima() == null) {
            return qtdDezenasAtual;
        }

        int quantidadeMaxima = parametroSimulacao.getQuantidadeMaxima();

        return Math.min(qtdDezenasAtual + 1, quantidadeMaxima);
    }

    @Override
    public void adicionarTimeSurpresa(ParametroEquipe equipeSelecionada) {
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.extra_equipe_selecionada), (new Gson()).toJson(equipeSelecionada));
        surpresinhaFragment.setArguments(bundle);
        abrirTelaSurpresinha();
    }

    @Override
    public void escolherOutroTimeSurpresinha() {
        // Esse método é chamado quando o usuário escolhe outro time na tela de surpresinha. Ele apenas abre a tela de surpresinha novamente.
    }

    public void setLabelValueJogosLoteca(String labelValueJogosLoteca) {
        this.labelValueJogosLoteca.setText(labelValueJogosLoteca);
    }

    public void setLabelValueSimplesLoteca(String labelValueSimplesLoteca) {
        this.labelValueSimplesLoteca.setText(labelValueSimplesLoteca);
    }

    public void setLabelValueDuplasLoteca(String labelValueDuplasLoteca) {
        this.labelValueDuplasLoteca.setText(labelValueDuplasLoteca);
    }

    public void setLabelValueTriplasLoteca(String labelValueTriplasLoteca) {
        this.labelValueTriplasLoteca.setText(labelValueTriplasLoteca);
    }


    public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
        return aposta;
    }

    public BigDecimal getValorTotalApostaAtual() {
        if (viewModel == null) {
            return BigDecimal.ZERO;
        }
        SimulaUiState state = viewModel.getUiState().getValue();
        if (state == null || state.getValorAposta() == null) {
            return BigDecimal.ZERO;
        }
        return state.getValorAposta();
    }

    public void mesDeSorteFragment(List<Integer> dezenasSelecionadas) {
        FragmentUtils.startMesesFragment2(fragmentManager, R.id.fragmentSimularApostas, parametroSimulacao, (ArrayList<Integer>) dezenasSelecionadas);
    }

    public void trevosFragment(List<Integer> dezenasSelecionadas) {
        FragmentUtils.startTrevosFragment2(fragmentManager, R.id.fragmentSimularApostas, parametroSimulacao, (ArrayList<Integer>) dezenasSelecionadas);
    }
    public void timesFragment(List<Integer> dezenasSelecionadas) {
        timesFragment = FragmentUtils.startTimesFragment(fragmentManager, R.id.fragmentSimularApostas, parametroSimulacao, (ArrayList<Integer>) dezenasSelecionadas);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ResultadoNavegacaoAposta.RESULT_RESET) {
            resetarTelaAposRetornoDeActivity();
        }
    }

    private void configuraParametrosJogo() {
        if (parametroSimulacao != null && parametroSimulacao.getValoresAposta() != null) {
            for (ParametroValorApostaDTO parametro : parametroSimulacao.getValoresAposta()) {

                if (tipoJogo == ModalidadeEnum.MAIS_MILIONARIA) {
                    if (parametro.getNumeroTrevos() != null && parametroSimulacao.getTrevos() != null && parametroSimulacao.getTrevos().getQtdMinima() != null && parametro.getNumeroTrevos().equals(parametroSimulacao.getTrevos().getQtdMinima())) {

                        qtdNumerosList.add(getResources().getString(R.string.string_vazia) + parametro.getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros_por_espaco) + ViewUtils.getMoedaFormat(parametro.getValor()));

                        qtdPrognosticosQuantidadeNumerosList.add(parametro.getNumeroPrognosticos());
                    }
                } else {
                    qtdNumerosList.add(getResources().getString(R.string.string_vazia) + parametro.getNumeroPrognosticos() + getResources().getString(R.string.espaco_numeros_por_espaco) + ViewUtils.getMoedaFormat(parametro.getValor()));

                    qtdPrognosticosQuantidadeNumerosList.add(parametro.getNumeroPrognosticos());
                }
            }

            if (parametroSimulacao.getTeimosinhas() != null) {
                for (Integer numeroTeimosinha : parametroSimulacao.getTeimosinhas()) {
                    if (numeroTeimosinha == 0) {
                        qtdConcursosList.add("Nenhum concurso");
                    } else if (numeroTeimosinha == 1) {
                        qtdConcursosList.add(numeroTeimosinha + getResources().getString(R.string.espaco_concurso));
                    } else {
                        qtdConcursosList.add(numeroTeimosinha + getResources().getString(R.string.espaco_concursos));
                    }
                }
            }
        }

        for (int i = 15; i <= 26; i++) {
            qtdPalpitesLotecaList.add(i);
            labelsPalpitesLoteca.add(i + " palpites");
        }
    }

    private void inicializarEstadoQuantidadeNumeros() {
        viewModel.onEvent(new SimulaUiEvent.InicializarQuantidadeNumeros(qtdNumerosList, qtdPrognosticosQuantidadeNumerosList, getQtdDezenasSelecionadaState()));
    }

    private void inicializarEstadoTeimosinhas() {
        List<Integer> qntConcursos = new ArrayList<>();

        if (parametroSimulacao != null && parametroSimulacao.getTeimosinhas() != null) {
            qntConcursos.addAll(parametroSimulacao.getTeimosinhas());
        }

        viewModel.onEvent(new SimulaUiEvent.InicializarTeimosinhas(qtdConcursosList, qntConcursos, getQtdConcursoState(), isEspecial, getString(R.string.label_sem_teimosinha)));
    }

    private void configuraFragments() {

        fragmentManager = getSupportFragmentManager();

        cartelaFragment = new LotecaFragment();

        Bundle bundle = new Bundle();

        cartelaFragment.setArguments(bundle);

        surpresinhaFragment =
                FragmentUtils.getSurpresinhaFragment2();
    }

    private void configuraActivity() {
        configuraTipoApostaTela(R.color.loteca_claro_mkp, R.color.loteca_escuro_mkp, View.GONE);

        String dataSorteioAtual = parametroSimulacao.getConcurso().getDataSorteio();

        // Passando um link
        Bundle bundle = new Bundle();
        bundle.putInt(getResources().getString(R.string.extra_qtd_dezenas_possiveis_selecionado), getQtdDezenasSelecionadaState());
        bundle.putString(getResources().getString(R.string.extra_data_sorteio_atual), dataSorteioAtual);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_light), typeGameColorLight);
        bundle.putSerializable(getResources().getString(R.string.extra_type_game_color_Dark), typeGameColorDark);
        bundle.putInt("typeGameColorLight", typeGameColorLight);
        bundle.putInt("typeGameColorDark", typeGameColorDark);
        bundle.putInt(COR_FONTE_FUNDO_BRANCO, estilo.getCorItemDezena());
        bundle.putInt(COR_FONTE_FUNDO_CLARO, estilo.getCorFonteFundoClaro());
        bundle.putInt(COR_FONTE_FUNDO_ESCURO, estilo.getCorFonteFundoEscuro());

        cartelaFragment.cartelaFragmentBuilder(bundle);

        configuraBotoesListeners();
    }

    public void configuraBotoesListeners() {
        btnTeimosinha.setOnClickListener(
                v -> viewModel.onEvent(new SimulaUiEvent.ClicarTeimosinhas())
        );

        if (btnPalpites != null) {
            btnPalpites.setOnClickListener(
                    v -> abrirDialogQuantidadePalpites(null)
            );
        }

        frameCarrinho.setOnClickListener(
                v -> viewModel.onEvent(new SimulaUiEvent.CarrinhoClicado())
        );

        if (botaoCompletar != null) {
            botaoCompletar.setOnClickListener(
                    v -> {

                        if (!verificaAberto(this)) {
                            return;
                        }

                        EtapaFragment etapa = getEtapaAtual();
                        if(etapa != null){
                            etapa.onCompletarRodapeClicado();
                        }
                    }
            );
        }
        if (containerComoJogar != null) {
            AccessibilityUtils.setBotao(containerComoJogar);
            containerComoJogar.setOnClickListener(view -> viewModel.onEvent(new SimulaUiEvent.ComoJogarClicado()));
        }

        if (botaoAdicionar != null) {
            botaoAdicionar.setOnClickListener(
                    v -> {
                        EtapaFragment etapa = getEtapaAtual();
                        if(etapa != null){
                            etapa.onAdicionarRodapeClicado();
                        }
                    }
            );
        }

        if (botaoLimparAposta != null) {
            botaoLimparAposta.setOnClickListener(
                    v -> {
                        EtapaFragment etapa = getEtapaAtual();
                        if(etapa != null){
                            etapa.onLimparRodapeClicado();
                        }
                    }
            );
        }

    }

    public void abrirDialogQuantidadePalpitesParaAumentar(
            int palpitesAtuais
    ) {
        abrirDialogQuantidadePalpites(
                palpitesAtuais
        );
    }

    private void abrirDialogQuantidadePalpites(
            Integer minimoParaAtualizar
    ) {
        if (labelsPalpitesLoteca == null || labelsPalpitesLoteca.isEmpty()) {
            return;
        }

        minimoPalpitesParaAtualizar = minimoParaAtualizar;

        DialogUtils.showDialogListItensNovo(
                this,
                getString(R.string.quantidade_de_palpites),
                null,
                "Escolha em quantos palpites você quer jogar:",
                labelsPalpitesLoteca,
                "Confirmar",
                "Cancelar",
                onQuantidadePalpitesDialogListener()
        );
    }

    private OnDialogListener onQuantidadePalpitesDialogListener() {
        return new OnDialogListener() {
            @Override
            public void itemSelecionado(int position) {
                // Empty implementation, as the selection is handled in the 'ok' method.
            }

            @Override
            public void ok(int position) {
                if (qtdPalpitesLotecaList == null
                        || position < 0
                        || position >= qtdPalpitesLotecaList.size()) {
                    minimoPalpitesParaAtualizar = null;
                    return;
                }

                int novoValor =
                        qtdPalpitesLotecaList.get(position);

                if (minimoPalpitesParaAtualizar != null
                        && novoValor <= minimoPalpitesParaAtualizar) {
                    minimoPalpitesParaAtualizar = null;
                    return;
                }

                SimulaUiState stateAtual =
                        viewModel != null
                                ? viewModel.getUiState().getValue()
                                : null;

                int valorAtualPalpites =
                        stateAtual != null
                                ? stateAtual.getPalpitesLoteca()
                                : novoValor;

                viewModel.onEvent(
                        new SimulaUiEvent.PalpitesLotecaAtualizado(
                                novoValor
                        )
                );

                if (novoValor < valorAtualPalpites) {
                    viewModel.onEvent(
                            new SimulaUiEvent.LimparApostaSolicitado()
                    );
                }

                minimoPalpitesParaAtualizar = null;
            }

            @Override
            public void cancelar() {
                minimoPalpitesParaAtualizar = null;
            }
        };
    }

    private void abrirTelaCartela() {
        fragmentManager.beginTransaction().replace(R.id.fragmentSimularApostas, cartelaFragment).commit();
    }

    private void configuraTipoApostaTela(int lightColor, int darkColor, int visibilityToolBarLoteca) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, lightColor));

        toolBarLoteca.setVisibility(visibilityToolBarLoteca);

        typeGameColorLight = lightColor;
        typeGameColorDark = darkColor;
    }

    private void renderCarrinho(SimulaUiState state) {

        if (tvValorCarrinho != null) {

            ViewUtils.setMoedaFormatHtml(state.getValorCarrinho(), tvValorCarrinho);
        }

        if (tvQuantidadeApostas != null) {

            int quantidade = state.getQuantidadeApostasCarrinho();

            tvQuantidadeApostas.setText(quantidade > 999 ? getString(R.string.mais_999) : String.valueOf(quantidade));
        }
    }

    public BarraTituloDTO getBarraTituloDTO() {
        return barraTituloDTO;
    }
    private int getQtdDezenasSelecionadaState() {
        SimulaUiState state =
                viewModel != null
                        ? viewModel.getUiState().getValue()
                        : null;

        return state != null
                ? state.getQtdDezenasPossiveisSelecionado()
                : 0;
    }

    private int getQtdConcursoState() {
        SimulaUiState state =
                viewModel != null
                        ? viewModel.getUiState().getValue()
                        : null;

        return state != null
                ? state.getQtdConcursoSelecionado()
                : 0;
    }
    private EtapaFragment getEtapaAtual() {

        Fragment fragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.fragmentSimularApostas
                        );

        if (fragment instanceof EtapaFragment fragmento) {
            return fragmento;
        }

        return null;
    }

    private void exibirDialogConfirmacaoSaida() {

        DialogUtils.dialogConfirmar(
                this,
                getString(R.string.text_descartar),
                (dialog, which) -> {

                    setResult(RESULT_CANCELED);

                    finish();
                }
        );
    }
    private boolean possuiApostaEmAndamento() {
        SimulaUiState state =
                viewModel.getUiState().getValue();

        return state != null
                && state.getPossuiAlteracoesPendentes();
    }

    private void resetarTelaAposRetornoDeActivity() {
        zerarApostasLoteca();
        inicializarTela();

        etapaRenderizada = null;
        renderizacaoSurpresinhaTime = false;

        abrirTelaCartela();
        cartelaFragment.zeraLoteca();

        viewModel.onEvent(
                new SimulaUiEvent.CarregarCarrinho()
        );
    }
    public void mostrarPopupApostaAdicionada() {
        DialogUtils.dialogEntendi(
                this,
                getString(R.string.added_bet_to_cart)
        );

        resetarTelaAposRetornoDeActivity();
    }

    public void abrirFluxoApostaComResultado(Intent intent) {
        if (intent == null) {
            return;
        }

        fluxoApostaLauncher.launch(intent);
    }

    private void logAdicionarAoCarrinho(
    ) {
        SimulaUiState state =
                viewModel.getUiState().getValue();

        if (state == null) {
            return;
        }

        AnalyticsHelper.getInstance().logInteraction(
                AnalyticsHelper.EventCategoryParams.CTA,
                AnalyticsHelper.EventActionParams.CLICK,
                AnalyticsHelper.EventLabelParams.ADD_TO_CART,
                AnalyticsHelper.JourneyParams.APOSTAR,
                AnalyticsHelper.SubJourneyParams.APOSTA_SIMPLES,
                AnalyticsHelper.Tela.MONTAR_APOSTA,
                ModalidadeEnum.fromString(
                        tipoJogo
                ),
                numeroConcurso,
                String.valueOf(
                        state.getQuantidadeSurpresinhas()
                ),
                String.valueOf(
                        state.getQtdDezenasPossiveisSelecionado()
                ),
                String.valueOf(
                        state.getQtdConcursoSelecionado()
                ),
                ViewUtils.getMoedaFormat(
                        state.getValorAposta()
                )
        );
    }

    private boolean verificaAberto(Context context) {
        if (parametroSimulacao != null
            && parametroSimulacao.getConcurso() != null
            && parametroSimulacao.getConcurso().getAberto() != null
            && !parametroSimulacao.getConcurso().getAberto()) {

            DialogUtils.dialogEntendi(context, context.getString(R.string.msg_conc_nao_aberto)
                            .replace("{num_concurso}", parametroSimulacao.getConcurso().getNumero().toString())
            );
            return false;
        }
        return true;
    }
}



