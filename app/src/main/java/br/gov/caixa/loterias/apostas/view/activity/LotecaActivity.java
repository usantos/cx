package br.gov.caixa.loterias.apostas.view.activity;

import android.app.Dialog;
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
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CarrinhoActivity;
import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.CarrinhoEffect;
import br.gov.caixa.loterias.apostas.effect.FavoritarApostaEffect;
import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
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
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.AccessibilityUtils;
import br.gov.caixa.loterias.apostas.utils.AlertDialogExperimenteLogarSingleton;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AnalyticsHelper;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
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
import br.gov.caixa.loterias.apostas.view.fragment.EtapaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.LotecaFragment;
import br.gov.caixa.loterias.apostas.view.fragment.SurpresinhaFragment2;
import br.gov.caixa.loterias.apostas.view.fragment.TimesFragment;
import br.gov.caixa.loterias.apostas.view.listener.CartelaFragmentListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogFavoritarListener;
import br.gov.caixa.loterias.apostas.view.listener.SurpresinhaFragmentListener;
import br.gov.caixa.loterias.apostas.viewModel.LotecaViewModel;

public class LotecaActivity extends LoteriasBaseAppActivity implements CartelaFragmentListener, SurpresinhaFragmentListener {

    public static String COR_FONTE_FUNDO_BRANCO = "COR_FONTE_FUNDO_BRANCO";
    public static String COR_FONTE_FUNDO_CLARO = "COR_FONTE_FUNDO_CLARO";
    public static String COR_FONTE_FUNDO_ESCURO = "COR_FONTE_FUNDO_ESCURO";

    public final static String APOSTA_EXTRA = "aposta";
    private int numero_concurso;
    private ConstraintLayout toolBarLoteca;
    private Button botaoLimparAposta, botaoCompletar, botaoAdicionar, botaoNextStep;
    private TextView labelValueJogosLoteca, labelValueSimplesLoteca, labelValueDuplasLoteca, labelValueTriplasLoteca;

    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

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
    private ConstraintLayout simulaToolbar,lotecaToolbar;
    private SurpresinhaApostaModel surpresinhaModel = new SurpresinhaApostaModel(this);
    private final ActivityResultLauncher<Intent> fluxoApostaLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode()
                                == ResultadoNavegacaoAposta.RESULT_RESETAR_SIMULA) {

                            resetarTelaAposRetornoDeActivity();
                        }
                    }
            );

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
        DadosCorporativosSilceBO dadosCorporativosSilceBO = DadosCorporativosSilceBO.getInstance();
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
        this.labelValueJogosLoteca = findViewById(R.id.labelValueJogosLoteca);
        this.labelValueSimplesLoteca = findViewById(R.id.labelValueSimplesLoteca);
        this.labelValueDuplasLoteca = findViewById(R.id.labelValueDuplasLoteca);
        this.labelValueTriplasLoteca = findViewById(R.id.labelValueTriplasLoteca);
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
                AnimacaoEffect.Exibir) {

            AnimacaoEffect.Exibir exibir =
                    (AnimacaoEffect.Exibir)
                            comando;

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
        renderDialogFavoritarAposta(state);
        renderComandoFavoritarAposta(state);
        renderInfoEtapa(state);
    }

    private void renderInfoEtapa(
            SimulaUiState state
    ) {
        if (textInfoEtapa == null) {
            return;
        }
        boolean ocultarInfo =
                state.isSurpresinhaHabilitada();

        textInfoEtapa.setVisibility(
                !ocultarInfo && state.getExibirInfoEtapa()
                        ? View.VISIBLE
                        : View.GONE
        );

        String texto;
        switch (state.getEtapaAposta()) {
            case TREVOS:
            case MES_SORTE:
            case TIME_CORACAO:
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
                .QuantidadeNumerosConfirmada) {

            AnalyticsEffect.QuantidadeNumerosConfirmada c =
                    (AnalyticsEffect.QuantidadeNumerosConfirmada)
                            comando;

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
                .TeimosinhaConfirmada) {

            AnalyticsEffect.TeimosinhaConfirmada c =
                    (AnalyticsEffect.TeimosinhaConfirmada)
                            comando;

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
        else if(comando instanceof  AnalyticsEffect.QtdSurpresinhas){
            AnalyticsEffect.QtdSurpresinhas c =
                    (AnalyticsEffect.QtdSurpresinhas)
                            comando;

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

    private void renderComandoFavoritarAposta(SimulaUiState state) {
        FavoritarApostaEffect comando = state.getComandoFavoritarAposta();

        if (comando == null) {
            return;
        }

        if (comando instanceof FavoritarApostaEffect.Sucesso) {
            AppCenterManager.registraEvento(getResources().getString(R.string.evento_efetuou_adicao_aposta_favorita_sucesso));
            viewModel.onEvent(new SimulaUiEvent.ComandoFavoritarApostaConsumido());
            return;
        }

        if (comando instanceof FavoritarApostaEffect.ConfirmarValidacaoNegocial) {
            FavoritarApostaEffect.ConfirmarValidacaoNegocial confirmar = (FavoritarApostaEffect.ConfirmarValidacaoNegocial) comando;

            DialogUtils.dialogSim(this, confirmar.getMensagem(), (dialog, which) -> viewModel.onEvent(new SimulaUiEvent.ConfirmarSalvarApostaFavorita(confirmar.getApostaFavorita())));

            viewModel.onEvent(new SimulaUiEvent.ComandoFavoritarApostaConsumido());
            return;
        }

        if (comando instanceof FavoritarApostaEffect.RedirecionarErro) {
            FavoritarApostaEffect.RedirecionarErro erro = (FavoritarApostaEffect.RedirecionarErro) comando;

            RedirectNetwork.checkRedirect(erro.getError(), this);

            viewModel.onEvent(new SimulaUiEvent.ComandoFavoritarApostaConsumido());
        }
    }

    private void solicitarSalvarFavoritoDoFragmentAtual(
            String nome
    ) {
        EtapaFragment etapa = getEtapaAtual();

        if (etapa != null) {
            etapa.onSalvarFavoritoRodapeConfirmado(nome);
        }
    }

    private void renderDialogFavoritarAposta(SimulaUiState state) {
        if (!state.isExibirDialogFavoritarAposta()) {
            return;
        }

        Dialog dialog = DialogUtils.buildDialogFavoritar(this, getString(R.string.favoritar_aposta), getString(R.string.exemplo_aposta), getString(R.string.informe_favorita), new OnDialogFavoritarListener() {
            @Override
            public void Confirmar(String nome, boolean manterSurpresinhas) {
                if (TextUtils.isEmpty(nome)) {
                    DialogUtils.dialogEntendi(LotecaActivity.this, getString(R.string.msg_favor_inserir_nome_aposta));
                    return;
                }

                solicitarSalvarFavoritoDoFragmentAtual(nome);
            }

            @Override
            public void Cancelar() {
            }
        }, false);

        if (dialog != null) {
            dialog.show();
        }

        viewModel.onEvent(new SimulaUiEvent.DialogFavoritarApostaConsumido());
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


    private void renderEstadoBotoes(SimulaUiState state) {
        boolean isLoteca = state.getTipoJogo() == ModalidadeEnum.LOTECA;
        EtapaAposta etapaAtual = state.getEtapaAposta() != null ? state.getEtapaAposta() : EtapaAposta.NUMEROS;

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

            if (botaoAdicionar instanceof MaterialButton) {
                visualModalidadeBotao(state.isBotaoAdicionarHabilitado(), (MaterialButton) botaoAdicionar);
                fonteBotaoBold(state.isBotaoAdicionarHabilitado(), (MaterialButton) botaoAdicionar);
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
        if (effect instanceof ScreenSimulaEffect.AtualizarQuantidadeTrevos) {

            ScreenSimulaEffect.AtualizarQuantidadeTrevos comando = (ScreenSimulaEffect.AtualizarQuantidadeTrevos) effect;

            EtapaFragment etapa =
                    getEtapaAtual();

            if (etapa != null) {

                etapa.atualizarQuantidadeTrevos(
                        comando.getValorSelecionado()
                );

            }

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoTelaSimulaConsumido()
            );

            return;
        }

        if (effect instanceof ScreenSimulaEffect.VoltarEtapa) {

            etapaRenderizada = null;

            renderEtapaAtual(
                    ((ScreenSimulaEffect.VoltarEtapa) effect)
                            .getEtapa(),
                    state
            );

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoTelaSimulaConsumido()
            );

            return;
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

        if (comando instanceof CarrinhoEffect.ExecutarAdicionarCarrinhoEffect) {

            CarrinhoEffect.ExecutarAdicionarCarrinhoEffect add = (CarrinhoEffect.ExecutarAdicionarCarrinhoEffect) comando;
            model.adicionaApostaNoCarrinho(this, add.getAposta(), add.getBarraTituloDTO());

            viewModel.onEvent(new SimulaUiEvent.ComandoCarrinhoConsumido());

            return;
        }
        if (comando instanceof
                CarrinhoEffect.AdicionarSurpresinha) {

            if (surpresinhaModel == null) {
                surpresinhaModel =
                        new SurpresinhaApostaModel(this);
            }

            surpresinhaModel.addSurpresinhaCarrinho(
                    ((CarrinhoEffect.AdicionarSurpresinha)
                            comando)
                            .getDto()
            );

            viewModel.onEvent(
                    new SimulaUiEvent.ComandoCarrinhoConsumido()
            );

            return;
        }


        if (comando instanceof CarrinhoEffect.AdicionarTimeSurpresa) {

            CarrinhoEffect.AdicionarTimeSurpresa timeSurpresa = (CarrinhoEffect.AdicionarTimeSurpresa) comando;

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
                System.out.println("teste aposta " + aposta);
            }
            if (extras.containsKey("NUMERO_CONCURSO")) {
                this.numero_concurso = extras.getInt("NUMERO_CONCURSO");
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
        setColorValuesLoteca();
        EtapaFragment etapa = getEtapaAtual();
        if(etapa != null){
            etapa.onLimparRodapeClicado();
        }
    }

    private void executarFavoritarRodape() {
        if (!DadosUsuarioBO.checarUsuarioLogado(this)) {
            AlertDialogExperimenteLogarSingleton.show(this, false, null);
            return;
        }

        viewModel.onEvent(new SimulaUiEvent.FavoritarApostaClicado());
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

    public void setColorValuesLoteca() {
        this.labelValueJogosLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
        this.labelValueSimplesLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
        this.labelValueDuplasLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
        this.labelValueTriplasLoteca.setTextColor(ContextCompat.getColor(this,estilo.getCorClara()));
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

    public TextView getLabelValueSimplesLoteca() {
        return labelValueSimplesLoteca;
    }

    public TextView getLabelValueJogosLoteca() {
        return labelValueJogosLoteca;
    }

    public TextView getLabelValueDuplasLoteca() {
        return labelValueDuplasLoteca;
    }

    public TextView getLabelValueTriplasLoteca() {
        return labelValueTriplasLoteca;
    }

    public Button getBotaoLimparAposta() {
        return botaoLimparAposta;
    }

    public Button getBotaoCompletar() {
        return botaoCompletar;
    }

    public TextView getValorApostaCartela() {
        return tvValorAposta;
    }

    public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
        return aposta;
    }

    public BigDecimal getValorTotalApostaAtual() {

        if (viewModel == null || viewModel.getUiState() == null || viewModel.getUiState().getValue() == null || viewModel.getUiState().getValue().getValorAposta() == null) {

            return BigDecimal.ZERO;
        }

        return viewModel.getUiState().getValue().getValorAposta();
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
        if (resultCode == ResultadoNavegacaoAposta.RESULT_RESETAR_SIMULA) {
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
        configuraTipoApostaTela(getResources().getString(R.string.underline_loteca_underline), R.color.loteca_claro_mkp, R.color.loteca_escuro_mkp, View.GONE, View.VISIBLE);

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

        DialogUtils.showDialogListItens(
                this,
                getString(R.string.quantidade_de_palpites),
                null,
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

                viewModel.onEvent(
                        new SimulaUiEvent.PalpitesLotecaAtualizado(
                                novoValor
                        )
                );

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

    private void configuraTipoApostaTela(String titleActivity, int lightColor, int darkColor, int visibilityToolBarSimularAposta, int visibilityToolBarLoteca) {
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

        if (fragment instanceof EtapaFragment) {
            return (EtapaFragment) fragment;
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
}